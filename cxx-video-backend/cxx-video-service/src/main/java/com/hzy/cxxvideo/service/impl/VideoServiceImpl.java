package com.hzy.cxxvideo.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hzy.cxxvideo.entity.*;
import com.hzy.cxxvideo.entity.vo.CommentsVO;
import com.hzy.cxxvideo.entity.vo.VideosVO;
import com.hzy.cxxvideo.mapper.*;
import com.hzy.cxxvideo.service.VideoService;
import com.hzy.cxxvideo.utils.PageResult;
import com.hzy.cxxvideo.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.ids.SelectByIdsMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.service.impl
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/15 18:32
 **/
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos video) {
        String videoId = sid.nextShort();
        video.setId(videoId);
        videosMapper.insertSelective(video);

        return videoId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideoCoverPath(Videos video) {
        Example example = new Example(Videos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",video.getId());
        videosMapper.updateByExampleSelective(video,example);
//        videosMapper.updateByPrimaryKeySelective(video);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PageResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {

        String desc = video.getVideoDesc();

//        System.out.println(isSaveRecord + "  " + video.getVideoDesc());
        // 保存熱搜詞
        if(isSaveRecord != null && isSaveRecord != 0) {
            SearchRecords searchRecords = new SearchRecords();
            String recordId = sid.nextShort();
            searchRecords.setId(recordId);
            searchRecords.setContent(desc);

            searchRecordsMapper.insert(searchRecords);
        }

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(video.getVideoDesc(),video.getUserId());
        PageInfo<VideosVO> pageInfo = new PageInfo(list);

        PageResult pageResult = new PageResult();
        // 当前页数
        pageResult.setPage(page);
        // 每行显示的内容
        pageResult.setRows(list);
        // 总记录数
        pageResult.setRecords(pageInfo.getTotal());
        // 总页数
        pageResult.setTotal(pageInfo.getPages());

        return pageResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreatorId) {
        // 1. 保存用户信息
        System.out.println(userId);
        System.out.println(videoId);
        System.out.println(videoCreatorId);
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        String id = sid.nextShort();
        usersLikeVideos.setId(id);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        System.out.println(usersLikeVideos);
        usersLikeVideosMapper.insert(usersLikeVideos);

        // 2. 添加视频like数
        videosMapperCustom.addVideoLikeCount(videoId);
        // 3. 添加videoCreator总like数
        usersMapper.addTotalLikeCount(videoCreatorId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnlikeVideo(String userId, String videoId, String videoCreatorId) {
        // 1. 查询用户信息
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);

        // 2. 减少视频like数
        videosMapperCustom.reduceVideoLikeCount(videoId);
        // 3. 减少videoCreator总like数
        usersMapper.reduceTotalLikeCount(videoCreatorId);
    }

    /**
     *
     * @return  true 表示用户已点赞， false 表示用户未点赞
     */
    @Override
    public boolean queryVideoInfo(String userId, String videoId, String videoCreatorId) {

        // 1. 查询用户点赞的联系是否存在
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);

        List<UsersLikeVideos> usersLikeVideos = usersLikeVideosMapper.selectByExample(example);

        // 注意这里的判断，需要是size > 0
        if (usersLikeVideos != null && usersLikeVideos.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PageResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {

//        System.out.println(isSaveRecord + "  " + video.getVideoDesc());

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);
        PageInfo<VideosVO> pageInfo = new PageInfo(list);

        PageResult pageResult = new PageResult();
        // 当前页数
        pageResult.setPage(page);
        // 每行显示的内容
        pageResult.setRows(list);
        // 总记录数
        pageResult.setRecords(pageInfo.getTotal());
        // 总页数
        pageResult.setTotal(pageInfo.getPages());

        return pageResult;
    }

    @Override
    public PageResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);
        PageInfo<VideosVO> pageInfo = new PageInfo(list);

        PageResult pageResult = new PageResult();
        // 当前页数
        pageResult.setPage(page);
        // 每行显示的内容
        pageResult.setRows(list);
        // 总记录数
        pageResult.setRecords(pageInfo.getTotal());
        // 总页数
        pageResult.setTotal(pageInfo.getPages());

        return pageResult;
    }

    @Override
    public PageResult queryComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);

        // 格式化成多少天前
        for(CommentsVO c : list) {
            String date = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(date);
        }

        PageInfo<VideosVO> pageInfo = new PageInfo(list);

        PageResult pageResult = new PageResult();
        // 当前页数
        pageResult.setPage(page);
        // 每行显示的内容
        pageResult.setRows(list);
        // 总记录数
        pageResult.setRecords(pageInfo.getTotal());
        // 总页数
        pageResult.setTotal(pageInfo.getPages());

        return pageResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportVideo(UsersReport usersReport) {
        String id = sid.nextShort();
        usersReport.setId(id);
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comments) {
        String id = sid.nextShort();
        comments.setId(id);
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }
}












