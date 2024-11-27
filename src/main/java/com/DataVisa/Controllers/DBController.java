package com.DataVisa.Controllers;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.DataVisa.DTO.DatavisaDbDTO;
import com.DataVisa.DTO.DatavisaSessionDTO;
import com.DataVisa.DTO.DbDTO;
import com.DataVisa.Models.DBModel;
import com.DataVisa.Services.DBService;


@RestController
public class DBController {
	
	@Autowired
	DBService databaseService;
	
	@GetMapping("/dataVisa/database/getDB/{id}")
	public ResponseEntity<DatavisaDbDTO> getDB(@PathVariable Long id){
		Pair<DatavisaDbDTO, HttpStatus> result = databaseService.findById(id);
		return new ResponseEntity<DatavisaDbDTO>(result.getLeft(), result.getRight());
	}
	
	@PostMapping("/dataVisa/database/addDB")
    public ResponseEntity<String> saveDb(@RequestBody DBModel dto){
		Pair<String, HttpStatus> result = databaseService.saveDb(dto);
        return new ResponseEntity<String>(result.getLeft(), result.getRight());
    }
	
	@PutMapping("/dataVisa/database/updateDB")
    public ResponseEntity<String> updateDB(@RequestBody DbDTO dto){
		Pair<String, HttpStatus> result = databaseService.updateDb(dto);
    	return new ResponseEntity<>(result.getLeft(), result.getRight());        
    }
	
    @DeleteMapping("/dataVisa/database/deleteDB/{dbName}")
    public  ResponseEntity<String> deleteDB(@PathVariable String dbName){
    	Pair<String, HttpStatus> result = databaseService.delete(dbName);
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
    }
    
    @GetMapping("/dataVisa/database/getAll")
    public ResponseEntity<?> getAll(){
    	Pair<Object, HttpStatus> result = databaseService.findAll();
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
    
    @GetMapping("/dataVisa/database/getActives")
    public ResponseEntity<?> getActive(){
    	Pair<Object, HttpStatus> result = databaseService.findActives();
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
    
    @PostMapping("/dataVisa/database/testConnection")
	public ResponseEntity<String> Connect(@RequestBody DBModel database){
    	Pair<String, HttpStatus> result =  databaseService.testConnection(database);
		return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
    
    @GetMapping("/dataVisa/database/connect/{nome}")
	public ResponseEntity<DatavisaSessionDTO> Connect(@PathVariable String nome){
    	Pair<DatavisaSessionDTO, HttpStatus> result =  databaseService.setConnection(nome);
		return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
    
}
