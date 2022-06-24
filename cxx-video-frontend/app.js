// app.js
App({
  serverUrl: "http://127.0.0.1:8080",
  // serverUrl: "http://45m49320y3.qicp.vip:29073",
  // 用户的全局信息
  userInfo: null,

  setGlobalUserInfo: function(user) {
    wx.setStorageSync('userInfo', user);
  },

  getUserInfo: function() {
    return wx.getStorageSync('userInfo');
  }

})

wx.setInnerAudioOption({
  mixWithOther: true,
  obeyMuteSwitch: false,
  success: function (e) {
    console.log(e)
    console.log('play success')
  },
  fail: function (e) {
    console.log(e)
    console.log('play fail')
  },

  reportReasonArray: [
    "色情低俗",
    "政治敏感",
    "涉嫌诈骗",
    "辱骂谩骂",
    "广告垃圾",
    "引人不适",
    "过于暴力",
    "违法违纪",
    "其他原因",
  ]
})