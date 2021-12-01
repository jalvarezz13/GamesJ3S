define(["knockout", "appController", "ojs/ojmodule-element-utils", "accUtils", "jquery"], function (ko, app, moduleUtils, accUtils, $) {
  class ShopViewModel {
    constructor() {
      var self = this;

      self.products = ko.observable([]);

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

    connected() {
      accUtils.announce("Shop page loaded.");
      // document.title = "Login";
      var self = this;
      var data = {
        url: "shop/getProducts",
        type: "get",
        contentType: "application/json",
        success: function (response) {
          self.products(response);
          console.log(response);
        },
        error: function (response) {
          self.error(response.responseJSON.errorMessage);
        },
      };
      $.ajax(data);
    }

    disconnected() {
      // Implement if needed
    }

    transitionCompleted() {
      // Implement if needed
    }
  }

  return ShopViewModel;
});
