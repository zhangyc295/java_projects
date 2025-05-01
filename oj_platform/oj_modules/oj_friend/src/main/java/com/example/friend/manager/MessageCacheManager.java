package com.example.friend.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.entity.enums.ContestListType;
import com.example.common.entity.model.PageBase;
import com.example.common.redis.RedisService;
import com.example.friend.mapper.MessageContentMapper;
import com.example.friend.model.contest.ContestListVO;
import com.example.friend.model.contest.ContestShowDTO;
import com.example.friend.model.message.MessageContentVO;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageCacheManager {
    @Autowired
    private RedisService redisService;
    @Autowired
    private MessageContentMapper messageContentMapper;

    public Long getListSize(Long userId) {
        String clientMessageListKey = getClientMessageListKey(userId);
        return redisService.getListSize(clientMessageListKey);
    }


    private String getClientMessageListKey(Long userId) {
        return RedisConstants.CLIENT_MESSAGE_LIST + userId;
    }

    private String getMessageContentKey(Long contentId) {
        return RedisConstants.MESSAGE_CONTENT + contentId;
    }

    public void refreshCache(Long userId) {
        List<MessageContentVO> messageContentVOList = messageContentMapper.selectMessageContentList(userId);
        if (CollectionUtil.isEmpty(messageContentVOList)) {
            return;
        }
        List<Long> contentIdList = messageContentVOList.stream().map(MessageContentVO::getContentId).toList();
        String clientMessageListKey = getClientMessageListKey(userId);
        redisService.rightPushAll(clientMessageListKey, contentIdList);
        Map<String, MessageContentVO> messageContentVOMap = new HashMap<>();
        for (MessageContentVO messageContentVO : messageContentVOList) {
            messageContentVOMap.put(getMessageContentKey(messageContentVO.getContentId()), messageContentVO);
        }
        redisService.multiSet(messageContentVOMap);
    }

    public List<MessageContentVO> getMessageContentVOList(PageBase pageBase, Long userId) {
        int start = (pageBase.getPageNumber() - 1) * pageBase.getPageSize();
        int end = start + pageBase.getPageSize() - 1; //下标需要 -1

        String clientMessageListKey = getClientMessageListKey(userId);
        List<Long> messageContentIdList = redisService.getCacheListByRange(clientMessageListKey, start, end, Long.class);

        List<MessageContentVO> messageContentVOList = assembleMessageContentVOList(messageContentIdList);
        if (CollectionUtil.isEmpty(messageContentVOList)) {

            messageContentVOList = getMessageContentListByDB(pageBase, userId); //从数据库中获取数据
            refreshCache(userId);
        }
        return messageContentVOList;
    }

    private List<MessageContentVO> assembleMessageContentVOList(List<Long> messageContentIdList) {
        if (CollectionUtil.isEmpty(messageContentIdList)) {
            return null;
        }
        List<String> messageContentKeyList = new ArrayList<>();
        for (Long contentId : messageContentIdList) {
            messageContentKeyList.add(getMessageContentKey(contentId));
        }
        List<MessageContentVO> messageContentVOList = redisService.multiGet(messageContentKeyList,MessageContentVO.class);
        CollUtil.removeNull(messageContentVOList);
        if (CollUtil.isEmpty(messageContentVOList)) {
            return null;
        }
        return messageContentVOList;
    }


    private List<MessageContentVO> getMessageContentListByDB(PageBase pageBase, Long userId) {
        PageHelper.startPage(pageBase.getPageNumber(), pageBase.getPageSize());

        return messageContentMapper.selectMessageContentList(userId);
    }
}
