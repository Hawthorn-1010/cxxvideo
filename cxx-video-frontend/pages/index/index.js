const app = getApp()

Page({
  data: {
    // 用于分页的属性
    screenWidth: 350,  
    videoList: [],
    page: 1,
    totalPage: 1,
    serverUrl : '',
    searchContent: "",
    isSaveRecord: 0,
  },

  onLoad: function (params) {
    var me = this;
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    var page = me.data.page;
    var searchContent = params.search;
    var isSaveRecord = params.isSaveRecord;
    me.setData({
      screenWidth: screenWidth,
      searchContent: searchContent,
      isSaveRecord: isSaveRecord,
    })

    if (isSaveRecord == null || isSaveRecord == '' ||isSaveRecord == undefined) {
      isSaveRecord = 0;
    }

    me.getVideos(page, isSaveRecord);
  },

  getVideos: function(page, isSaveRecord) {
    var me = this;
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + "/video/showAllVideos?page=" + page + "&isSaveRecord=" + isSaveRecord,
      data:{
        videoDesc : me.data.searchContent,
      },
      method: "POST",
      success: function(res) {
        wx.hideLoading();
        console.log(res);
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        // 如果page = 1，设置第一页为空
        if(page === 1) {
          me.setData({
            videoList: [],
          })
        }

        var videoList = res.data.data.rows;
        var newVideoList = me.data.videoList;

        me.setData({
          serverUrl: serverUrl,
          page: page,
          totalPage: res.data.data.total,
          videoList: newVideoList.concat(videoList),
        })

      },

    })
  },

  onReachBottom: function() {
    var me = this;
    var page = me.data.page;
    var newPage = page + 1;
    
    if(page === me.data.totalPage) {
      wx.showToast({
        title: '已经到底了~',
        icon: 'none',
        duration: 1000,
      })
      // 要记得return
      return;
    }

    me.getVideos(newPage, 0);

  },

  onPullDownRefresh: function() {
    this.getVideos(1, 0);
    wx.showNavigationBarLoading();

  },

  showVideoInfo: function(e) {
    var me = this;
    console.log(e);
    var index = e.target.dataset.arrindex;
    var videoList = me.data.videoList;
    console.log(videoList[index]);
    console.log(videoList);
    var videoInfo = JSON.stringify(videoList[index]);
    console.log(videoInfo);
    wx.navigateTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  }

})
