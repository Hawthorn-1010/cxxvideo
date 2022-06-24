  // 不是一個對象所以要用原生js寫
  function uploadVideo() {
    var me = this;
    wx.chooseMedia({
      sourceType: ['album'],
      maxDuration: 60,
      success(res) {
        console.log(res)

        var duration = res.tempFiles[0].duration;
        var errMsg = res.tempFiles[0].errMsg;
        var height = res.tempFiles[0].height;
        var size = res.tempFiles[0].size;
        var tempFilePath = res.tempFiles[0].tempFilePath;
        var thumbTempFilePath = res.tempFiles[0].thumbTempFilePath;
        var width = res.tempFiles[0].width;

        if (duration < 2) {
          wx.showToast({
            title: '视频时长太短！',
            icon: 'none',
            duration: 2000
          })
        } else if (duration > 20) {
          wx.showToast({
            title: '视频时长不能超过20秒！',
            icon: 'none',
            duration: 2000
          })
        } else {
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration=' + duration +
              '&height=' + height +
              '&width=' + width +
              '&tempFilePath=' + tempFilePath +
              '&thumbTempFilePath=' + thumbTempFilePath +
              '&thumbTempFilePath=' + thumbTempFilePath,
          })
        }
      }
    })
  }

  module.exports = {
    uploadVideo: uploadVideo,
  }