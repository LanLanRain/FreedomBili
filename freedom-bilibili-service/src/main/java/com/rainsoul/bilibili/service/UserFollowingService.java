package com.rainsoul.bilibili.service;

import com.rainsoul.bilibili.dao.UserFollowingDao;
import com.rainsoul.bilibili.domain.FollowingGroup;
import com.rainsoul.bilibili.domain.User;
import com.rainsoul.bilibili.domain.UserFollowing;
import com.rainsoul.bilibili.domain.UserInfo;
import com.rainsoul.bilibili.domain.constant.UserConstant;
import com.rainsoul.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 获取指定用户的所有关注分组信息。
     *
     * @param userId 需要获取关注信息的用户ID。
     * @return 返回一个包含所有关注分组信息的列表。每个分组都包含了该分组下的所有关注用户信息。
     */
    public List<FollowingGroup> getUserFollowings(Long userId) {
        // 从数据库获取用户的所有关注记录
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);

        // 将关注记录转换为关注ID的集合
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());

        // 初始化用户信息列表
        List<UserInfo> userInfoList = new ArrayList<>();
        // 如果关注ID集合不为空，则查询这些用户的基本信息
        if (followingIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
            // 将查询到的用户基本信息匹配并设置到关注记录中
            for (UserFollowing userFollowing : list) {
                for (UserInfo userInfo : userInfoList) {
                    if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                        userFollowing.setUserInfo(userInfo);
                    }
                }
            }
        }

        // 获取系统定义的关注分组信息
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);

        // 创建一个包含所有关注用户的分组
        FollowingGroup allFollowingGroup = new FollowingGroup();
        allFollowingGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allFollowingGroup.setFollowingUserInfoList(userInfoList);

        // 初始化结果列表，首先添加包含所有关注用户的分组
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allFollowingGroup);

        // 为每个系统定义的关注分组添加对应的用户信息
        for (FollowingGroup followingGroup : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : list) {
                if (followingGroup.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            followingGroup.setFollowingUserInfoList(infoList);
            result.add(followingGroup);
        }

        return result;
    }

    /**
     * 获取指定用户的粉丝列表。
     *
     * @param userId 需要获取粉丝列表的用户ID。
     * @return 返回一个包含该用户所有粉丝的列表，每个粉丝都附带其用户信息，并标示是否正在关注该用户。
     */
    public List<UserFollowing> getUserFans(Long userId) {
        // 从数据库获取指定用户的粉丝列表
        List<UserFollowing> fansList = userFollowingDao.getUserFans(userId);
        // 将粉丝ID转换为集合，以便后续查询
        Set<Long> fanIdSet = fansList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        // 如果粉丝ID集合不为空，则查询这些粉丝的详细用户信息
        if (fanIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        // 获取指定用户的所有关注者列表
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        // 为每个粉丝附加用户信息，并标记是否被指定用户关注
        for (UserFollowing fans : fansList) {
            for (UserInfo userInfo : userInfoList) {
                if (fans.getUserId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(false); // 标记为未关注
                    fans.setUserInfo(userInfo);
                }
            }
            for (UserFollowing following : followingList) {
                if (following.getFollowingId().equals(fans.getUserId())) {
                    fans.getUserInfo().setFollowed(true); // 标记为已关注
                }
            }
        }
        return fansList;
    }

}
