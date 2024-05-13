package com.rainsoul.bilibili.domain;

import java.util.Date;

public class UserFollowing {
    private Long id;
    private Long userId;
    private Long followingId;
    private Long groupId;
    private Date createTime;

    private UserInfo userInfo;

    public UserFollowing() {
    }

    public UserFollowing(Long id, Long userId, Long followingId, Long groupId, Date createTime, UserInfo userInfo) {
        this.id = id;
        this.userId = userId;
        this.followingId = followingId;
        this.groupId = groupId;
        this.createTime = createTime;
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserFollowing{" +
                "id=" + id +
                ", userId=" + userId +
                ", followingId=" + followingId +
                ", groupId=" + groupId +
                ", createTime=" + createTime +
                '}';
    }
}
