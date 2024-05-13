package com.rainsoul.bilibili.service;

import com.mysql.cj.util.StringUtils;
import com.rainsoul.bilibili.dao.UserDao;
import com.rainsoul.bilibili.domain.User;
import com.rainsoul.bilibili.domain.UserInfo;
import com.rainsoul.bilibili.domain.constant.UserConstant;
import com.rainsoul.bilibili.domain.exception.ConditionException;
import com.rainsoul.bilibili.service.util.MD5Util;
import com.rainsoul.bilibili.service.util.RSAUtil;
import com.rainsoul.bilibili.service.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 添加用户到数据库。
     *
     * @param user 用户对象，包含用户的基本信息。
     * @throws ConditionException 如果手机号为空、该手机号已注册或密码解密失败，则抛出异常。
     */
    public void addUser(User user) {
        // 验证手机号是否为空
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空");
        }
        // 检查手机号是否已注册
        if (userDao.getUserByPhone(phone) != null) {
            throw new ConditionException("该手机号已注册");
        }
        // 生成盐值并加密密码
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password); // 解密密码
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        String md5password = MD5Util.sign(rawPassword, salt, "utf-8"); // 加密密码
        user.setSalt(salt);
        user.setPassword(md5password);
        user.setCreateTime(now);
        userDao.addUser(user); // 添加用户信息

        // 初始化用户附加信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo); // 添加用户附加信息
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    /**
     * 用户登录
     * @param user 用户对象，包含手机号和密码
     * @return 返回登录成功后的token
     * @throws ConditionException 如果手机号为空、用户不存在、密码解密失败、密码错误则抛出异常
     */
    public String login(User user) throws Exception {
        // 验证手机号是否为空
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空");
        }
        // 根据手机号从数据库获取用户
        User dbUser = userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("该用户不存在");
        }

        // 获取用户输入的密码
        String password = user.getPassword();

        String rawPassword;

        // 尝试解密密码
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }

        // 获取用户盐值，用于密码加密
        String salt = dbUser.getSalt();

        // 使用盐值和解密后的密码计算MD5加密密码
        String md5Password = MD5Util.sign(rawPassword, salt, "utf-8");

        // 验证计算后的密码与数据库中存储的密码是否一致
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误");
        }
        // 登录成功，生成并返回token
        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    public void updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfo(userInfo);
    }
}
