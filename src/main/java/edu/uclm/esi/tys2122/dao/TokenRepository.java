package edu.uclm.esi.tys2122.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uclm.esi.tys2122.model.Token;

public interface TokenRepository extends JpaRepository<Token, String> {

}
