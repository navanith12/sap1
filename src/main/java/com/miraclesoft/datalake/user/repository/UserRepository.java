package com.miraclesoft.datalake.user.repository;

import com.miraclesoft.datalake.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByusername(String username);
    
//    @Transactional
//    @Query(value ="select u.id from users u  where u.username=:username LIMIT 1", nativeQuery = true)
    Boolean existsByUsername(@Param("username")String username);
    
//    @Transactional
//    @Query(value = "select u.email from users u where u.email=:email LIMIT 1", nativeQuery = true)
    Boolean existsByEmail(@Param("email")String email);
    
    @Transactional
    @Query(value = "select * from users u where u.id in (select user_id from user_roles where role_id = '2')", nativeQuery = true)
    List<User> getBusiness();
    @Transactional
    @Query(value = "select * from users u where u.id in (select user_id from user_roles where role_id = '1')", nativeQuery = true)
    List<User> getUser();
    @Transactional
    @Query(value = "select * from users u where u.id in (select user_id from user_roles where role_id = '3')", nativeQuery = true)
    List<User> getAdmin();
    @Transactional
    @Query(value = "select * from users u where u.username = :username ", nativeQuery = true)
    User getUserEmail(@Param("username") String username);
}
