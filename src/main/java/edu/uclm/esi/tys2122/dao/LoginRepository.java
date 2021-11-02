package edu.uclm.esi.tys2122.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uclm.esi.tys2122.model.Login;

public interface LoginRepository extends JpaRepository <Login, String> {

}
