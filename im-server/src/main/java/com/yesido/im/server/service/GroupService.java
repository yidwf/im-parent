package com.yesido.im.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesido.im.ptoto.model.entity.Group;
import com.yesido.mongodb.service.MongoDBService;

/**
 * 群组service
 * 
 * @author yesido
 * @date 2019年8月27日 上午10:06:49
 */
@Service
public class GroupService {
    Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private MongoDBService mongoDBService;

    /**
     * 获取群组详情
     */
    public Group findByGroupId(String groupId) {
        return mongoDBService.get(groupId, Group.class);
    }
}
