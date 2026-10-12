package com.doctor_patient.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.entity.Slot;
import com.doctor_patient.repo.SlotRepo;

@RestController
@RequestMapping("/api/v1/slot")
public class SlotController {

	
	@Autowired
	SlotRepo slotRepo;
	
	@PostMapping("/save")
	public ResponseEntity<Slot> savePatient(@RequestBody Slot slot){
		Slot slotSaved=slotRepo.save(slot);
		return new ResponseEntity<>(slotSaved,HttpStatus.OK);
	}
	
	@GetMapping("/allSlot")
	public ResponseEntity<List<Slot>> getAllSlot(){
		List<Slot> allSlotList=slotRepo.findAll();
		return new ResponseEntity<>(allSlotList,HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePatient(@PathVariable Long  id){
		slotRepo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Slot> getSlotById(@PathVariable Long  id){
		Slot slot=slotRepo.findById(id).orElseThrow();
		return new ResponseEntity<>(slot,HttpStatus.OK);
	}
}
