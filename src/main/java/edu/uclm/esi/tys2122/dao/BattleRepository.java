package edu.uclm.esi.tys2122.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.tys2122.model.Match;
import edu.uclm.esi.tys2122.model.User;

public interface BattleRepository extends JpaRepository<Match, String> {

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO battle (dtype,id,looser_id, winner_id) VALUES (:game, :id, :looser, :winner)", nativeQuery = true)
	public void saveMatch(String game, String id, User looser, User winner);

	@Query(value = "SELECT COUNT(IF(looser_id=:id && dtype='CheckersMatch',1, null)) as looserc, COUNT(IF(winner_id=:id && dtype='CheckersMatch',1,null)) as winnerc,	COUNT(IF(looser_id=:id && dtype='TictactoeMatch',1, null)) as loosert, COUNT(IF(winner_id=:id && dtype='TictactoeMatch',1,null)) as winnert FROM battle", nativeQuery = true)
	public Object[] getStatistics(String id);
}
