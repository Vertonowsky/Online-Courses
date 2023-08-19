package com.example.vertonowsky.avatar;


import com.example.vertonowsky.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Avatar {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id")
    private int id;

    private String url;

    private String name;

    @OneToMany(mappedBy = "avatar")
    @JsonIgnore
    private Set<User> users;




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (o == null || getClass() != o.getClass()) return false;
        Avatar avatar = (Avatar) o;
        return Objects.equals(url, avatar.url) && Objects.equals(name, avatar.name) && Objects.equals(users, avatar.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, users);
    }
}
