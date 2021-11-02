package edu.uclm.esi.tys2122;

import java.io.BufferedReader;
import java.io.FileReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.uclm.esi.tys2122.http.Manager;
import edu.uclm.esi.tys2122.newchess.ChessMatch;

@SpringBootApplication
public class Importadora {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Importadora.class, args);
		importar();
	}
	
	private static void importar() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("/Users/macariopolousaola/Downloads/ficsgamesdb_200001_chess_nomovetimes_222726.pgn"));
		String linea = br.readLine();
		ChessMatch match=null;
		while(linea != null) {
			linea = br.readLine();
			if (linea.startsWith("[WhiteElo"))
				match.setWhiteElo(linea);
			else if (linea.startsWith("[BlackElo"))
				match.setBlackElo(linea);
			else if (linea.startsWith("[White ")) {
				match = new ChessMatch();
				match.setWhite(linea);
			} else if (linea.startsWith("[Black "))
				match.setBlack(linea);
			else if (linea.startsWith("[Result"))
				match.setResult(linea);
			else if (linea.startsWith("1.")) {
				Manager.get().getChessMatchRepo().save(match);
				match.setMovements(linea);
			}
		}
		br.close();
	}
}
