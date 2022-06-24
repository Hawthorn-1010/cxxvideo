package com.hzy.cxxvideo.mapper;

import com.hzy.cxxvideo.entity.Users;
import com.hzy.cxxvideo.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends MyMapper<Users> {

    public void addTotalLikeCount(String userId);

    public void reduceTotalLikeCount(String userId);

    // 增加粉丝
    public void addFansCount(String userId);

    public void reduceFansCount(String userId);

    // 增加关注着
    public void addFollowingCount(String userId);

    public void reduceFollowingCount(String userId);

}