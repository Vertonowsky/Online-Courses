package com.example.vertonowsky.role;

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
