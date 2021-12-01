define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class MenuViewModel {
		constructor() {
			let self = this;

			self.games = ko.observableArray([]);
			self.matches = ko.observableArray([]);
			self.error = ko.observable(null);
			self.tempName = ko.observable(null);
			
			self.x = ko.observable(null);
			self.y = ko.observable(null);

			self.pieces = ko.observableArray(["hola","adios","F"]);
			self.chosenPiece = ko.observableArray(["hola"]);
						
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
					console.error(JSON.stringify(response));
					self.error(response.responseJSON.errorMessage);
				}
			}
			$.ajax(data);
		};
		
		mover(match){
			let self = this;
			let info ={
				x : this.x(),
				y : this.y(),
				matchId : match.id
			}
			let data ={
				type : "post",
				url : "/games/move",
				data : JSON.stringify(info),
				contentType : "application/json",
				success: function (response){
					console.log(JSON.stringify(response));
				},
				error: function (response){
					console.error(JSON.stringify(response));
					self.error(response.responseJSON.errorMessage)
				}
			}
			$.ajax(data);
		}
		
		conectarAWebSocket(){
			let self = this;
			let ws = new WebSocket("ws://localhost/wsGenerico");
			ws.onopen = function(event){
				//alert("conexión establecida");
			}
			ws.onmessage = function(event){
				let msg = JSON.parse(event.data);
				if(msg.type == "MATCH UPDATE")
					self.reload(msg.matchId);
				if(msg.type == "MATCH READY")
					self.reload(msg.matchId);
			}
		}
		
		joinGame(game) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/joinGame/" + game.name + "&" + self.tempName(),
				success : function(response) {
					self.matches.push(response);
					self.conectarAWebSocket();
					console.log(JSON.stringify(response));
					//self.updateAlivePieces();
				},
				error : function(response) {
					console.error(JSON.stringify(response));
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		reload(match) {
			let self = this;
			let matchId = match.id ? match.id : match

			let data = {
				type : "get",
				url : "/games/findMatch/" + matchId,
				success : function(response) {
					for (let i=0; i<self.matches().length; i++)
						if (self.matches()[i].id==matchId){ 
							self.matches.splice(i, 1, response);
							break;
						}
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(JSON.stringify(response));
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		updateAlivePieces(matchId) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/updateAlivePieces/" + matchId,
				success : function(response) {
					self.pieces.push(response);
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(JSON.stringify(response));
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		showPiece(){
			let self = this
			alert(self.chosenPiece())
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
