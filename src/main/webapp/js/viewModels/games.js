define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class MenuViewModel {
		constructor() {
			let self = this;

			self.games = ko.observableArray([]);
			self.matches = ko.observableArray([]);
			self.error = ko.observable(null);

			self.x = ko.observable(null);
			self.y = ko.observable(null);
						
			// Header Config
			self.headerConfig = ko.observable({
				'view' : [],
				'viewModel' : null
			});
			moduleUtils.createView({
				'viewPath' : 'views/header.html'
			}).then(function(view) {
				self.headerConfig({
					'view' : view,
					'viewModel' : app.getHeaderModel()
				})
			});
		}

		mover(match) {
			let self = this;

			let info = {
				x : self.x(),
				y : self.y(),
				matchId : match.id
			}

			let data = {
				type : "post",
				url : "/games/move",
				contentType : 'application/json',
				data : JSON.stringify(info),
				success : function(response) {

				},
				error : function(response) {

				}
			}
			$.ajax(data);
		}

		connected() {
			accUtils.announce('Juegos.');
			document.title = "Juegos";

			let self = this;

			let data = {
				type : "get",
				url : "/games/getGames",
				success : function(response) {
					self.games(response);
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}
			}
			$.ajax(data);
		};

		joinGame(game) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/joinGame/" + game.name,
				success : function(response) {
					if (response.game == "ChessMatch") {
						self.colocarPiezas(response.board);
					}
					self.matches.push(response);
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}
			};
			$.ajax(data);
		}
		
		colocarPiezas(board) {
			let color = "brown";
			for (let i=0; i<board.squares.length; i++) {
				for (let j=0; j<board.squares.length; j++) {
					let square = board.squares[i][j];
					
					board.squares[i][j] = {
						color : color,
						valor : square,
						imagen : null
					};
					
					color = color=="white" ? "brown" : "white";
					switch (board.squares[i][j].valor) {
						case 1 : 
							board.squares[i][j].imagen = "css/images/tb.png";
							break;
						case 2 : 
							board.squares[i][j].imagen = "css/images/cb.png";
							break;
						case -1 : 
							board.squares[i][j].imagen = "css/images/tn.png";
							break;
						case -2 : 
							board.squares[i][j].imagen = "css/images/cn.png";
						default:
							board.squares[i][j].imagen = null;
					}
				}
			}
		}

		reload(match) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/findMatch/" + match.id,
				success : function(response) {
					for (let i=0; i<self.matches().length; i++)
						if (self.matches()[i].id==match.id) 
							self.matches.splice(i, 1, response);
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}
			};
			$.ajax(data);
		}

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return MenuViewModel;
});
