define(["knockout", "appController", "ojs/ojmodule-element-utils", "accUtils", "jquery"], function (ko, app, moduleUtils, accUtils, $) {
  class LoginViewModel {
    constructor() {
      var self = this;

      self.userName = ko.observable("pepe");
      self.pwd = ko.observable("pepe123");
      self.message = ko.observable();
      self.error = ko.observable();

      // Header Config
      self.headerConfig = ko.observable({
        view: [],
        viewModel: null,
      });
      moduleUtils
        .createView({
          viewPath: "views/header.html",
        })
        .then(function (view) {
          self.headerConfig({
            view: view,
            viewModel: app.getHeaderModel(),
          });
        });

      $.ajax({
        url: "user/checkCookie",
        type: "get",
        success: function (response) {
          app.router.go({ path: response });
        },
      });
    }

    login(googleUser) {
      var self = this;
      var info;
      if (googleUser) {
        info = {
          name = googleUser.getBasicProfile.getName(),
          email = googleUser.getBasicProfile.getEmail(),
          id = googleUser.getBasicProfile.getId(),
          type = google,
        }
      } else {
        info = {
          name: this.userName(),
          pwd: this.pwd(),
        };
      }
      var data = {
        data: JSON.stringify(info),
        url: "user/login",
        type: "post",
        contentType: "application/json",
        success: function (response) {
          app.router.go({ path: "games" });
        },
        error: function (response) {
          self.error(response.res);
        },
      };
      $.ajax(data);
    }

    resetPassword() {
      app.router.go({ path: "resetPassword" });
    }

    register() {
      app.router.go({ path: "register" });
    }

    connected() {
      accUtils.announce("Login page loaded.");
      // document.title = "Login";

      let divGoogle = document.createElement("div");
      divGoogle.setAttribute("id", "my-signin2");
      document.getElementById("zonaGoogle").appendChild(divGoogle);
      gapi.signin2.render("my-signin2", {
        scope: "profile email",
        width: 150,
        height: 50,
        longtitle: false,
        theme: "dark",
        onsuccess: function (googleUser) {
          localStorage.login3rd = true;
          self.login(googleUser)
        },
        onfailure: function (error) {
          alert(error);
          console.log(error);
        },
      });
    }

    disconnected() {
      // Implement if needed
    }

    transitionCompleted() {
      // Implement if needed
    }
  }

  return LoginViewModel;
});
