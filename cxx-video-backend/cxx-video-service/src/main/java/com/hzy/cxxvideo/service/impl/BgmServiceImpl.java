package com.hzy.cxxvideo.service.impl;

import com.hzy.cxxvideo.entity.Bgm;
import com.hzy.cxxvideo.entity.Users;
import com.hzy.cxxvideo.mapper.BgmMapper;
import com.hzy.cxxvideo.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.service.impl
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/13 21:42
 **/
@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    public BgmMapper bgmMapper;

    @Override
    public List<Bgm> queryBgmList() {

        return bgmMapper.selectAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Bgm queryBgmIfo(String bgmId) {
        Example bgmExample = new Example(Bgm.class);
        Example.Criteria criteria = bgmExample.createCriteria();
        criteria.andEqualTo("id",bgmId);
        Bgm bgm = bgmMapper.selectOneByExample(bgmExample);
        return bgm;
    }
}
















