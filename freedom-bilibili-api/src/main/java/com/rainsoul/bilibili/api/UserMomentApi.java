package com.rainsoul.bilibili.api;

import com.rainsoul.bilibili.api.support.UserSupport;
import com.rainsoul.bilibili.domain.JsonResponse;
import com.rainsoul.bilibili.domain.UserMoment;
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

    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
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
