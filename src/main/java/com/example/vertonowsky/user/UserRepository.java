package com.example.vertonowsky.user;

import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.topic.model.FinishedTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.registrationMethod = :registrationMethod")
    Optional<User> findByEmail(String email, RegistrationMethod registrationMethod);

    @Query("SELECT u.finishedTopics FROM User u WHERE u = :user")
    Iterable<FinishedTopic> findFinishedTopics(@Param("user") User user);

    @Override
    void delete(User user);


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.finishedTopics LEFT JOIN FETCH u.coursesOwned LEFT JOIN FETCH u.paymentHistories LEFT JOIN FETCH u.codes WHERE u.email = :email")
    Optional<User> findByEmailAllData(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.finishedTopics WHERE u.email = :email")
    Optional<User> findByEmailWithFinishedTopics(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.coursesOwned AS co LEFT JOIN FETCH co.course WHERE u.email = :email")
    Optional<User> findByEmailWithCoursesOwned(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.finishedTopics LEFT JOIN FETCH u.coursesOwned WHERE u.email = :email")
    Optional<User> findByEmailWithFinishedTopicsAndCoursesOwned(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.finishedTopics LEFT JOIN FETCH u.coursesOwned AS co LEFT JOIN FETCH co.course AS c LEFT JOIN FETCH c.chapters as chapters LEFT JOIN FETCH chapters.topics LEFT JOIN FETCH u.paymentHistories WHERE u.email = :email")
    Optional<User> findByEmailWithFinishedTopicsAndCoursesOwnedAndPaymentHistories(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.paymentHistories WHERE u.email = :email")
    Optional<User> findByEmailWithPaymentHistories(String email);

}
