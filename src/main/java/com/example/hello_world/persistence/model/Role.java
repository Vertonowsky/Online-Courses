package com.example.hello_world.persistence.model;


import com.example.hello_world.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
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



    public Role() {
    }

    public Role(RoleType roleType) {
        this.roleType = roleType;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return id == role.id && roleType == role.roleType && Objects.equals(users, role.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleType, users);
    }
}
