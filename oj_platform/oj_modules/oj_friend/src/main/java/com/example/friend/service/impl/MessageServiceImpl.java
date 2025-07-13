package com.example.friend.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.model.PageBase;
import com.example.common.entity.model.TableResult;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.friend.manager.MessageCacheManager;
import com.example.friend.mapper.MessageContentMapper;
import com.example.friend.model.message.MessageContentVO;
import com.example.friend.service.MessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageCacheManager messageCacheManager;

    @Autowired
    private MessageContentMapper messageContentMapper;

    @Override
    public TableResult list(PageBase pageBase) {

        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
        Long total = messageCacheManager.getListSize(userId);

        List<MessageContentVO> MessageContentVOList;
        if (total == null || total <= 0) {
            PageHelper.startPage(pageBase.getPageNumber(), pageBase.getPageSize());

            MessageContentVOList = messageContentMapper.selectMessageContentList(userId);

            // 刷新缓存
            messageCacheManager.refreshCache(userId);
            total = new PageInfo<>(MessageContentVOList).getTotal();
        } else {
            MessageContentVOList = messageCacheManager.getMessageContentVOList(pageBase, userId);
//            total = contestCacheManager.getListSize(contestShowDTO.getType(), userId);
        }
        if (CollectionUtil.isEmpty(MessageContentVOList)) {
            return TableResult.empty();
        }
//        assembleContestVOList(MessageContentVOList);
        return TableResult.success(MessageContentVOList, total);
    }
}
