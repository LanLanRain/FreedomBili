package com.rainsoul.bilibili.api;

import com.rainsoul.bilibili.api.support.UserSupport;
import com.rainsoul.bilibili.domain.JsonResponse;
import com.rainsoul.bilibili.domain.auth.UserAuthorities;
import com.rainsoul.bilibili.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthApi {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities() throws Exception {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }
}
