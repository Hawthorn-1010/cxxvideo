package com.hzy.cxxvideo.service;

import com.hzy.cxxvideo.entity.Users;
import org.springframework.stereotype.Service;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.service
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/6 17:40
 **/
@Service
public interface UserService {

    // 判断用户名是否存在
    public boolean queryUsernameIsExist(String username);

    // 注册成功，保存用户数据
    public void saveUser(Users user);

    // 根据用户名查找密码，判断密码是否正确,正确的话返回查询结果集，不正确的话返回null
    public Users checkPasswordByUsername(Users user);

    public void updateFaceImage(Users user);

    public Users queryUserInfo(String userId);

    public void addUserFollowers(String userId, String followerId);

    public void reduceUserFollowers(String userId, String followerId);

    public boolean isFollow(String userId, String followerId);

}
