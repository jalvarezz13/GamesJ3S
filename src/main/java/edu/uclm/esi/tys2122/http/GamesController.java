package edu.uclm.esi.tys2122.http;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.checkers.CheckersMatch;
import edu.uclm.esi.tys2122.checkers.CheckersPiece;
import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.Match;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.services.GamesService;
import edu.uclm.esi.tys2122.services.UserService;

@RestController
@RequestMapping("games")
public class GamesController extends CookiesController {

	/* Attributes */

	@Autowired
	private GamesService gamesService;

	@Autowired
	private UserService userService;

	/* Routes */

	@GetMapping("/getGames")
	public List<Game> getGames(HttpSession session) throws Exception {
		return gamesService.getGames();
	}

	@GetMapping("/joinGame/{header}")
	public Match joinGame(HttpSession session, @PathVariable String header) {
		String gameName = header.split("&")[0];
		String tempName = header.split("&")[1];

		User user;
		if (session.getAttribute("user") != null) {
			user = (User) session.getAttribute("user");
		} else {
			if (tempName.equals("null"))
				tempName = "anonimo" + new SecureRandom().nextInt(1000);
			String tempEmail = tempName + "@" + tempName + ".es";
			user = new User(tempName, tempEmail);
			try {
				userService.save(user);
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Este nombre ya esta registrado por otro usuario");
			}
			session.setAttribute("user", user);
		}

		Manager.get().add(session);

		Game game = Manager.get().findGame(gameName);
		if (game == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encuentra el juego " + gameName);

		Match match = getMatch(game);

		if (!match.getPlayers().isEmpty() && match.getPlayers().get(0).getId() == user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes iniciar más partidas mientras tienes una en espera.");
		}

		match.addPlayer(user);
		if (match.isReady()) {
			game.getPendingMatches().remove(match);
			game.getPlayingMatches().add(match);
		}
		gamesService.put(match);
		return match;
	}

	@PostMapping("/move")
	public Match move(HttpSession session, @RequestBody Map<String, Object> movement) throws Exception {
		User user = (User) session.getAttribute("user");
		JSONObject jso = new JSONObject(movement);
		Match match = gamesService.getMatch(jso.getString("matchId"));
		match.move(user.getId(), jso);
		return match;
	}

	@GetMapping("/findMatch/{matchId}")
	public Match findMatch(@PathVariable String matchId) {
		return gamesService.getMatch(matchId);
	}

	@GetMapping("/updateAlivePieces/{matchId}")
	public CheckersPiece[] updateAlivePieces(@PathVariable String matchId, HttpSession session) {
		User user = (User) session.getAttribute("user");
		CheckersMatch match = (CheckersMatch) gamesService.getMatch(matchId);
		return match.getAlivePieces(user.getId());
	}

	@PostMapping("/showPossibleMovements")
	public Match showPossibleMovements(@RequestBody Map<String, Object> pieceInfo) {
		JSONObject jso = new JSONObject(pieceInfo);
		CheckersMatch match = (CheckersMatch) gamesService.getMatch(jso.getString("matchId"));
		CheckersMatch auxMatch = match.getPossibleMovements((String) jso.get("pieceId"), (String) jso.get("pieceColor"));
		auxMatch.setId(match.getId());
		auxMatch.setPlayers(match.getPlayers());
		auxMatch.setPlayerWithTurn(match.getPlayerWithTurn());
		auxMatch.setReady(match.isReady());
		auxMatch.setWinner(match.getWinner());
		auxMatch.setLooser(match.getLooser());
		auxMatch.setDraw(match.isDraw());

		return auxMatch;
	}
	
	@GetMapping("/surrender/{matchId}")
	public void surrender(HttpSession session, @PathVariable String matchId) throws Exception {
		User user = (User) session.getAttribute("user");
		Match auxMatch = gamesService.getMatch(matchId);
		auxMatch.closeMatchByUser(user);
	}

	/* Functions */

	private Match getMatch(Game game) {
		Match match;
		if (game.getPendingMatches().isEmpty()) {
			match = game.newMatch();
			game.getPendingMatches().add(match);
		} else {
			match = game.getPendingMatches().get(0);
		}
		return match;
	}
}
