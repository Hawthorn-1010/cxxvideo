package com.hzy.cxxvideo.service;

import com.github.pagehelper.Page;
import com.hzy.cxxvideo.entity.Comments;
import com.hzy.cxxvideo.entity.UsersReport;
import com.hzy.cxxvideo.entity.Videos;
import com.hzy.cxxvideo.mapper.VideosMapper;
import com.hzy.cxxvideo.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.service
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/15 15:29
 **/
@Service
public interface VideoService {

    public String saveVideo (Videos video);

    public void updateVideoCoverPath (Videos video);

    public PageResult getAllVideos (Videos video, Integer isSaveRecord, Integer page, Integer pageSize);

    public List<String> getHotWords ();

    public void userLikeVideo(String userId, String videoId, String videoCreatorId);

    public void userUnlikeVideo(String userId, String videoId, String videoCreatorId);

    public boolean queryVideoInfo(String userId, String videoId, String videoCreatorId);

    public PageResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    public PageResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

    public PageResult queryComments(String videoId, Integer page, Integer pageSize);

    public void reportVideo(UsersReport usersReport);

    public void saveComment(Comments comments);

}







