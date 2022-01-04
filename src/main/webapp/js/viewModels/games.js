define(["knockout", "appController", "ojs/ojmodule-element-utils", "accUtils", "jquery", "utils/routes"], function (
  ko,
  app,
  moduleUtils,
  accUtils,
  $,
  routesFile
) {
  class MenuViewModel {
    constructor() {
      let self = this;
      self.routes = routesFile.getRoutes();

      self.games = ko.observableArray([]);
      self.matches = ko.observableArray([]);
      self.gameError = ko.observable(null);
      self.tempName = ko.observable(null);

      self.pieces = ko.observableArray([]);
      self.chosenPiece = ko.observableArray([]);

      self.playerColor = ko.observable("BLANCO");

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
        url: self.routes.getGames,
        success: function (response) {
          self.games(response);
          self.gameError("");
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.gameError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    mover(match, movement) {
      let self = this;
      let info;
      const [movementX, movementY] = self.getSquareByDirection(match.possibleMovementsXY, movement);

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
          movementX: movementX.toString(),
          movementY: movementY.toString(),
          direction: movement,
          matchId: match.id,
        };
      }
      let data = {
        type: "post",
        url: self.routes.move,
        data: JSON.stringify(info),
        contentType: "application/json",
        success: function (response) {
          // Select apunta a Seleccione...
          self.chosenPiece(["Seleccione..."]);
          console.log(JSON.stringify(response));
          self.gameError("");
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.gameError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    conectarAWebSocket() {
      let self = this;
      let ws = new WebSocket(`ws://${window.location.origin.split("//")[1]}/${self.routes.webSocket}`);
      ws.onopen = function (event) {
        //alert("conexión establecida");
      };
      ws.onmessage = function (event) {
        let msg = JSON.parse(event.data);
        if (msg.type == "MATCH UPDATE") {
          self.updateAlivePieces(msg.matchId);
          self.reload(msg.matchId);
        }
        if (msg.type == "MATCH READY") self.reload(msg.matchId);
      };
    }

    joinGame(game) {
      let self = this;

      let data = {
        type: "get",
        url: self.routes.joinGame + game.name + "&" + self.tempName(),
        success: function (response) {
          self.matches.push(response);
          self.conectarAWebSocket();
          if (game.name == "Las damas") {
            self.updateAlivePieces(response.id);
          }
          console.log(JSON.stringify(response));
          self.gameError("");
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.gameError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    reload(match) {
      let self = this;
      let matchId = match.id ? match.id : match;

      let data = {
        type: "get",
        url: self.routes.findMatch + matchId,
        success: function (response) {
          for (let i = 0; i < self.matches().length; i++)
            if (self.matches()[i].id == matchId) {
              self.matches.splice(i, 1, response);
              break;
            }
          console.log(JSON.stringify(response));
          self.gameError("");
        },
        error: function (response) {
          console.error(JSON.stringify(response));
          self.gameError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    updateAlivePieces(matchId) {
      let self = this;

      let data = {
        type: "get",
        url: self.routes.updateAlivePieces + matchId,
        success: function (response) {
          //self.pieces([]);
          console.log(self.pieces());
          self.pieces(self.parsePieces(response));
          console.log(self.pieces());
          self.gameError("");
        },
        error: function (response) {
          self.gameError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    showPossibleMovements(match) {
      let self = this;
      if (self.chosenPiece() != "Seleccione..." && self.chosenPiece().toString().split(" ")[0] != "") {
        let info = {
          matchId: match.id,
          pieceId: self.chosenPiece().toString().split(" ")[0],
          pieceColor: self.chosenPiece().toString().split(" ")[1],
        };
        let data = {
          type: "post",
          url: self.routes.showPossibleMovements,
          data: JSON.stringify(info),
          contentType: "application/json",
          success: function (response) {
            for (let i = 0; i < self.matches().length; i++)
              if (self.matches()[i].id == match.id) {
                self.matches.splice(i, 1, response);
                break;
              }
            // Comprueba si es posible mover, si no se puede, imprime eso
            let token = true;
            response.possibleMovementsXY.map((movement) => {
              if (movement != null) {
                token = false;
              }
            });
            if (token) {
              self.gameError("No se puede mover esta ficha");
            } else {
              self.gameError("");
            }
          },
          error: function (response) {
            console.error(JSON.stringify(response));
            self.gameError(response.responseJSON.message);
          },
        };
        $.ajax(data);
      }
    }

    parsePieces(response) {
      let self = this;
      let alivePieces = [];
      alivePieces.push(`Seleccione...`);
      response.map((Piece) => {
        if (Piece != null){
          let { id, color } = Piece;
          alivePieces.push(`${id} ${color}`);
          self.playerColor(color);
        }
      });
      return alivePieces;
    }

    // TODO: Repasar logica al igual que en backend
    getSquareByDirection(squares, movement) {
      switch (movement) {
        case "leftUp":
          return squares[0];
        case "rightUp":
          return squares[1];
        case "rightDown":
          return squares[2];
        case "leftDown":
          return squares[3];
      }
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
