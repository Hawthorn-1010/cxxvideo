package com.hzy.cxxvideo.controller;

import com.hzy.cxxvideo.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.controller
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/9 8:43
 **/
@RestController
public class BasicController {

    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user_redis_session";

    public static final String FILE_SPACE = "D:/cxx-video";

    public static final Integer PAGE_SIZE = 6;

}
