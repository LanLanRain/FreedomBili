package com.rainsoul.bilibili.service;

import com.rainsoul.bilibili.dao.UserFollowingDao;
import com.rainsoul.bilibili.domain.FollowingGroup;
import com.rainsoul.bilibili.domain.User;
import com.rainsoul.bilibili.domain.UserFollowing;
import com.rainsoul.bilibili.domain.constant.UserConstant;
import com.rainsoul.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    /**
     * 添加用户关注关系。
     * 如果给定的关注组ID不存在，则使用默认关注组。
     * 如果指定的关注用户不存在，或尝试关注自己，则抛出异常。
     * 如果用户已经关注了该用户，则先删除原有的关注关系，再重新添加，确保关注关系的时效性。
     *
     * @param userFollowing 用户关注对象，包含用户ID、关注ID和（可选）关注组ID。
     */
    @Transactional
    public void addUserFollowing(UserFollowing userFollowing) {
        // 检查是否提供了关注组ID，未提供则使用默认关注组
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            // 检查提供的关注组ID是否存在
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在");
            }
        }

        // 检查被关注的用户是否存在
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) {
            throw new ConditionException("关注用户不存在");
        }

        // 禁止用户关注自己
        if (userFollowing.getUserId().equals(followingId)) {
            throw new ConditionException("自己不能关注自己");
        }

        // 删除已存在的关注关系，避免重复关注
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);

        // 设置关注关系创建时间，并添加关注关系
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

}
