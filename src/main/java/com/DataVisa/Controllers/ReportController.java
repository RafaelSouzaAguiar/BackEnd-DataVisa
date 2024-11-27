package com.DataVisa.Controllers;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.DataVisa.DTO.ReportDTO;
import com.DataVisa.Models.ReportModel;
import com.DataVisa.Services.ReportService;


@RestController
//@CrossOrigin(origins = "*", allowedHeaders = {"email", "senha"})
public class ReportController {
	
	@Autowired
	ReportService reportService;
	
	
	@PostMapping("/dataVisa/report/addReport")
	public ResponseEntity<Object> add(@RequestBody ReportModel report){
		Pair<Object, HttpStatus> result = reportService.create(report);
	    return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
	
	@GetMapping("/dataVisa/report/getReport/{id}")
	public ResponseEntity<ReportDTO> get(@PathVariable Long id){
		Pair<ReportDTO, HttpStatus> result = reportService.getReport(id);
		return new ResponseEntity<ReportDTO>(result.getLeft(), result.getRight());
	}
	
    @DeleteMapping("/dataVisa/report/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
    	Pair<String, HttpStatus> result = reportService.delete(id);
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
    }
    
    @GetMapping("/dataVisa/report/getActives")
	public ResponseEntity<?> getActives(){
    	Pair<Object, HttpStatus> result = reportService.getActives();
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
    
}
