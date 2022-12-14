package com.blog.blog.repository;
import com.blog.blog.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findOneByEmailIgnoreCase(String email);
    Optional<User> findOneByUsernameIgnoreCase(String userName);
}
