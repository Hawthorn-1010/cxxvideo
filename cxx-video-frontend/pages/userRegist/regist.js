const app = getApp()

Page({
    data: {

    },

    doRegist: function(e) {
        var formObject = e.detail.value;
        var username = formObject.username;
        var password = formObject.password;

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
            var serverUrl = app.serverUrl;
            wx.showLoading({
              title: '注册中……',
            })
            wx.request({
              url: serverUrl + '/register',
              method: "POST",
              data: {
                  username: username,
                  password: password
              },
              header: {
                'content-type': 'application/json' // 默认值
              },
              // 回调函数
              success: function(res) {
                  console.log(res.data);
                  wx.hideLoading();
                  var status = res.data.status;
                  if(status == 200) {
                    wx.showToast({
                      title: '注册成功！\r\n正在为您跳转登录...',
                      icon: "none",
                      success: function() {
                        setTimeout(function(){
                          wx.navigateTo({
                            url: '../userLogin/userLogin',
                          })
                        },1000)
                      }
                    })
                      // app.userInfo = res.data;
                      // fixme 修改原有對象為本地緩存
                      app.setGlobalUserInfo(res.data);
                  } else if (status == 500) {
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
    goLoginPage: function(optiom) {
      wx.navigateTo({
        url: '../userLogin/userLogin',
      })
  }
})