package org.example.java_doc_searcher_spring.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parser {
    private static final String INPUT_PATH =
            "E:/java_projects/search_index/jdk-8u431-docs-all/docs/api/";
    private Index index = new Index();

    public void run() {
        long startTime = System.currentTimeMillis();
        // 1.查询文件html
        ArrayList<File> fileList = new ArrayList<>();
        enumFile(INPUT_PATH, fileList);
        //System.out.println(fileList.size());
        for (File file : fileList) {
            System.out.println(file.getAbsolutePath());
            //解析每个html文件
            parseHtml(file);
        }
        //保存
        index.save();
        long endTime = System.currentTimeMillis();
        System.out.println("总消耗时间" + (endTime - startTime) + "ms");
    }

    public void runByThread() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        ArrayList<File> files = new ArrayList<>();
        enumFile(INPUT_PATH, files);
        CountDownLatch latch = new CountDownLatch(files.size());
        //多线程  引入线程池
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (File file : files) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(file.getAbsolutePath());
                    parseHtml(file);
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        index.save();
        long endTime = System.currentTimeMillis();
        System.out.println("多线程总消耗时间" + (endTime - startTime) + "ms");

    }

    private void parseHtml(File file) {
        //标题  url  正文
        String title = parseTitle(file);
        String url = parseUrl(file);
        String content = parseContentRegex(file);
        //添加索引
        index.addDoc(title, url, content);
    }

    private String parseTitle(File file) {
        //获取文件名  除去后缀名
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, lastIndex);
    }

    private String parseUrl(File file) {
        String part1 = "https://docs.oracle.com/javase/8/docs/api/";
        String part2 = file.getAbsolutePath().substring(INPUT_PATH.length()).replace(File.separatorChar, '/');;
        return part1 + part2;
    }

    public String parseContent(File file) {
        //读取文件  读取html有效数据
        //try(FileReader fileReader = new FileReader(file)) try with resources 自动关闭资源
        try {
            FileReader fileReader = new FileReader(file);
            boolean flag = true;
            StringBuilder content = new StringBuilder();
            while (true) {
                int result = fileReader.read();
                if (result == -1) {
                    break;
                }
                char c = (char) result;
                if (flag) {
                    if (c == '<') {
                        flag = false;
                        continue;
                    }
                    //去除换行
                    if (c == '\n' || c == '\r') {
                        c = ' ';
                    }
                    content.append(c);
                } else {
                    if (c == '>') {
                        flag = true;
                    }
                }
            }
            fileReader.close();
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            while (true) {
                int result = bufferedReader.read();
                if (result == -1) {
                    break;
                }
                char c = (char) result;
                if (c == '\n' || c == '\r') {
                    c = ' ';
                }
                content.append(c);
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //正则表达式
    public String parseContentRegex(File file) {
        String content = readFile(file);
        content = content.replaceAll("<script.*?>(.*?)</script>", " ");
        content = content.replaceAll("<.*?>", " ");
        content = content.replaceAll("\\s+", " ");
        return content;
    }

    //递归路径   //递归的结果
    private void enumFile(String inputPath, ArrayList<File> fileList) {
        File rootPath = new File(inputPath);
        File[] files = rootPath.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                enumFile(file.getAbsolutePath(), fileList);
            } else {
                if (file.getName().endsWith(".html")) {
                    fileList.add(file);
                }
            }
//            System.out.println(file.getName());
//            System.out.println(file);
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.run();
    }
}
