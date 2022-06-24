package com.hzy.cxxvideo.mapper;

import com.hzy.cxxvideo.entity.Videos;
import com.hzy.cxxvideo.entity.vo.VideosVO;
import com.hzy.cxxvideo.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideosMapperCustom extends MyMapper<Videos> {

    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId") String userId);

    public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);

    public List<VideosVO> queryMyFollowVideos(@Param("userId") String userId);

    // 增加点赞数
    public void addVideoLikeCount(String videoId);

    // 减少点赞数
    public void reduceVideoLikeCount(String videoId);


}