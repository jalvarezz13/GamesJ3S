define(["knockout", "appController", "ojs/ojmodule-element-utils", "accUtils", "jquery"], function (ko, app, moduleUtils, accUtils, $) {
  class ChangePasswordViewModel {
    constructor() {
      var self = this;

      self.newPass = ko.observable();
      self.newPass2 = ko.observable();
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

    changePassword() {
      var self = this;
      let {href} = window.location
      var info = {
        newPass: self.newPass(),
        newPass2: self.newPass2(),
        token: href.split("/").at(-1),
      };
      var data = {
        data: JSON.stringify(info),
        url: `user/changePassword`,
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
      accUtils.announce("Change password page loaded.");
      // document.title = "Login";
    }

    disconnected() {
      // Implement if needed
    }

    transitionCompleted() {
      // Implement if needed
    }
  }

  return ChangePasswordViewModel;
});
