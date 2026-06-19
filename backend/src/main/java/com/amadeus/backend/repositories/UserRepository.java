package com.amadeus.backend.repositories;

import com.amadeus.backend.models.UserEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findByUsername(String username);
}
