package edu.uclm.esi.tys2122.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uclm.esi.tys2122.newchess.ChessMovement;

public interface MovementRepository extends JpaRepository <ChessMovement, String> {

}
