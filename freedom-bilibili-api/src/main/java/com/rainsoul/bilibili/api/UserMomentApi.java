package com.rainsoul.bilibili.api;

import com.rainsoul.bilibili.api.support.UserSupport;
import com.rainsoul.bilibili.domain.JsonResponse;
import com.rainsoul.bilibili.domain.UserMoment;
import com.rainsoul.bilibili.domain.annotation.ApiLimitedRole;
import com.rainsoul.bilibili.domain.constant.AuthRoleConstant;
import com.rainsoul.bilibili.service.UserMomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentApi {

    @Autowired
    private UserMomentService userMomentService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 添加用户动态
     *
     * @param userMoment 包含用户动态信息的对象，通过RequestBody接收前端传来的JSON数据。
     * @return 返回一个JSON响应，表示动态添加是否成功。
     * @throws Exception 如果添加过程中遇到任何错误，则抛出异常。
     */
    @PostMapping("/user-moments")
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        // 服务层调用，添加用户动态
        userMomentService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }

}
