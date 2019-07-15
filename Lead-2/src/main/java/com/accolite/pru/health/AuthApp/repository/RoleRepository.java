package com.accolite.pru.health.AuthApp.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accolite.pru.health.AuthApp.model.Role;
import com.accolite.pru.health.AuthApp.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {

	//Collection<Role> findByRole(RoleName role);

	
}
