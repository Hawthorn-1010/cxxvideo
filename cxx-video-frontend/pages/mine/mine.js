const app = getApp()

var videoUtils = require("../../utils/videoUtils.js")

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    nickname: '',
    isMe: true,
    isFollow: false,
    authorId: '',
    userId: '',

    videoSelClass: "video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",

    myVideoList: [],
    likeVideoList: [],
    followVideoList: [],

    myWorkFlag: false,
    myLikesFlag: true,
    myFollowFlag: true,

    myVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,

    serverUrl: '',
  },

  // 进入页面时，获取用户信息
  onLoad: function(params) {
    var me = this;
    console.log(params);
    var videoInfo;
    // 这里不能写params，要写params.videoInfo
    if(params.videoInfo != null && params.videoInfo != undefined && params.videoInfo != '') {
      videoInfo = JSON.parse(params.videoInfo);
      if(videoInfo.userId != app.getUserInfo().data.id) {
        me.setData({
          isMe: false,
          authorId: videoInfo.userId,
        })
      }
    }
      var user = app.getUserInfo();
      console.log(user);
      var serverUrl = app.serverUrl;
      var faceUrl;

      console.log(videoInfo);
      var userId;
      // 判断开启的是其他人还是我的主页
      if (videoInfo != null && videoInfo != undefined && videoInfo != '') {
        userId = videoInfo.userId;
        // 获取当前关注状态
        wx.request({
          url: app.serverUrl + '/user/isFollow?userId=' + userId + '&followerId=' + app.getUserInfo().data.id,
          method: "POST",
          success: function(res) {
            console.log(res);
            me.setData({
              isFollow: res.data.data,
            })
          }
        })

      } else {
        userId = user.data.id;
      }
      me.setData({
        userId: userId,
      })
      console.log(userId);
      wx.request({
        url: serverUrl + '/user/query?userId=' + userId,
        method : "POST",
        header: {
          'content-type': 'application/json', // 默认值
          'userId': user.data.id,
          'userToken': user.data.userToken,
        },
        success: function(res) {
          wx.hideLoading();
          console.log(res.data);
          if(res.data.status == 200) {
            user = res.data;
            if(user.data.faceImage == null || user.data.faceImage == '' || user.data.faceImage == undefined) {
              faceUrl = "../resource/images/noneface.png";
            } else {
              faceUrl = serverUrl + user.data.faceImage;
            }
            me.setData({
              faceUrl: faceUrl,
              fansCounts: user.data.fansCounts,
              followCounts: user.data.followCounts,
              receiveLikeCounts: user.data.receiveLikeCounts,
              nickname: user.data.nickname
            })
          } else if(res.data.status == 500) {
            wx.showToast({
              title: res.data.msg,
            })
          } else if (res.data.status == 502) {
            wx.showToast({
              title: res.data.msg,
              duration: 2000,
              icon: 'none',
            })
          }
        }
      })
      // 进入首页后立即显示我的作品
      this.getMyVideoList(1);
  },

  logout: function() {
        var serverUrl = app.serverUrl;
        var user = app.getUserInfo();
        console.log(user.data.id);
        wx.showLoading({
          title: '退出登录中……',
        })
        wx.request({
          url: serverUrl + '/logout?userId=' + user.data.id,
          method: "POST",
          header: {
            'content-type': 'application/json' // 默认值
          },
          success: function(res){
              console.log(user);
              wx.hideLoading();
              console.log(res.data);
              var status = res.data.status;
              if(status == 200) {
                  wx.showToast({
                    title: '退出成功！',
                    icon: 'none',
                    duration: 3000
                  })
                  // 客户端token清除
                  wx.removeStorage({
                    key: 'userInfo',
                  })
                  wx.navigateTo({
                    url: '../userLogin/userLogin',
                  })
              } 
          }
        })
      },
  changeFace: function() {
    // js作用域
    var me = this;
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album'],
      success (res) {
        // tempFilePath可以作为img标签的src属性显示图片
        const tempFilePaths = res.tempFilePaths;
        console.log(tempFilePaths);
        wx.showLoading({
          title: '头像上传中...',
        })
        // 提交图片到后端
        var serverUrl = app.serverUrl;
        var user = app.getUserInfo();
        wx.uploadFile({
          url: serverUrl + '/user/uploadFace?userId=' + user.data.id, 
          filePath: tempFilePaths[0],
          name: 'file',
          success (res){
            // 本来是String，要转成json
            user = JSON.parse(res.data);
            console.log(res.data);
            console.log(user);
            wx.hideLoading();
            if(user.status == 200) {
              wx.showToast({
                title: '头像上传成功!',
                duration: 3000
              })
              var imageUrl = serverUrl + user.data.faceImage;
              console.log(imageUrl);
              me.setData({
                faceUrl: imageUrl
              })
            } else if(res.data.status == 500) {
              wx.showToast({
                title: res.data.msg,
              })
            }
          }
        })
      }
    })
  },

  uploadVideo: function() {
    videoUtils.uploadVideo();
  },

  follow: function() {
    var me = this;
    var authorId = me.data.authorId;
    var followerId = app.getUserInfo().data.id;
    var isFollow = me.data.isFollow;
    var url;

    if (!isFollow) {
      url = app.serverUrl + '/user/addUserFollowers?userId=' + authorId + '&followerId=' + followerId;
    } else {
      url = app.serverUrl + '/user/reduceUserFollowers?userId=' + authorId + '&followerId=' + followerId;
    }

    wx.request({
      url: url,
      method: "POST",
      success: function() {
          if(isFollow) {
            me.setData({
              fansCounts: me.data.fansCounts--,
            })
          } else {
            me.setData({
              fansCounts: me.data.fansCounts++,
            })
          }
          me.setData({
            isFollow: !isFollow,
          })
      }
    })
  },

  // 选中的时候添加底色
  doSelectWork: function () {
    this.setData({
      isSelectedWork: "video-info-selected",
      isSelectedLike: "",
      isSelectedFollow: "",

      myWorkFlag: false,
      myLikesFlag: true,
      myFollowFlag: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyVideoList(1);
  },

  doSelectLike: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "video-info-selected",
      isSelectedFollow: "",

      myWorkFlag: true,
      myLikesFlag: false,
      myFollowFlag: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyLikesList(1);
  },

  doSelectFollow: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "",
      isSelectedFollow: "video-info-selected",

      myWorkFlag: true,
      myLikesFlag: true,
      myFollowFlag: false,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyFollowList(1)
  },

  // 获取我的视频列表
  getMyVideoList: function(page) {
    var me = this;
    var serverUrl = app.serverUrl;
    var userId = me.data.userId;
    var myVideoPage = me.data.myVideoPage;

    // 判断是否是第一页
    if(page != 1) {
      myVideoPage = page;
    }

    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + "/video/showAllVideos?page=" + myVideoPage,
      data: {
        userId: userId,
      },
      method: "POST",
      success: function(res) {
        wx.hideLoading();
        console.log(res);
        // 如果page = 1，设置第一页为空
        if(myVideoPage === 1) {
          me.setData({
            myVideoList: [],
          })
        }

        var myVideoList = res.data.data.rows;
        var newVideoList = me.data.myVideoList;

        me.setData({
          serverUrl: serverUrl,
          myVideoPage: myVideoPage,
          myVideoTotal: res.data.data.total,
          myVideoList: newVideoList.concat(myVideoList),
        })
      },
    })
  },

  // 获取收藏视频列表
  getMyLikesList: function(page) {
    var me = this;
    var serverUrl = app.serverUrl;
    var userId = me.data.userId;
    var likeVideoPage = me.data.likeVideoPage;

        // 判断是否是第一页
        if(page != 1) {
          likeVideoPage = page;
        }

    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + "/video/showMyLike?page=" + likeVideoPage + "&userId=" + userId ,
      method: "POST",
      success: function(res) {
        wx.hideLoading();
        console.log(res);
        // 如果page = 1，设置第一页为空
        if(likeVideoPage === 1) {
          me.setData({
            likeVideoList: [],
          })
        }

        var likeVideoList = res.data.data.rows;
        var newVideoList = me.data.likeVideoList;

        me.setData({
          serverUrl: serverUrl,
          likeVideoPage: likeVideoPage,
          likeVideoTotal: res.data.data.total,
          likeVideoList: newVideoList.concat(likeVideoList),
        })
      },
    })
  },

  // 获取关注者视频列表
  getMyFollowList: function(page) {
    var me = this;
    var serverUrl = app.serverUrl;
    var userId = me.data.userId;
    var followVideoPage = me.data.followVideoPage;

     // 判断是否是第一页
    if(page != 1) {
      followVideoPage = page;
    }

    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + "/video/showMyFollowVideos?page=" + followVideoPage + "&userId=" + userId,
      method: "POST",
      success: function(res) {
        wx.hideLoading();
        console.log(res);
        // 如果page = 1，设置第一页为空
        if(followVideoPage === 1) {
          me.setData({
            followVideoList: [],
          })
        }

        var followVideoList = res.data.data.rows;
        var newVideoList = me.data.followVideoList;

        me.setData({
          serverUrl: serverUrl,
          followVideoPage: followVideoPage,
          followVideoTotal: res.data.data.total,
          followVideoList: newVideoList.concat(followVideoList),
        })
      },
    })
  },

  // 触底
  onReachBottom: function() {
    var me = this;
    var myWorkFlag = me.data.myWorkFlag;
    var myLikesFlag = me.data.myLikesFlag;
    var myFollowFlag = me.data.myFollowFlag;
    var me = this;
    var page;
    var newPage;
    
    if (!myWorkFlag) {
      page = me.data.myVideoPage;
      newPage = page + 1;
      if(page === me.data.myVideoTotal) {
        wx.showToast({
          title: '已经到底了~',
          icon: 'none',
          duration: 1000,
        })
        // 要记得return
        return;
      }
      me.getMyVideoList(newPage);
    } else if (!myLikesFlag) {
      page = me.data.likeVideoPage;
      newPage = page + 1;
      if(page === me.data.likeVideoTotal) {
        wx.showToast({
          title: '已经到底了~',
          icon: 'none',
          duration: 1000,
        })
        // 要记得return
        return;
      }
      me.getMyLikesList(newPage);
    } else if (!myFollowFlag) {
      page = me.data.followVideoPage;
      newPage = page + 1;
      if(page === me.data.followVideoTotal) {
        wx.showToast({
          title: '已经到底了~',
          icon: 'none',
          duration: 1000,
        })
        // 要记得return
        return;
      }
      me.getMyFollowList(newPage);
    }

  },

  // 图片被点击，展示视频详情页
  showVideoInfo: function(e) {
    // console.log(e);
    var index = e.target.dataset.arrindex;
    var me = this;
    var myWorkFlag = me.data.myWorkFlag;
    var myLikesFlag = me.data.myLikesFlag;
    var myFollowFlag = me.data.myFollowFlag;
    var me = this;
    var videoList;

    if (!myWorkFlag) {
        videoList = me.data.myVideoList;
    } else if (!myLikesFlag) {
        videoList = me.data.likeVideoList;
    } else if (!myFollowFlag) {
        videoList = me.data.followVideoList;
    }
    console.log(videoList[index]);
    console.log(videoList);
    var videoInfo = JSON.stringify(videoList[index]);
    console.log(videoInfo);
    wx.navigateTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  }
})
