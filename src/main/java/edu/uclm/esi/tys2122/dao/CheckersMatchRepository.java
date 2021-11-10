package edu.uclm.esi.tys2122.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uclm.esi.tys2122.checkers.CheckersMatch;

public interface CheckersMatchRepository extends JpaRepository <CheckersMatch, String> {

}