package com.example.hello_world.web.service;

import com.example.hello_world.RoleType;
import com.example.hello_world.persistence.model.Role;
import com.example.hello_world.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {


    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public Role createRoleIfNotExists(RoleType roleType) {
        Optional<Role> optionalRole = roleRepository.findByRoleType(roleType);

        if (optionalRole.isEmpty()) {
            Role role = new Role(RoleType.ROLE_USER);
            roleRepository.save(role);
            return role;
        }

        return optionalRole.get();
    }
}
