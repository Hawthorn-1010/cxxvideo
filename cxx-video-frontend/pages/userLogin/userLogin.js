const app = getApp();

Page({
    data: {
      realUrl: '',
    },

    onLoad: function(params) {
      var me = this;
      console.log(params);
      if(params.bookmarkVideoUrl != null && params.bookmarkVideoUrl != '' && params.bookmarkVideoUrl != undefined) {
        var realUrl = params.bookmarkVideoUrl;
        realUrl = realUrl.replace(/#/g,"?");
        realUrl = realUrl.replace(/@/g,"=");
        
        me.setData({
          realUrl: realUrl,
        })
      }
    },
    
    doLogin: function(e) {
        var me = this;
        var formObject = e.detail.value;
        var username = formObject.username;
        var password = formObject.password;
        var serverUrl = app.serverUrl;
        if (username.length == 0) {
            wx.showToast({
              title: '用户名不能为空！',
              icon: 'none',
              duration: 3000
            })
        } else if (password.length == 0) {
            wx.showToast({
                title: '密码不能为空！',
                icon: 'none',
                duration: 3000
              })
        } else {
            wx.showLoading({
              title: '登录中……',
            })
            wx.request({
              url: serverUrl + '/login',
              method: "POST",
              data: {
                  username: username,
                  password: password
              },
              header: {
                'content-type': 'application/json' // 默认值
              },
              success: function(res){
                  wx.hideLoading();
                  console.log(res.data);
                  var status = res.data.status;
                  var realUrl = me.data.realUrl;
                  if(status == 200) {
                      wx.showToast({
                        title: '登录成功！',
                        icon: 'none',
                        // duration: 3000,
                        success: function() {
                          setTimeout(function() {
                            if(realUrl != null && realUrl != undefined && realUrl != '') {
                              console.log(realUrl);
                                wx.redirectTo({
                                url: realUrl,
                              })
                            } else {
                                wx.navigateTo({
                                url: '../mine/mine',
                              })
                            }
                          },500) 
                        }
                      })
                      // app.userInfo = res.data;
                      // fixme 修改原有對象為本地緩存
                      app.setGlobalUserInfo(res.data);
                  } else if(status == 500) {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none',
                        duration: 3000
                      })
                  }
              }
            })
        } 

    },
    // 跳转
    goRegisterPage: function(optiom) {
        wx.navigateTo({
          url: '../userRegist/regist',
        })
    }
})