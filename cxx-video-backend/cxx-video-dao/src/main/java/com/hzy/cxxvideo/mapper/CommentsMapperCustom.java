package com.hzy.cxxvideo.mapper;

import com.hzy.cxxvideo.entity.Comments;
import com.hzy.cxxvideo.entity.vo.CommentsVO;
import com.hzy.cxxvideo.entity.vo.VideosVO;
import com.hzy.cxxvideo.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentsMapperCustom extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(@Param("videoId") String videoId);

}