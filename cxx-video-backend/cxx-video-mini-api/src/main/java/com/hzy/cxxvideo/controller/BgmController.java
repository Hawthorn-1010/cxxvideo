package com.hzy.cxxvideo.controller;

import com.hzy.cxxvideo.service.BgmService;
import com.hzy.cxxvideo.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.controller
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/13 22:19
 **/

@RestController
@RequestMapping("/bgm")
@Api(value = "背景音乐相关的接口", tags = {"背景音乐相关的controller"})
public class BgmController {

    @Autowired
    public BgmService bgmService;

    @ApiOperation(value = "查询可供使用的bgm", notes = "查询bgm的接口")
    @RequestMapping(path = "/queryBgm", method = RequestMethod.POST)
    public JSONResult queryBgm() {

        return JSONResult.ok(bgmService.queryBgmList());
    }

}



















