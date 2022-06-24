package com.hzy.cxxvideo.controller.interceptor;

import com.hzy.cxxvideo.utils.JSONResult;
import com.hzy.cxxvideo.utils.JSONUtils;
import com.hzy.cxxvideo.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.controller.interceptor
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/22 3:39
 **/
public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user_redis_session";
    /*
    拦截请求，在controller调用之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (true) {
            System.out.println("请求拦截");
            return false;
        }
        return true;

//        String userId = request.getHeader("userId");
//        String userToken = request.getHeader("userToken");
//
//        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
//
//            String uniqueToken = redisOperator.get(USER_REDIS_SESSION + ":" + userId);
//
//            if (StringUtils.isBlank(userToken) && StringUtils.isEmpty(userToken)) {
//                System.out.println("请登录...");
//                returnErrorResponse(response, new JSONResult().errorTokenMsg("请登录！"));
//                return false;
//            } else {
//                if(!uniqueToken.equals(userToken)) {
//                    System.out.println("账号在其它设备登录！");
//                    returnErrorResponse(response, new JSONResult().errorTokenMsg("账号在其它设备登录！"));
//                    return false;
//                }
//            }
//        } else {
//            System.out.println("请登录...");
//            returnErrorResponse(response, new JSONResult().errorTokenMsg("请登录！"));
//            return false;
//        }
//        return true;
    }

    // 把拦截信息里的错误以json字符串的形式抛出
    public void returnErrorResponse(HttpServletResponse response, JSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JSONUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }

    /*
    请求controller后，在渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
    /*
    请求controller后，在渲染视图之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
