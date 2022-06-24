package com.hzy.cxxvideo.controller;

import com.hzy.cxxvideo.entity.Users;
import com.hzy.cxxvideo.entity.vo.UsersVO;
import com.hzy.cxxvideo.service.UserService;
import com.hzy.cxxvideo.utils.JSONResult;
import com.hzy.cxxvideo.utils.JSONUtils;
import com.hzy.cxxvideo.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// 以json形式以小程序数据交互
@RestController
@Api(value = "用户注册登陆的接口", tags = {"注册登录的controller"})
public class RegisterLoginController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public JSONResult register(@RequestBody Users user) throws Exception {

        // 1.判断用户名不为空
        if (StringUtils.isBlank(user.getUsername())) {
            return JSONResult.errorMsg("用户名不能为空！");
        }

        // 2.判断密码不能为空
        if (StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("密码不能为空！");
        }

        // 3.判断用户是否已被注册
        if(userService.queryUsernameIsExist(user.getUsername())) {
            return JSONResult.errorMsg("用户名已被注册！");
        } else {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);

            userService.saveUser(user);
        }

        // 4.判断邮箱是否已被注册

        //注册用户，把用户信息存到库里。传到小程序
        user.setPassword("");

        UsersVO userVO = setUserRedisSessionToken(user);

        return JSONResult.ok(userVO);
    }

    public UsersVO setUserRedisSessionToken (Users user) {
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000 * 60 * 30);

        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(user,userVO);
        userVO.setUserToken(uniqueToken);

        return userVO;
    }

    @ApiOperation(value = "用户登录", notes = "用户登陆的接口")
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public JSONResult login(@RequestBody Users user) {

        // 1. 没有输入用户名
        if (StringUtils.isBlank(user.getUsername())) {
            return JSONResult.errorMsg("请输入用户名！");
        }

        // 2. 没有输入密码
        if (StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("请输入密码！");
        }

        // 3. 用户名不存在
        if(!userService.queryUsernameIsExist(user.getUsername())) {
            return JSONResult.errorMsg("用户名不存在！");
        }

        // 4. 密码不正确
        Users userResult = userService.checkPasswordByUsername(user);
        if(userResult == null) {
            return JSONResult.errorMsg("密码不正确！");
        }

        // 5. 返回
        userResult.setPassword("");

        UsersVO userVO = setUserRedisSessionToken(userResult);

        return JSONResult.ok(userVO);
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户名id", required = true,
            dataType = "String", paramType = "query")
    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public JSONResult logout(String userId) {
//        System.out.println("USER_ID IS :" + userId);
        redisOperator.del(USER_REDIS_SESSION + ":" + userId);
        return JSONResult.ok();
    }

}















