define([
  "knockout",
  "appController",
  "ojs/ojmodule-element-utils",
  "accUtils",
  "jquery",
], function (ko, app, moduleUtils, accUtils, $) {
  class ResetPasswordViewModel {
    constructor() {
      var self = this;

      self.email = ko.observable("pepe@pepe.com");
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
        url: "user/resetPassword",
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
