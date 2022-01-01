package edu.uclm.esi.tys2122.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.tys2122.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	public User findByNameAndPwd(String name, String pwd);

	public Optional<User> findById(String id);

	public User findByName(String name);

	public User findByEmail(String email);

	public User findByCookie(String cookieValue);

	@Query(value = "DELETE FROM user WHERE type='temporal'", nativeQuery = true)
	public void deleteTemporalUserDB();

	@Transactional
	@Modifying
	@Query(value = "UPDATE user SET token= :token WHERE email= :email", nativeQuery = true)
	public void setTokenByEmail(@Param("token") String token, @Param("email") String email);

	public User findByToken(String token);

	@Transactional
	@Modifying
	@Query(value = "UPDATE user SET pwd= :pwd WHERE id= :id", nativeQuery = true)
	public void updatePwdById(@Param("pwd") String pwd, @Param("id") String id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE user SET token= null WHERE id= :id", nativeQuery = true)
	public void deleteTokenAfterUse(@Param("id") String id);
}