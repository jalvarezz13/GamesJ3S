define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class MenuViewModel {
		constructor() {
			let self = this;

			self.games = ko.observableArray([]);
			self.matches = ko.observableArray([]);

			self.x = ko.observable(null);
			self.y = ko.observable(null);

			self.error = ko.observable(null);
						
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
					console.error(response);
					self.error(response);
				}
			}
			$.ajax(data);
		};

		mover(match) {
			let self = this;

			let info = {
				matchId : match.id,
				x : this.x(),
				y : this.y()
			};

			let data = {
				type : "post",
				url : "/games/move",
				data : JSON.stringify(info),
				contentType : "application/json",
				success : function(response) {
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(response);
					self.error(response);
				}
			}
			$.ajax(data);
		}

		conectarAWebSocket() {
			let ws = new WebSocket("ws://localhost/wsGenerico");
			ws.onopen = function(event) {
				alert("Conexión establecida");
			}
			ws.onmessage = function(event) {
				let msg = JSON.parse(event.data);
			}
		}

		joinGame(game) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/joinGame/" + game.name,
				success : function(response) {
					self.matches.push(response);
					self.conectarAWebSocket();
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}
			};
			$.ajax(data);
		}

		reload(match) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/findMatch/" + match.id,
				success : function(response) {
					for (let i=0; i<self.matches().length; i++)
						if (self.matches()[i].id==match.id) {
							self.matches.splice(i, 1, response);
							break;
						}
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
