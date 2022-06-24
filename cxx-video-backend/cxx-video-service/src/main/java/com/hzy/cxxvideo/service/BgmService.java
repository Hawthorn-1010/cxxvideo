package com.hzy.cxxvideo.service;

import com.hzy.cxxvideo.entity.Bgm;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.service
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/13 21:41
 **/
@Service
public interface BgmService {

    // 查询背景音乐列表
    public List<Bgm> queryBgmList();

    public Bgm queryBgmIfo(String bgmId);

}
