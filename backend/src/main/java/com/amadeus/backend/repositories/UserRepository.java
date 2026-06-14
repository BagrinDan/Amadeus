package com.amadeus.backend.repositories;

import com.amadeus.backend.models.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{


}
