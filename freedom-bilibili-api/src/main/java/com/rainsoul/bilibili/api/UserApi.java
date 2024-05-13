package com.rainsoul.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.rainsoul.bilibili.api.support.UserSupport;
import com.rainsoul.bilibili.domain.JsonResponse;
import com.rainsoul.bilibili.domain.PageResult;
import com.rainsoul.bilibili.domain.User;
import com.rainsoul.bilibili.domain.UserInfo;
import com.rainsoul.bilibili.service.UserFollowingService;
import com.rainsoul.bilibili.service.UserService;
import com.rainsoul.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户相关的 API 控制器
 */
@RestController
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollingService;

    /**
     * 获取当前用户的信息
     * 无参数
     *
     * @return JsonResponse<User> 包含用户信息的响应对象
     */
    @GetMapping(value = "/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    /**
     * 获取RSA公钥
     * 无参数
     *
     * @return JsonResponse<String> 包含RSA公钥字符串的响应对象
     */
    @GetMapping(value = "/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String publicKeyStr = RSAUtil.getPublicKeyStr();
        return JsonResponse.success(publicKeyStr);
    }

    /**
     * 添加新用户
     *
     * @param user 待添加的用户对象
     * @return JsonResponse<String> 添加结果的响应对象
     */
    @PostMapping(value = "/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();
    }

    /**
     * 用户登录，返回登录token
     *
     * @param user 包含登录所需信息的用户对象
     * @return JsonResponse<String> 包含登录token的响应对象
     * @throws Exception 登录异常
     */
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return JsonResponse.success(token);
    }

    /**
     * 更新用户信息
     *
     * @param userInfo 包含更新后用户信息的对象
     * @return JsonResponse<String> 更新结果的响应对象
     */
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfo(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfo(userInfo);
        return JsonResponse.success();
    }

    /**
     * 分页列出用户信息。
     *
     * @param pageNum 请求的页码。
     * @param pageSize 每页显示的记录数。
     * @param nick 用户昵称，用于查询条件。
     * @return 返回封装了用户信息分页结果的JsonResponse对象。
     */
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer pageNum,
                                                                  @RequestParam Integer pageSize,
                                                                  String nick) {
        // 获取当前登录用户的ID
        Long userId = userSupport.getCurrentUserId();
        // 构建用于查询的参数JSON对象
        JSONObject params = new JSONObject();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("nick", nick);
        params.put("userId", userId);
        // 调用服务层方法，根据参数查询用户信息分页结果
        PageResult<UserInfo> pageResult = userService.pageListUserInfos(params);
        // 如果查询结果中有数据，则对每条用户信息检查其关注状态
        if (pageResult.getTotal() > 0) {
            List<UserInfo> checkedUserInfoList =  userFollingService.checkFollowingStatus(userId, pageResult.getList());
            pageResult.setList(checkedUserInfoList);
        }
        // 将处理后的分页结果封装进JsonResponse中返回
        return new JsonResponse<>(pageResult);
    }

}
