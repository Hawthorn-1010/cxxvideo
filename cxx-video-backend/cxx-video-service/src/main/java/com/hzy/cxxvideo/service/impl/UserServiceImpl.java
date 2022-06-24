package com.hzy.cxxvideo.service.impl;

import com.hzy.cxxvideo.entity.Users;
import com.hzy.cxxvideo.entity.UsersFans;
import com.hzy.cxxvideo.entity.UsersLikeVideos;
import com.hzy.cxxvideo.mapper.UsersFansMapper;
import com.hzy.cxxvideo.mapper.UsersMapper;
import com.hzy.cxxvideo.service.UserService;
import com.hzy.cxxvideo.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.service
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/6 17:44
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Users user = new Users();
        user.setUsername(username);

        // 这个select one方法是只要满足其中username相同即可？
        Users result = usersMapper.selectOne(user);
        if(result != null) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        // idworker设置全局id
        String userId = sid.nextShort();
        user.setId(userId);

        usersMapper.insert(user);

    }

    @Override
    public Users checkPasswordByUsername(Users user) {

        try {
            Users user2 = new Users();
            user2.setUsername(user.getUsername());
            Users result = usersMapper.selectOne(user2);
            // 如果密码正确
            if(result.getPassword().equals(MD5Utils.getMD5Str(user.getPassword()))) {
                return result;
            } else {
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateFaceImage(Users user) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",user.getId());
        usersMapper.updateByExampleSelective(user,userExample);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    // 增加用户粉丝数
    @Override
    public void addUserFollowers(String userId, String followerId) {
        // 1. 添加两个用户的关系
        UsersFans usersFans = new UsersFans();
        String id = sid.nextShort();
        usersFans.setId(id);
        usersFans.setUserId(userId);
        usersFans.setFanId(followerId);

        usersFansMapper.insert(usersFans);

        // 2. 被关注者粉丝数量增加
        usersMapper.addFansCount(userId);
        // 3. 关注者关注数量增加
        usersMapper.addFollowingCount(followerId);
    }

    // 减少用户粉丝数
    @Override
    public void reduceUserFollowers(String userId, String followerId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        // 注意这个属性值是entity中对应的属性值
        criteria.andEqualTo("fanId",followerId);
        usersFansMapper.deleteByExample(example);

        usersMapper.reduceFansCount(userId);

        usersMapper.reduceFollowingCount(followerId);
    }

    @Override
    public boolean isFollow(String userId, String followerId) {

        // 1. 查询用户点赞的联系是否存在
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        // 注意这里匹配的是entity里的字段
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",followerId);

        List<UsersFans> usersFans = usersFansMapper.selectByExample(example);

        // 注意这里的判断，需要是size > 0
        if (usersFans != null && usersFans.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

}





