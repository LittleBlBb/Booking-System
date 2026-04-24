package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bookingsystem.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    List<User> findByCompanyId(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User findByActivationCode(String code);

}
