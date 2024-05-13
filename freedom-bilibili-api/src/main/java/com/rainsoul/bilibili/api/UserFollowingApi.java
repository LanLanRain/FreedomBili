package com.rainsoul.bilibili.api;

import com.rainsoul.bilibili.api.support.UserSupport;
import com.rainsoul.bilibili.domain.FollowingGroup;
import com.rainsoul.bilibili.domain.JsonResponse;
import com.rainsoul.bilibili.domain.UserFollowing;
import com.rainsoul.bilibili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 添加用户关注
     *
     * @param userFollowing 用户关注对象，包含被关注用户的ID等信息
     * @return 返回操作结果，成功则返回一个包含成功信息的JsonResponse对象
     */
    @PostMapping(value = "/user-followings")
    public JsonResponse<String> addUserFollowing(UserFollowing userFollowing) {
        // 获取当前登录用户的ID
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId); // 设置关注操作发起用户的ID
        userFollowingService.addUserFollowing(userFollowing); // 执行添加用户关注的操作
        return JsonResponse.success(); // 返回操作成功的响应
    }

    /**
     * 获取当前用户关注的用户或组织列表。
     *
     * <p>此接口不接受任何参数，通过用户服务获取当前用户的ID，然后调用用户关注服务查询该用户关注的用户或组织信息。
     *
     * @return JsonResponse<List < FollowingGroup>> - 包含查询结果的JSON响应对象。查询结果为一个FollowingGroup对象的列表，
     * 其中每个对象代表用户关注的一个组（比如，用户关注的组织或个人）。
     */
    @GetMapping(value = "/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings() {
        // 获取当前登录用户的ID
        Long userId = userSupport.getCurrentUserId();
        // 根据用户ID查询用户关注的信息
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(result);
    }

    /**
     * 获取当前用户关注者的列表。
     *
     * <p>该接口不需要任何参数，通过调用 {@code userSupport.getCuurrentUserId()} 获取当前登录用户的ID，
     * 然后委托给 {@code userFollowingService} 以获取该用户的所有关注者列表。
     *
     * @return {@link JsonResponse} 包装了用户关注者列表的 {@link List<UserFollowing>}。
     */
    @GetMapping(value = "/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        // 获取当前登录用户的ID
        Long userId = userSupport.getCurrentUserId();
        // 根据用户ID获取其关注者列表
        List<UserFollowing> result = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(result);
    }

    @PostMapping(value = "/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroups(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return new JsonResponse<>(groupId);
    }

    @GetMapping(value = "/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups() {
        // 获取登录用户的ID
        Long userId = userSupport.getCurrentUserId();
        // 根据用户ID获取用户关注的所有群组
        List<FollowingGroup> followingGroupList = userFollowingService.getUserFollowingGroups(userId);
        return new JsonResponse<>(followingGroupList);
    }


}
