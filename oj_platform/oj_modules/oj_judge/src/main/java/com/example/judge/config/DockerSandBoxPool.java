package com.example.judge.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.example.common.entity.constants.JudgeConstants;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class DockerSandBoxPool {

    private DockerClient dockerClient;

    private String sandboxImage;

    private String volumeDir;

    private Long memoryLimit;

    private Long memorySwapLimit;

    private Long cpuLimit;

    private int poolSize;

    // docker名称前缀
    private String containerNamePrefix;

    private BlockingQueue<String> blockingQueue;

    private Map<String, String> containerNameMap;

    public DockerSandBoxPool(DockerClient dockerClient, String sandboxImage, String volumeDir, Long memoryLimit,
                             Long memorySwapLimit, Long cpuLimit, int poolSize, String containerNamePrefix) {
        this.dockerClient = dockerClient;
        this.sandboxImage = sandboxImage;
        this.volumeDir = volumeDir;
        this.memoryLimit = memoryLimit;
        this.memorySwapLimit = memorySwapLimit;
        this.cpuLimit = cpuLimit;
        this.poolSize = poolSize;
        this.containerNamePrefix = containerNamePrefix;

        // 阻塞队列 最多放poolSize个容器
        this.blockingQueue = new ArrayBlockingQueue<>(poolSize);
        this.containerNameMap = new HashMap<>();
    }

    // 从队列取出容器
    public String getContainer() {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 使用完毕后归还容器
    public void returnContainer(String containerId) {
        blockingQueue.add(containerId);
    }

    // 初始化
    public void initDockerSandBoxPool() {
        for (int i = 0; i < poolSize; i++) {
            createDockerContainer(containerNamePrefix + JudgeConstants.UNDERLINE_SEPARATOR + i);
        }
    }

    private void createDockerContainer(String containerName) {

        List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).exec();
        if (!CollectionUtil.isEmpty(containerList)) {
            String names = JudgeConstants.JAVA_CONTAINER_PREFIX + containerName;
            for (Container container : containerList) {
                String[] containerNames = container.getNames();
                if (containerNames != null && containerNames.length > 0 && names.equals(containerNames[0])) {
                    if ("created".equals(container.getState()) || "exited".equals(container.getState())) {
                        //启动容器
                        dockerClient.startContainerCmd(container.getId()).exec();
                    }
                    blockingQueue.add(container.getId());
                    //  id   容器名
                    containerNameMap.put(container.getId(), containerName);
//                    System.out.println("==================" + container.getId());
//                    System.out.println("==================" + containerName);
                    return;
                }
            }
        }

        //拉取镜像
        pullJavaEnvImage();
        //创建容器  限制资源   控制权限
        HostConfig hostConfig = getHostConfig(containerName);
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(JudgeConstants.JAVA_ENV_IMAGE)
                .withName(containerName);  //containerName容器名称
        CreateContainerResponse createContainerResponse = containerCmd.withHostConfig(hostConfig)
                .withAttachStderr(true).withAttachStdout(true).withTty(true).exec();
        //记录容器id
        String containerId = createContainerResponse.getId();
        //启动容器
        dockerClient.startContainerCmd(containerId).exec();
        blockingQueue.add(containerId);
    }

    //拉取java执行环境镜像 只拉取一次
    private void pullJavaEnvImage() {
        // 拉取所有的镜像   只拉取一次java8
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> imageList = listImagesCmd.exec();
        //  遍历
        for (Image image : imageList) {
            String[] repoTags = image.getRepoTags();
            // sandboxImage      java8 jdk镜像
            if (repoTags != null && repoTags.length > 0 && sandboxImage.equals(repoTags[0])) {
                return;
            }
        }

        // 如果没有 java8 重新拉取一次
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(sandboxImage);
        try {
            // 监听拉取的过程（阻塞等待）
            pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //限制资源   控制权限
    private HostConfig getHostConfig(String containerName) {
        HostConfig hostConfig = new HostConfig();
        //设置挂载目录，指定用户代码路径
        String userCodeDir = createContainerDir(containerName);
        hostConfig.setBinds(new Bind(userCodeDir, new Volume(volumeDir)));
        //限制 docker 容器使用的资源
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwapLimit);
        //swap 交换区限制
        hostConfig.withCpuCount(cpuLimit);

        hostConfig.withNetworkMode("none");
        //禁用网络
        hostConfig.withReadonlyRootfs(true);
        //禁止在 root 目录写文件
        return hostConfig;
    }

    //每个容器都要创建的指定挂载目录
    private String createContainerDir(String containerName) {
        //一级目录存放所有容器的挂载目录
        String codeDir = System.getProperty("user.dir") + File.separator + JudgeConstants.CODE_DIR_POOL;
        if (!FileUtil.exist(codeDir)) {
            FileUtil.mkdir(codeDir);
        }
        return codeDir + File.separator + containerName;
    }

    public String getCodeDir(String containerId) {
        System.out.println("====================" + containerId);
        String containerName = containerNameMap.get(containerId);
        log.info("containerName：{}", containerName);
        return System.getProperty("user.dir") + File.separator + JudgeConstants.CODE_DIR_POOL + File.separator + containerName;
    }
}
