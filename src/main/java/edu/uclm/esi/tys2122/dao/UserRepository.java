package edu.uclm.esi.tys2122.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.tys2122.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	public User findByNameAndPwd(String name, String pwd);
	public Optional<User> findByName(String name);
	public User findByEmail(String email);

	@Query(value = "DELETE FROM user WHERE temp='YES'", nativeQuery = true)
	public void deleteTemporalUserDB();
}
