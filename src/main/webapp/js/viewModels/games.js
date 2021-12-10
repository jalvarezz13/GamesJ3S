define(["knockout", "appController", "ojs/ojmodule-element-utils", "accUtils", "jquery"], function (ko, app, moduleUtils, accUtils, $) {
  class MenuViewModel {
    constructor() {
      let self = this;

      self.games = ko.observableArray([]);
      self.matches = ko.observableArray([]);
      self.error = ko.observable(null);
      self.tempName = ko.observable(null);

      self.pieces = ko.observableArray([]);
      self.chosenPiece = ko.observableArray([]);

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
      accUtils.announce("Juegos.");
      document.title = "Juegos";

      let self = this;

      let data = {
        type: "get",
        url: "/games/getGames",
        success: function (response) {
          self.games(response);
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.error(response.responseJSON.errorMessage);
        },
      };
      $.ajax(data);
    }

    mover(match, movement) {
      let self = this;
      let info;

      if (match.game == "TictactoeMatch") {
        info = {
          x: this.x(),
          y: this.y(),
          matchId: match.id,
        };
      } else {
        info = {
          pieceId: self.chosenPiece().toString().split(" ")[0],
          pieceColor: self.chosenPiece().toString().split(" ")[1],
          movement: movement,
          matchId: match.id,
        };
      }
      let data = {
        type: "post",
        url: "/games/move",
        data: JSON.stringify(info),
        contentType: "application/json",
        success: function (response) {
          console.log(JSON.stringify(response));
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.error(response.responseJSON.errorMessage);
        },
      };
      $.ajax(data);
    }

    conectarAWebSocket() {
      let self = this;
      let ws = new WebSocket("ws://localhost/wsGenerico");
      ws.onopen = function (event) {
        //alert("conexión establecida");
      };
      ws.onmessage = function (event) {
        let msg = JSON.parse(event.data);
        if (msg.type == "MATCH UPDATE") self.reload(msg.matchId);
        if (msg.type == "MATCH READY") self.reload(msg.matchId);
      };
    }

    joinGame(game) {
      let self = this;

      let data = {
        type: "get",
        url: "/games/joinGame/" + game.name + "&" + self.tempName(),
        success: function (response) {
          self.matches.push(response);
          self.conectarAWebSocket();
          if (game.name == "Las damas") {
            self.updateAlivePieces(response.id);
          }
          console.log(JSON.stringify(response));
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.error(response.responseJSON.errorMessage);
        },
      };
      $.ajax(data);
    }

    reload(match) {
      let self = this;
      let matchId = match.id ? match.id : match;

      let data = {
        type: "get",
        url: "/games/findMatch/" + matchId,
        success: function (response) {
          for (let i = 0; i < self.matches().length; i++)
            if (self.matches()[i].id == matchId) {
              self.matches.splice(i, 1, response);
              break;
            }
          console.log(JSON.stringify(response));
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.error(response.responseJSON.errorMessage);
        },
      };
      $.ajax(data);
    }

    updateAlivePieces(matchId) {
      let self = this;

      let data = {
        type: "get",
        url: "/games/updateAlivePieces/" + matchId,
        success: function (response) {
          self.pieces(self.parsePieces(response));
        },
        error: function (response) {
          self.error(response.responseJSON.errormessage);
        },
      };
      $.ajax(data);

      //   Mirar que pasa si se juegan varios juegos a ver si se rellena dos veces las piezas o se sobreescribe
    }

    showPossibleMovements(match) {
      let self = this;
      if (self.chosenPiece() != "Seleccione...") {
        let info = {
          matchId: match.id,
          pieceId: self.chosenPiece().toString().split(" ")[0],
          pieceColor: self.chosenPiece().toString().split(" ")[1],
        };
        let data = {
          type: "post",
          url: "/games/showPossibleMovements",
          data: JSON.stringify(info),
          contentType: "application/json",
          success: function (response) {
            for (let i = 0; i < self.matches().length; i++)
              if (self.matches()[i].id == match.id) {
                self.matches.splice(i, 1, response);
                break;
              }
            console.log(JSON.stringify(response));
          },
          error: function (response) {
            console.error(JSON.stringify(response));
            self.error(response.responseJSON.errorMessage);
          },
        };
        $.ajax(data);
      }
    }

    parsePieces(response) {
      let alivePieces = [];
      alivePieces.push(`Seleccione...`);
      response.map((Piece) => {
        let { id, color } = Piece;
        alivePieces.push(`${id} ${color}`);
      });
      return alivePieces;
    }

    disconnected() {
      // Implement if needed
    }

    transitionCompleted() {
      // Implement if needed
    }
  }

  return MenuViewModel;
});
