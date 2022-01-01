define(["knockout", "appController", "ojs/ojmodule-element-utils", "accUtils", "jquery", "utils/routes"], function (ko, app, moduleUtils, accUtils, $, routesFile) {
  class LoginViewModel {
    constructor() {
      var self = this;

      self.routes = routesFile.getRoutes();

      self.userName = ko.observable("pepe");
      self.pwd = ko.observable("pepe123");
      self.message = ko.observable();
      self.error = ko.observable();

      self.googleUser = undefined;

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

    login() {
      var self = this;
      var info;
      if (self.googleUser) {
        info = {
          name: self.googleUser.getBasicProfile().getName(),
          email: self.googleUser.getBasicProfile().getEmail(),
          id: self.googleUser.getBasicProfile().getId(),
          type: "google",
        };
      } else {
        info = {
          name: this.userName(),
          pwd: this.pwd(),
        };
      }
      var data = {
        data: JSON.stringify(info),
        url: self.routes.login,
        type: "post",
        contentType: "application/json",
        success: function (response) {
          app.router.go({ path: "games" });
        },
        error: function (response) {
          self.error(response.responseJSON.message);
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

      var self = this;
      let divGoogle = document.createElement("div");
      divGoogle.setAttribute("id", "my-signin2");
      document.getElementById("zonaGoogle").appendChild(divGoogle);
      gapi.signin2.render("my-signin2", {
        scope: "profile email",
        width: 150,
        height: 50,
        longtitle: false,
        theme: "dark",
        onsuccess: function (response) {
          self.googleUser = response;
          localStorage.login3rd = true;
          self.login();
        },
        onfailure: function (error) {
          alert(error);
          console.log(error);
          self.googleUser = undefined;
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
