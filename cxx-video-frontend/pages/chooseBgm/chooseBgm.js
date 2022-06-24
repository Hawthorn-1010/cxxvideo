const app = getApp()

// 旧版本js
// Page({
//     data: {
//       bgmList: [],
//       serverUrl: "",
//       isplay: false,
//     },

//     onLoad: function() {
//       var serverUrl = app.serverUrl;
//       var me = this;
//       wx.request({
//         url: serverUrl + '/bgm/queryBgm',
//         method: "POST",
//         success: function(res){
//             console.log(res.data);
//             var status = res.data.status;
//             if(status == 200) {
//               var bgmList = res.data.data;
//               me.setData({
//                 bgmList: bgmList,
//                 serverUrl: serverUrl
//               })
//             } else if(status == 500) {
//               wx.showToast({
//                   title: res.data.msg,
//                   icon: 'none',
//                   duration: 3000
//                 })
//             }
//         }
//       })
//     },
// })

// 新版本js
var innerAudioContext = wx.createInnerAudioContext();
Page({
 
  data: {
    isplay: false,//是否播放
    bgmList: [],
    serverUrl: "",
    preIndex: -1,
    videoParams: {},
    bgmId: 0,
    desc: '',
    videoId: '',
  },

    onLoad: function(params) {
      var me = this;
      console.log(params);
      me.setData({
        videoParams: params,
      })
      var serverUrl = app.serverUrl;
      var me = this;
      wx.request({
        url: serverUrl + '/bgm/queryBgm',
        method: "POST",
        success: function(res){
            console.log(res.data);
            var status = res.data.status;
            if(status == 200) {
              var bgmList = res.data.data;
              me.setData({
                bgmList: bgmList,
                serverUrl: serverUrl
              })
            } else if(status == 500) {
              wx.showToast({
                  title: res.data.msg,
                  icon: 'none',
                  duration: 3000
                })
            }
        }
      })
    },

  //播放
  play: function (e) {
    var me = this;
    var serverUrl = app.serverUrl;
    var userInfo = app.getUserInfo();
    // 这里要加data
    var bgmList = me.data.bgmList;
    console.log(bgmList);
    //获取下一个播放的音频index
    var index = e.currentTarget.dataset.index;
    if (index != -1) {
      innerAudioContext.destroy();
      innerAudioContext = wx.createInnerAudioContext();
      console.log(index);
      innerAudioContext.src = serverUrl + bgmList[index].path;
      innerAudioContext.onCanplay(function() {
        innerAudioContext.play();
      })
    } else {
      innerAudioContext.play();
    }
    me.setData({
      isplay: true,
    })
  },
  // 停止
  pause: function () {
    innerAudioContext.pause();
    this.setData({ isplay: false });
  },

  upLoad: function(e) {
    console.log(e);
    console.log(app.getUserInfo());
    var me = this;
    var serverUrl = app.serverUrl;
    var thumbTempFilePath = me.data.videoParams.thumbTempFilePath;
    var tempFilePath = me.data.videoParams.tempFilePath;

    me.setData({
      bgmId: e.detail.value.bgmId,
      desc: e.detail.value.desc,
    })
        // tempFilePath可以作为img标签的src属性显示图片
        wx.showLoading({
          title: '视频上传中...',
        })
        // 提交视频到后端
        var serverUrl = app.serverUrl;
        var userInfo = app.getUserInfo();
        wx.uploadFile({
          url: serverUrl + '/video/uploadVideo', 
          formData: {
            userId: userInfo.data.id,
            audioId: me.data.bgmId,
            videoSeconds: me.data.videoParams.duration,
            videoWidth: me.data.videoParams.width,
            videoHeight: me.data.videoParams.height,
            videoDesc: me.data.desc,
          },
          filePath: tempFilePath,
          name: 'file',
          success: function(res) {
            console.log(res);
            var resData = JSON.parse(res.data);
            wx.hideLoading();
            me.setData({
              videoId: resData.data,
            })
            if(resData.status == 200) {
              // 上传视频封面
              wx.uploadFile({
                url: serverUrl + '/video/uploadVideoCover',
                formData: {
                videoId: me.data.videoId,
                userId: userInfo.data.id,
                },
                name: 'file',
                filePath: thumbTempFilePath,
                success: function(res) {
                  console.log(res);
                }
              }) 
              wx.showToast({
                title: '视频上传成功！',
                icon: 'none',
                success: function() {
                  setTimeout(function(){
                    wx.navigateBack({
                      delta: 1,
                    })
                  },1000)
                }
              })
            } else {
              wx.showToast({
                title: '视频上传失败！',
                icon: 'none',
              })
            }
          }

        })
    
  }
 
})
