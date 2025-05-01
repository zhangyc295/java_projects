package com.example.job.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.NacosConstants;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.entity.enums.ContestStatus;
import com.example.common.redis.RedisService;
import com.example.job.entity.*;
import com.example.job.mapper.*;
import com.example.job.service.MessageContentService;
import com.example.job.service.MessageService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ContestXxlJob {

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private MessageContentMapper messageContentMapper;

    @Autowired
    private MessageContentService messageContentService;

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserContestMapper userContestMapper;

    // 更新竞赛发布状态
    @XxlJob("contestListOrganizeHandler")
    public void contestListOrganizeHandler() {
        log.info("contestListOrganizeHandler --------------------------------------");
        //未完赛竞赛列表
        List<Contest> unFinishedList = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                .select(Contest::getContestId, Contest::getTitle, Contest::getStartTime, Contest::getEndTime)
                .gt(Contest::getEndTime, LocalDateTime.now()).eq(Contest::getStatus, 1).orderByDesc(Contest::getCreateTime));
        refreshCache(RedisConstants.CONTEST_UNFINISHED_LIST, unFinishedList);

        //历史竞赛列表
        List<Contest> historyList = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                .select(Contest::getContestId, Contest::getTitle, Contest::getStartTime, Contest::getEndTime)
                .le(Contest::getEndTime, LocalDateTime.now()).eq(Contest::getStatus, 1).orderByDesc(Contest::getCreateTime));
        refreshCache(RedisConstants.CONTEST_FINISHED_LIST, historyList);
    }

    // 竞赛排名
    @XxlJob("contestResultHandler")
    public void contestResultHandler() {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();

        // 前一天时间
        LocalDateTime yesterday = now.minusDays(1);

        // 已发布的竞赛列表
        List<Contest> contests = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                .select(Contest::getContestId, Contest::getTitle)
                .eq(Contest::getStatus, ContestStatus.PUBLISH.getValue())
                .ge(Contest::getEndTime, yesterday).le(Contest::getEndTime, now));
        if (CollectionUtil.isEmpty(contests)) {
            return;
        }
        Set<Long> contestIdSet = contests.stream().map(Contest::getContestId).collect(Collectors.toSet());
        List<RankInfo> scoreList = userSubmitMapper.selectUserScoreList(contestIdSet);
        // key: contestId  value: rankInfo
        Map<Long, List<RankInfo>> contestSocreMap = scoreList.stream().collect(Collectors.groupingBy(RankInfo::getContestId));

        // 生成系统通知消息
        createMessage(contests, contestSocreMap);


    }

    private void createMessage(List<Contest> contests, Map<Long, List<RankInfo>> contestSocreMap) {
        List<MessageContent> messageContentList = new ArrayList<>();
        List<Message> messageList = new ArrayList<>();
        for (Contest contest : contests) {
            Long contestId = contest.getContestId();

            List<RankInfo> rankInfoList = contestSocreMap.get(contestId);
            int totalSize = rankInfoList.size();
            int ranking = 1;
            for (RankInfo rankInfo : rankInfoList) {
                String title = "系统通知：" + contest.getTitle() + "排名情况公布";
                String content = "您所参与的竞赛：" + contest.getTitle() + "，本次共有" + totalSize + "人参与，" +
                        "您排名为第" + ranking + "名！";
                rankInfo.setContestRank(ranking);
                // List储存 避免多次数据库插入操作
                MessageContent messageContent = new MessageContent();
                messageContent.setMessageTitle(title);
                messageContent.setMessageContent(content);
                messageContent.setCreatedBy(NacosConstants.SYSTEM_ADMIN);
                messageContentList.add(messageContent);
//                messageContentMapper.insert(messageContent);

                Message message = new Message();
                message.setSendId(NacosConstants.SYSTEM_ADMIN);
                message.setReceiveId(rankInfo.getUserId());
                message.setCreatedBy(NacosConstants.SYSTEM_ADMIN);
                messageList.add(message);
                ranking++;
            }
            // 更新得分排名信息
            userContestMapper.updateUserScore(rankInfoList);
            redisService.rightPushAll(getContestRankKey(contestId), rankInfoList);
        }
        // 批量插入  contentId在批量插入后生成 需要重新赋值
        messageContentService.batchInsert(messageContentList);

        // 为存入redis做准备 避免多次交互
        Map<String, MessageContentVO> messageContentRedisMap = new HashMap<>();

        for (int i = 0; i < messageContentList.size(); i++) {
            MessageContent messageContent = messageContentList.get(i);

            MessageContentVO messageContentRedis = new MessageContentVO();
            BeanUtils.copyProperties(messageContent, messageContentRedis);
            String messageContentRedisKey = getMessageContentKey(messageContent.getContentId());
            messageContentRedisMap.put(messageContentRedisKey, messageContentRedis);
//            redisService.setCacheObject(messageContentRedisKey, messageContent);

            Message message = messageList.get(i);
            message.setContentId(messageContent.getContentId());
            System.out.println("================================" + message);

        }

//        try {
//            messageService.batchInsert(messageList);
//        } catch (Exception e) {
//            // 如果是 InvocationTargetException，解包真实异常
//            Throwable cause = e;
//
//            System.err.println("插入失败，根本原因: " + cause);
//            cause.printStackTrace(); // 打印完整堆栈
//            throw e;
//        }

        messageService.batchInsert(messageList);
//        for (Message message : messageList) {
//            System.out.println("Message after insert - messageId: " + message.getMessageId() + ", contentId: " + message.getContentId());
//        }

        // 操作redis  消息的对应列表
        Map<Long, List<Message>> messageMap = messageList.stream().collect(Collectors.groupingBy(Message::getReceiveId));
        Iterator<Map.Entry<Long, List<Message>>> iterator = messageMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<Message>> entry = iterator.next();
            Long receiveId = entry.getKey();
            String clientMessageListKey = getClientMessageListKey(receiveId);
            List<Long> messageContentIdList = entry.getValue().stream().map(Message::getContentId).toList();
            redisService.rightPushAll(clientMessageListKey, messageContentIdList);
        }
        redisService.multiSet(messageContentRedisMap);
        // 统一插入 比较多次交互
    }

    public void refreshCache(String contestListKey, List<Contest> examList) {
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }

        Map<String, Contest> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();
        for (Contest contest : examList) {
            examMap.put(getDetailKey(contest.getContestId()), contest);
            examIdList.add(contest.getContestId());
        }
        redisService.multiSet(examMap); //刷新详情缓存
        redisService.deleteObject(contestListKey);
        redisService.rightPushAll(contestListKey, examIdList); //刷新列表缓存
    }

    private String getDetailKey(Long contestId) {
        return RedisConstants.CONTEST_DETAIL + contestId;
    }

    private String getClientMessageListKey(Long userId) {
        return RedisConstants.CLIENT_MESSAGE_LIST + userId;
    }

    private String getMessageContentKey(Long contentId) {
        return RedisConstants.MESSAGE_CONTENT + contentId;
    }

    private String getContestRankKey(Long contentId) {
        return RedisConstants.CONTEST_RANK_LIST + contentId;
    }
}

