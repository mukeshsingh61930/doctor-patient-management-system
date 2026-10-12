package com.doctor_patient.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.User;
@Repository
public interface UserRepo extends JpaRepository<User,Long>{

	   boolean existsByUserId(String userId);
	   Optional<User> findByUserId(String userId);
}
