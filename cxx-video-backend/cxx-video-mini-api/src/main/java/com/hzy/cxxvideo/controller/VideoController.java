package com.hzy.cxxvideo.controller;

import com.hzy.cxxvideo.entity.Comments;
import com.hzy.cxxvideo.entity.Users;
import com.hzy.cxxvideo.entity.UsersReport;
import com.hzy.cxxvideo.entity.Videos;
import com.hzy.cxxvideo.enums.VideoStatusEnum;
import com.hzy.cxxvideo.service.BgmService;
import com.hzy.cxxvideo.service.VideoService;
import com.hzy.cxxvideo.utils.JSONResult;
import com.hzy.cxxvideo.utils.MergeVideoAudio;
import com.hzy.cxxvideo.utils.PageResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.controller
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/13 22:19
 **/

@RestController
@RequestMapping("/video")
@Api(value = "上传视频相关的接口", tags = {"上传视频相关的controller"})
public class VideoController extends BasicController{
    
//    @Autowired
//    private VideosService videosService;

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户名id", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "audioId", value = "背景音乐id", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "歌曲时长", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoDesc", value = "视频描述", required = false,
                    dataType = "String", paramType = "form")
    })
    @ApiOperation(value = "用户上传视频", notes = "用户上传视频的接口")
    @RequestMapping(path="/uploadVideo", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
    public JSONResult uploadVideo(String userId, String audioId,
                                  double videoSeconds, int videoWidth, int videoHeight,
                                  String videoDesc,
                                  @ApiParam(value = "视频接口", required = true) MultipartFile file) throws IOException {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户名不能为空！");
        }

        String FILE_SPACE = "D:/cxx-video";
        String uploadPath = "/" + userId + "/userVideo";
        // 用于生成临时视频
        String tempPath = FILE_SPACE + uploadPath;
        String videoPath = null;
        FileOutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            // 判断上传的相片是否为空
            if(file != null) {
                String filename = file.getOriginalFilename();

                if(StringUtils.isNotBlank(filename)) {
                    videoPath = FILE_SPACE + uploadPath + "/" + filename;
                    // 存在数据库中的路径
                    uploadPath += ("/" + filename);

                    File video = new File(videoPath);
                    // ?
                    if(video.getParentFile() == null || !video.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        video.getParentFile().mkdirs();
                    }
                    outputStream = new FileOutputStream(video);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream,outputStream);
                }
            } else {
                return JSONResult.errorMsg("上传失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传失败！");
        } finally {
            if(outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }

        if (StringUtils.isNoneBlank(audioId)) {
            MergeVideoAudio.convert(FILE_SPACE, tempPath, videoPath, bgmService.queryBgmIfo(audioId).getPath(), videoSeconds);
        }

        // 存入数据库的uploadPath直接填uploadPath
        Videos video = new Videos();
        video.setUserId(userId);
        video.setAudioId(audioId);
        video.setVideoDesc(videoDesc);
        video.setVideoPath(uploadPath);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setStatus(VideoStatusEnum.SUCCESS.value());
        video.setVideoSeconds((float)videoSeconds);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);


        return JSONResult.ok(videoId);
    }

    // 上传视频封面
    @ApiOperation(value = "上传视频封面", notes = "上传视频封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")

    })
    @RequestMapping(path = "/uploadVideoCover", method = RequestMethod.POST)
    // @RequestParam("file")，对应前端的参数name
    public JSONResult uploadFace(String videoId, String userId,
                                 @ApiParam(value = "视频封面", required = true) MultipartFile file) throws IOException {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空！");
        }

        if(StringUtils.isBlank(videoId)) {
            return JSONResult.errorMsg("视频id不能为空！");
        }

        String uploadPath = "/" + userId + "/userVideo";
        String imagePath = null;

        FileOutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            // 判断上传的相片是否为空
            if(file != null) {
                String filename = file.getOriginalFilename();

                if(StringUtils.isNotBlank(filename)) {
                    imagePath = FILE_SPACE + uploadPath + "/" + filename;
                    // 存在数据库中的路径
                    uploadPath += ("/" + filename);

                    File image = new File(imagePath);
                    // ?
                    if(image.getParentFile() == null || !image.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        image.getParentFile().mkdirs();
                    }
                    outputStream = new FileOutputStream(image);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream,outputStream);
                }
            } else {
                return JSONResult.errorMsg("上传失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传失败！");
        } finally {
            if(outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }


        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(uploadPath);
        videoService.updateVideoCoverPath(video);

        return JSONResult.ok();
    }

    /**
     * 分頁搜索查詢視頻列表
     * @param video
     * @param isSaveRecord  = 1 : 保存 , = 0 : 不保存
     * @param page
     * @return
     */
    @RequestMapping(path = "/showAllVideos", method = RequestMethod.POST)
    // @RequestParam("file")，对应前端的参数name
    public JSONResult showAllVideos(@RequestBody Videos video, Integer isSaveRecord, Integer page) {

        if(page == null) {
            page = 1;
        }

        PageResult allVideos = videoService.getAllVideos(video, isSaveRecord, page, PAGE_SIZE);
        return JSONResult.ok(allVideos);
    }

    @RequestMapping(path = "/hot", method = RequestMethod.POST)
    public JSONResult hot() {
        return JSONResult.ok(videoService.getHotWords());
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoCreatorId", value = "视频创建者id", required = true, dataType = "String", paramType = "query")

    })
    @RequestMapping(path = "/userLikeVideo", method = RequestMethod.POST)
    public JSONResult userLikeVideo(String userId, String videoId, String videoCreatorId) {
        System.out.println(userId);
        System.out.println(videoId);
        System.out.println(videoCreatorId);
        videoService.userLikeVideo(userId,videoId,videoCreatorId);
        return JSONResult.ok();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoCreatorId", value = "视频创建者id", required = true, dataType = "String", paramType = "query")

    })
    @RequestMapping(path = "/userUnlikeVideo", method = RequestMethod.POST)
    public JSONResult userUnlikeVideo(String userId, String videoId, String videoCreatorId) {
        videoService.userUnlikeVideo(userId,videoId,videoCreatorId);
        return JSONResult.ok();
    }

    @RequestMapping(path = "/queryVideoInfo", method = RequestMethod.POST)
    public JSONResult queryVideoInfo(String userId, String videoId, String videoCreatorId) {
        boolean res = videoService.queryVideoInfo(userId, videoId, videoCreatorId);
        return JSONResult.ok(res);
    }

    /*
    用户点赞的视频
     */
    @RequestMapping(path = "/showMyLike", method = RequestMethod.POST)
    public JSONResult showMyLike(String userId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户名不能为空！");
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        PageResult pageResult = videoService.queryMyLikeVideos(userId, page, pageSize);
        return JSONResult.ok(pageResult);
    }

    /*
    关注用户发表的视频
     */
    @RequestMapping(path = "/showMyFollowVideos", method = RequestMethod.POST)
    public JSONResult showMyFollowVideos(String userId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户名不能为空！");
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        PageResult pageResult = videoService.queryMyFollowVideos(userId, page, pageSize);
        return JSONResult.ok(pageResult);
    }

    @RequestMapping(path = "/reportVideo", method = RequestMethod.POST)
    public JSONResult reportVideo(@RequestBody UsersReport usersReport) {

        videoService.reportVideo(usersReport);

        return JSONResult.ok();
    }

    @RequestMapping(path = "/saveComment", method = RequestMethod.POST)
    public JSONResult saveComment(@RequestBody Comments comments) {
        videoService.saveComment(comments);
        return JSONResult.ok();
    }

    @RequestMapping(path = "/showComments", method = RequestMethod.POST)
    public JSONResult showComments(String videoId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(videoId)) {
            return JSONResult.errorMsg("");
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        PageResult pageResult = videoService.queryComments(videoId, page, pageSize);
        return JSONResult.ok(pageResult);
    }

}



















