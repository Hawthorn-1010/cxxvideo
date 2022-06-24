package com.hzy.cxxvideo.enums;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.enums
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/15 18:47
 **/
public enum VideoStatusEnum{

    // 允许播放
    SUCCESS(1),
    // 禁止播放
    FORBIDDEN(2);

    private final int value;

    VideoStatusEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
