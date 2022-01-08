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
      //Routes
      self.routes = routesFile.getRoutes();

      //Games
      self.games = ko.observableArray([]);
      self.matches = ko.observableArray([]);
      self.tempName = ko.observable(null);

      //Error
      self.globalError = ko.observable(null);

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
        },
        error: function (response) {
          self.globalError(JSON.stringify(response));
        },
      };
      $.ajax(data);
    }

    // BOTH: Joins a game
    joinGame(game) {
      let self = this;

      let data = {
        type: "get",
        url: self.routes.joinGame + game.name + "&" + self.tempName(),
        success: function (response) {
          if (game.name == "Tres en raya") {
            response = {
              ...response,
              x: null,
              y: null,
              gameError: "",
            };
            self.matches.push(response);
            self.conectarAWebSocket();
          }

          if (game.name == "Las damas") {
            response = {
              ...response,
              pieces: [],
              chosenPiece: [],
              playerColor: "BLANCO",
              gameError: "",
            };
            self.matches.push(response);
            self.conectarAWebSocket();
            self.updateAlivePieces(response.id);
          }
        },
        error: function (response) {
          self.globalError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    // BOTH: Syncronizing the game via WebSocket messages
    conectarAWebSocket() {
      let self = this;
      let ws = new WebSocket(`ws://${window.location.origin.split("//")[1]}/${self.routes.webSocket}`);
      ws.onopen = function (event) {};
      ws.onmessage = function (event) {
        let msg = JSON.parse(event.data);
        if (msg.type == "MATCH UPDATE") {
          let gameName = self.matches().find((match) => match.id == msg.matchId).game;
          if (gameName == "CheckersMatch") {
            self.updateAlivePieces(msg.matchId);
          }
          self.reload(msg.matchId);
        }
        if (msg.type == "MATCH READY") self.reload(msg.matchId);
      };
    }

    // BOTH: Make a movement
    mover(match, movement) {
      let self = this;
      let info;
      if (match.game == "TictactoeMatch") {
        info = {
          x: match.x,
          y: match.y,
          matchId: match.id,
        };
      }
      if (match.game == "CheckersMatch") {
        const [movementX, movementY] = self.getSquareByDirection(match.possibleMovementsXY, movement);
        info = {
          pieceId: match.chosenPiece.toString().split(" ")[0],
          pieceColor: match.chosenPiece.toString().split(" ")[1],
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
        success: function (response) {},
        error: function (response) {
          match.gameError = response.responseJSON.message;
          self.updateMatch(match.id, match);
        },
      };
      $.ajax(data);
    }

    // BOTH: Refreshes the match information
    reload(matchId) {
      let self = this;

      let data = {
        type: "get",
        url: self.routes.findMatch + matchId,
        success: function (response) {
          let actualMatch = self.matches().find((match) => match.id == matchId);
          let newMatch;
          if (actualMatch.game == "TictactoeMatch") {
            newMatch = {
              ...response,
              x: null,
              y: null,
              gameError: "",
            };
          }

          if (actualMatch.game == "CheckersMatch") {
            newMatch = {
              ...response,
              pieces: actualMatch.pieces,
              chosenPiece: ["Seleccione..."],
              playerColor: actualMatch.playerColor,
              gameError: "",
            };
          }
          self.updateMatch(matchId, newMatch);
        },
        error: function (response) {
          self.globalError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    // BOTH: Replaces the entire match
    updateMatch(matchId, newMatch) {
      let self = this;
      for (let i = 0; i < self.matches().length; i++)
        if (self.matches()[i].id == matchId) {
          self.matches.splice(i, 1);
          self.matches.splice(i, 0, newMatch);
          break;
        }
    }

    // BOTH: Removes a Match given a matchId
    removeMatch(matchId) {
      let self = this;
      for (let i = 0; i < self.matches().length; i++)
        if (self.matches()[i].id == matchId) {
          self.matches.splice(i, 1);
          break;
        }
    }

    // CHECKERS: Updates select component
    updateAlivePieces(matchId) {
      let self = this;

      let data = {
        type: "get",
        url: self.routes.updateAlivePieces + matchId,
        success: function (response) {
          let actualMatch = self.matches().find((match) => match.id == matchId);

          const [pieces, color] = self.parsePieces(response);
          actualMatch.pieces = pieces;
          actualMatch.chosenPiece = ["Seleccione..."];
          actualMatch.playerColor = color;
          actualMatch.gameError = "";

          self.updateMatch(matchId, actualMatch);
        },
        error: function (response) {
          self.globalError(response.responseJSON.message);
        },
      };
      $.ajax(data);
    }

    // CHECKERS: Prints in a different color the possible places to move a piece
    showPossibleMovements(match) {
      let self = this;
      if (match.chosenPiece != "Seleccione..." && match.chosenPiece.toString().split(" ")[0] != "") {
        let info = {
          matchId: match.id,
          pieceId: match.chosenPiece.toString().split(" ")[0],
          pieceColor: match.chosenPiece.toString().split(" ")[1],
        };
        let data = {
          type: "post",
          url: self.routes.showPossibleMovements,
          data: JSON.stringify(info),
          contentType: "application/json",
          success: function (response) {
            match.board = response.board;
            match.possibleMovements = response.possibleMovements;
            match.possibleMovementsXY = response.possibleMovementsXY;

            match.gameError = match.possibleMovementsXY.every((movement) => movement == null) ? "No se puede mover esta ficha" : "";

            self.updateMatch(match.id, match);
          },
          error: function (response) {
            self.globalError(response.responseJSON.message);
          },
        };
        $.ajax(data);
      }
    }

    // CHECKERS: Auxiliar method that parses pieces information
    parsePieces(response) {
      let alivePieces = [];
      let playerColor;
      alivePieces.push(`Seleccione...`);
      response.map((Piece) => {
        if (Piece != null) {
          let { id, color } = Piece;
          alivePieces.push(`${id} ${color}`);
          playerColor = color;
        }
      });
      return [alivePieces, playerColor];
    }

    // CHECKERS: Given a direction returns the corresponding cell
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

    // CHECKERS: Remove the match that has been won given the match id
    winGame(matchId) {
      let self = this;
      self.removeMatch(matchId);
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
