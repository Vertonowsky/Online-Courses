package com.example.vertonowsky.avatar;

import com.example.vertonowsky.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AvatarRepository extends CrudRepository<Avatar, Integer> {

    @Query("SELECT a FROM Avatar a ORDER BY a.name")
    List<Avatar> findAll();

    @Query("SELECT a FROM Avatar a LEFT JOIN FETCH a.users u WHERE u = :user")
    Optional<Avatar> findAvatarByUser(@Param("user") User user);

    Optional<Avatar> findById(Integer id);

}
