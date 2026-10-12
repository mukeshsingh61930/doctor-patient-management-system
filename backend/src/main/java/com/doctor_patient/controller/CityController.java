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

import com.doctor_patient.entity.City;
import com.doctor_patient.entity.State;
import com.doctor_patient.repo.CityRepo;


@RestController
@RequestMapping("/api/v1/cities")
public class CityController {
	
	@Autowired
	CityRepo cityRepo;

	@GetMapping("/stateId/{stateId}")
	public List<City> getByStateId(@PathVariable Long stateId) {
		return this.cityRepo.getCitiesByStateId(stateId);
	}
	
	@GetMapping("/allCity")
	public ResponseEntity<List<City>> getAllCity(){
		List<City> allCityList=cityRepo.findAll();
		return new ResponseEntity<>(allCityList,HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getCityById(@PathVariable Long id) {
	    Optional<City> city = cityRepo.findById(id);
	    if (city.isPresent()) {
	        return new ResponseEntity<>(city.get(), HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("City not found", HttpStatus.NOT_FOUND);
	    }
	}


}
