package com.example.vertonowsky.avatar;

import com.example.vertonowsky.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface AvatarRepository extends CrudRepository<Avatar, Integer> {

    Set<Avatar> findAll();

    @Query("SELECT a FROM Avatar a LEFT JOIN FETCH a.users u WHERE u = :user")
    Optional<Avatar> findAvatarByUser(@Param("user") User user);

    Optional<Avatar> findById(Integer id);

}
