// pages/videoInfo/videoInfo.js
var videoUtils = require("../../utils/videoUtils.js");
const app = getApp();

Page({
    data: {
        cover: "cover",
        src: "",
        videoInfo: [],
        userLikeVideo: false,
        faceUrl: '',

        commentList: [],
        commentPage: 1,
        commentTotal: 1,
    },

    onLoad: function(params) {
        var me = this;
        console.log(params);
        var videoInfo = JSON.parse(params.videoInfo);
        var videoWidth = videoInfo.videoWidth;
        var videoHeight = videoInfo.videoHeight;
        var cover = "cover";
        var serverUrl = app.serverUrl;
        var userInfo = app.getUserInfo();
        console.log(userInfo);
        if(videoWidth > videoHeight) {
            cover = "";
        }

        me.setData({
            videoInfo: videoInfo,
            src: app.serverUrl + videoInfo.videoPath,
            cover: cover,
        })
        // 用户头像在视频详情页展示
        wx.request({
            url: serverUrl + '/user/query?userId=' + videoInfo.userId,
            method : "POST",
            success: function(res) {
                console.log(res);
              wx.hideLoading();
              console.log(res.data);
              if(res.data.status == 200) {
                if(res.data.data.faceImage != null || res.data.data.faceImage != undefined || res.data.data.faceImage != '') {
                    var faceUrl = serverUrl + res.data.data.faceImage;
                    me.setData({
                      faceUrl: faceUrl,
                    })
                } else {
                    me.setData({
                        faceUrl: "../resource/images/noneface.png",
                    })
                }
              } else if(res.data.status == 500) {
                wx.showToast({
                  title: res.data.msg,
                })
              }
            }
          })

        // 用户点赞情况在视频详情页展示
        wx.request({
            url: serverUrl + '/video/queryVideoInfo?userId=' + userInfo.data.id + "&videoId=" + videoInfo.id + "&videoCreatorId=" + videoInfo.userId,
            method : "POST",
            success: function(res) {
              console.log(res);
              me.setData({
                  userLikeVideo: res.data.data,
              })
            }
          })

          // 获取当前视频的评论
          this.getCommentList(1);
    },

    showSearch: function() {
        wx.navigateTo({
          url: '../searchVideo/searchVideo',
        })
    },

    upload: function() {
        var me = this;
        var userInfo = app.getUserInfo();
        videoUtils.uploadVideo();

        var videoInfo = JSON.stringify(me.data.videoInfo);
        var realUrl = "../videoInfo/videoInfo#videoInfo@" + videoInfo;

        console.log(videoInfo);
        console.log(realUrl);
        if(userInfo == null || userInfo == '' || userInfo == undefined) {
            wx.navigateTo({
              url: '../userLogin/userLogin?bookmarkVideoUrl=' + realUrl,
            })
        }
    },

    showIndex: function() {
        wx.navigateTo({
          url: '../index/index',
        })
    },

    showMine: function() {
        var user = app.getUserInfo();

        if(user == null || user == "" || user == undefined) {
            wx.navigateTo({
              url: '../userLogin/userLogin',
            })
        } else {
            wx.navigateTo({
              url: '../mine/mine',
            })
        }
    },

    likeVideoOrNot: function() {
        var me = this;
        var serverUrl = app.serverUrl;
        var userInfo = app.getUserInfo();
        var videoInfo = me.data.videoInfo;
        console.log(userInfo);
        console.log(videoInfo);
        // 判断用户信息是否为空
        // 原来这里写错了会影响下面的执行吗
        if(userInfo == null || userInfo == "" || userInfo == undefined) {
            wx.navigateTo({
              url: '../userLogin/userLogin',
            })
        }

        console.log(me.data.userLikeVideo);
        if (me.data.userLikeVideo === false) {
            me.setData({
                userLikeVideo: true,
            })
            wx.request({
              url: serverUrl + '/video/userLikeVideo?userId=' + userInfo.data.id + "&videoId=" + videoInfo.id + "&videoCreatorId=" + videoInfo.userId,
              method: "POST",
            //   data: {
            //       userId: userInfo.data.id,
            //       videoId: videoInfo.id,
            //       videoCreatorId: videoInfo.userId,
            //   }
            })
        } else {
            me.setData({
                userLikeVideo: false,
            })
            wx.request({
                url: serverUrl + '/video/userUnlikeVideo?userId=' + userInfo.data.id + "&videoId=" + videoInfo.id + "&videoCreatorId=" + videoInfo.userId,
                method: "POST"
            })
        }
    },

    // 点击视频发布者的头像
    showPublisher: function() {
        var me = this;
        console.log(me.data.videoInfo);
        var videoInfo = JSON.stringify(me.data.videoInfo);
        wx.navigateTo({
          url: '../mine/mine?videoInfo=' + videoInfo,
        })
    },

    // 点击分享按钮
    shareMe: function() {
      var me = this;
      wx.showActionSheet({
        itemList: ["下载到本地","举报该视频"],
        success: function(res) {
          console.log(res.tapIndex);
          if(res.tapIndex == 0) {
            wx.showLoading({
              title: '下载中...',
            })
            // 下载到本地
            wx.downloadFile({
              url: app.serverUrl + me.data.videoInfo.videoPath, //仅为示例，并非真实的资源
              success (res) {
                // 只要服务器有响应数据，就会把响应内容写入文件并进入 success 回调，业务需要自行判断是否下载到了想要的内容
                if (res.statusCode === 200) {
                  console.log(res.tempFilePath);
                  wx.saveVideoToPhotosAlbum({
                    filePath: res.tempFilePath,
                    success (res) {
                      console.log(res.errMsg);
                      wx.hideLoading();
                    }
                  })
                }
              }
            })
          } else if(res.tapIndex == 1) {
            // 举报该视频
          }
        }
      })
    },


    onShareAppMessage: function() {
      var me =this;
      var videoInfo = me.data.videoInfo;

      return {
         title: "视频分享",
         path: "pages/videoInfo/videoInfo?videoInfo=" + JSON.stringify(videoInfo),
      }
    },

    // 评论框获得焦点
    leaveComment: function() {
      this.setData({
        commentFocus: true,
      })
    },

    // 提交评论到后端
    saveComment: function(e) {
      var me = this;
      // 注意这里是转换成字符串后的
      var videoInfo = JSON.stringify(me.data.videoInfo);
      var realUrl = "../videoInfo/videoInfo#videoInfo@" + videoInfo;
      var comment = e.detail.value;
      var user = app.getUserInfo();
      if (user == null || user == undefined || user == '') {
        wx.showToast({
          title: '请登录！',
          icon: "none",
            success: function() {
              setTimeout(function() {
                wx.navigateTo({
                  url: '../userLogin/userLogin?bookmarkVideoUrl=' + realUrl,
                })
              },500) 
            }
        })
      } else {
        wx.showLoading({
          title: '请稍后...',
        })
        wx.request({
          url: app.serverUrl + '/video/saveComment',
          data: {
            videoId: me.data.videoInfo.id,
            toUserId: me.data.videoInfo.userId,
            fromUserId: app.getUserInfo().data.id,
            comment: comment,
          },
          method: "POST",
          success: function(res) {
            wx.hideLoading();
            console.log(res);

            me.setData({
              contentValue: '',
            })
          }
        })
      }
    },

  // 获取当前视频的评论内容
  getCommentList: function(page) {
    var me = this;
    var serverUrl = app.serverUrl;
    var userId = me.data.userId;
    var commentPage = me.data.commentPage;

     // 判断是否是第一页
    if(page != 1) {
      commentPage = page;
    }

    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + "/video/showComments?page=" + commentPage + "&videoId=" + me.data.videoInfo.id,
      method: "POST",
      success: function(res) {
        wx.hideLoading();
        console.log(res);
        // 如果page = 1，设置第一页为空
        if(commentPage === 1) {
          me.setData({
            commentList: [],
          })
        }

        var commentList = res.data.data.rows;
        var newCommentList = me.data.commentList;

        me.setData({
          serverUrl: serverUrl,
          commentPage: commentPage,
          commentTotal: res.data.data.total,
          commentList: newCommentList.concat(commentList),
        })
      },
    })
  },

    onReachBottom: function() {
      var me = this;
      var page;
      var newPage;
      
        page = me.data.commentPage;
        newPage = page + 1;
        if(page === me.data.commentTotal) {
          wx.showToast({
            title: '已经到底了~',
            icon: 'none',
            duration: 1000,
          })
          // 要记得return
          return;
        }
        me.getCommentList(newPage); 
    },


})