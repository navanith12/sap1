package com.miraclesoft.datalake.user.repository;

import com.miraclesoft.datalake.user.model.Role;
import com.miraclesoft.datalake.user.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
