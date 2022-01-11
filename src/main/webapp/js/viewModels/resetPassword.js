define([
  "knockout",
  "appController",
  "ojs/ojmodule-element-utils",
  "accUtils",
  "jquery",
  "utils/routes"
], function (ko, app, moduleUtils, accUtils, $, routesFile) {
  class ResetPasswordViewModel {
    constructor() {
      var self = this;

      self.routes = routesFile.getRoutes();

      self.email = ko.observable("");
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
    }

    goLogin() {
      app.router.go({ path: "login" });
    }

    resetPassword() {
      var self = this;
      var info = {
        email: this.email(),
      };
      var data = {
        data: JSON.stringify(info),
        url: self.routes.resetPassword,
        type: "post",
        contentType: "application/json",
        success: function (response) {
          self.message(response);
          self.error("");
        },
        error: function (response) {
          self.message("");
          self.error(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    connected() {
      accUtils.announce("Reset password page loaded.");
      // document.title = "Login";
    }

    disconnected() {
      // Implement if needed
    }

    transitionCompleted() {
      // Implement if needed
    }
  }

  return ResetPasswordViewModel;
});
