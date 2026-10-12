package com.doctor_patient.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.State;

@Repository
public interface StateRepo extends JpaRepository<State,Long>{

}
