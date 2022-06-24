package com.hzy.cxxvideo.controller;

import com.hzy.cxxvideo.entity.Users;
import com.hzy.cxxvideo.entity.vo.UsersVO;
import com.hzy.cxxvideo.service.UserService;
import com.hzy.cxxvideo.utils.JSONResult;
import com.hzy.cxxvideo.utils.JSONUtils;
import com.hzy.cxxvideo.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

// 以json形式以小程序数据交互
@RestController
@RequestMapping("/user")
@Api(value = "用户业务相关的接口", tags = {"用户业务相关的controller"})
public class UserController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @RequestMapping(path = "/uploadFace", method = RequestMethod.POST)
    // @RequestParam("file")，对应前端的参数name
    public JSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws IOException {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户名不能为空！");
        }

        String fileSpace = "D:/cxx-video";
        String uploadPath = "/" + userId + "/faceImage";

        FileOutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            // 判断上传的相片是否为空
            if(files != null && files.length > 0) {
                String filename = files[0].getOriginalFilename();

                if(StringUtils.isNotBlank(filename)) {
                    String imagePath = fileSpace + uploadPath + "/" + filename;
                    // 存在数据库中的路径
                    uploadPath += ("/" + filename);

                    File image = new File(imagePath);
                    // ?
                    if(image.getParentFile() == null || !image.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        image.getParentFile().mkdirs();
                    }
                    outputStream = new FileOutputStream(image);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream,outputStream);
                }
            } else {
                return JSONResult.errorMsg("上传失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传失败！");
        } finally {
            if(outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }

        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPath);
        userService.updateFaceImage(user);

        return JSONResult.ok(user);
    }

    @ApiOperation(value = "用户查询", notes = "登入“我的页面自动查询”")
    @ApiImplicitParam(name = "userId", value = "用户名id", required = true,
            dataType = "String", paramType = "query")
    @RequestMapping(path = "/query", method = RequestMethod.POST)
    public JSONResult query(String userId) {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户名不能为空！");
        }

        Users userResult = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult,usersVO);
        usersVO.setUserToken(redisOperator.get(USER_REDIS_SESSION + ":" + userResult.getId()));
        System.out.println(usersVO.toString());

        return JSONResult.ok(usersVO);
    }

    /*
    关注功能：增加粉丝数
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "视频id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "followerId", value = "用户id", required = true, dataType = "String", paramType = "query"),

    })
    @RequestMapping(path = "/addUserFollowers", method = RequestMethod.POST)
    public JSONResult addUserFollowers(String userId, String followerId) {
        userService.addUserFollowers(userId,followerId);
        return JSONResult.ok();
    }

    /*
    关注功能：减少粉丝数
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "视频id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "followerId", value = "用户id", required = true, dataType = "String", paramType = "query"),

    })
    @RequestMapping(path = "/reduceUserFollowers", method = RequestMethod.POST)
    public JSONResult reduceUserFollowers(String userId, String followerId) {
        userService.reduceUserFollowers(userId,followerId);
        return JSONResult.ok();
    }

    /**
     * 查询用户是否已关注当前用户
     */
    @RequestMapping(path = "/isFollow", method = RequestMethod.POST)
    public JSONResult isFollow(String userId, String followerId) {
        boolean res = userService.isFollow(userId,followerId);
        return JSONResult.ok(res);
    }

}















