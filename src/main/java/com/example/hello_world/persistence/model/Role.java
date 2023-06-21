package com.example.hello_world.persistence.model;


import com.example.hello_world.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;


    @Enumerated(EnumType.STRING)
    private RoleType roleType;


    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<User> users;




    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    public Role() {
    }



    public int getId() {
        return id;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }


}
