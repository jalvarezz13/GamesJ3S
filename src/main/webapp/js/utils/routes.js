define([], function () {
    const Routes = {
        register: "user/register",
        login: "user/login",

        changePassword: "user/changePassword",
        resetPassword: "user/resetPassword",
                
        getGames: "/games/getGames",
        move: "/games/move",
        getStatistics: "/games/getStatistics",

        joinGame: "/games/joinGame/",
        findMatch: "/games/findMatch/",
        updateAlivePieces: "/games/updateAlivePieces/",
        showPossibleMovements: "/games/showPossibleMovements",   
        surrender: "/games/surrender/",

        webSocket: "wsGenerico",
    }
    
    self.getRoutes = function() {
        return Routes
    }

    return {getRoutes: getRoutes};
});