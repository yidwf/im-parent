package com.yesido.im.restful.api.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.model.entity.Group;
import com.yesido.im.restful.consts.Constant;
import com.yesido.im.restful.service.GroupService;
import com.yesido.rpc.dto.GroupDetailDto;
import com.yesido.rpc.proto.RPCResult;

/**
 * 群组RPC服务
 * 
 * @author yesido
 * @date 2019年9月11日 下午3:22:41
 */
@RestController
@RequestMapping(Constant.RPC_URI_PREFIX + "/group")
public class RPCGroupController {

    @Autowired
    private GroupService groupService;

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public RPCResult<GroupDetailDto> detail(String groupId) {
        Group group = groupService.findByGroupId(groupId);
        // 简单处理
        GroupDetailDto dto = JSONObject.parseObject(JSONObject.toJSONString(group), GroupDetailDto.class);
        return RPCResult.ok(dto);
    }
}
