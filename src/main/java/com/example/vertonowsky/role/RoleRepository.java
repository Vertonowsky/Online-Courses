package com.example.vertonowsky.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findById(Integer id);

    Optional<Role> findByRoleType(RoleType roleType);

    @Override
    void delete(Role role);

}
