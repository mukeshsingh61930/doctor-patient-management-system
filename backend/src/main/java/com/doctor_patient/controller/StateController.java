package com.doctor_patient.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.entity.State;
import com.doctor_patient.repo.StateRepo;

@RestController
@RequestMapping("/api/v1/states")
public class StateController {

	@Autowired
	StateRepo stateRepo;
	
	@GetMapping("/allState")
	public ResponseEntity<List<State>> getAllState(){
		List<State> allStateList=stateRepo.findAll();
		return new ResponseEntity<>(allStateList,HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getStateById(@PathVariable Long id) {
	    Optional<State> state = stateRepo.findById(id);
	    if (state.isPresent()) {
	        return new ResponseEntity<>(state.get(), HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("State not found", HttpStatus.NOT_FOUND);
	    }
	}
	
}
