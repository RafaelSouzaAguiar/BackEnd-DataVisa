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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.DataVisa.DTO.DatavisaSessionDTO;
import com.DataVisa.DTO.DatavisaUserDTO;
import com.DataVisa.Models.UserModel;
import com.DataVisa.Services.UserService;


@RestController
//@CrossOrigin(origins = "*", allowedHeaders = {"email", "senha"})
public class UserController {
	
	@Autowired
	UserService userService;
	
	
	@GetMapping("/dataVisa/user/login")
	public ResponseEntity<DatavisaSessionDTO> login(@RequestHeader("email") String email, @RequestHeader("senha") String senha){
		Pair<DatavisaSessionDTO, HttpStatus> result = userService.login(email, senha);
	    return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
	
	@GetMapping("/dataVisa/user/logout")
	public ResponseEntity<String> logout(){
		Pair<String, HttpStatus> result =  userService.logout();
		return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
	
	@GetMapping("/dataVisa/user/getUser/{email}")
	public ResponseEntity<DatavisaUserDTO> getUser(@PathVariable String email){
		Pair<DatavisaUserDTO, HttpStatus> result = userService.findById(email);
		return new ResponseEntity<DatavisaUserDTO>(result.getLeft(), result.getRight());
	}
	
	@PostMapping("/dataVisa/user/addUser")
    public ResponseEntity<String> addUser(@RequestBody UserModel user){
		Pair<String, HttpStatus> result = userService.create(user);
        return new ResponseEntity<>(result.getLeft(), result.getRight());
    }
	
	@PutMapping("/dataVisa/user/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody UserModel user){
		Pair<String, HttpStatus> result = userService.update(user);
    	return new ResponseEntity<>(result.getLeft(), result.getRight());        
    }
	
    @DeleteMapping("/dataVisa/user/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody UserModel user){
    	Pair<String, HttpStatus> result = userService.delete(user);
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
    }
    
    @GetMapping("/dataVisa/user/getAll")
	public ResponseEntity<?> getAll(){
    	Pair<Object, HttpStatus> result = userService.findAll();
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
    
    @GetMapping("/dataVisa/user/getAllPending")
	public ResponseEntity<?> getAllPending(){
    	Pair<Object, HttpStatus> result = userService.getAllPending();
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
	}
    
    @PutMapping("/dataVisa/user/aprovePendingUser")
    public ResponseEntity<String> aprovePendingUser(@RequestBody DatavisaUserDTO user){
		Pair<String, HttpStatus> result = userService.aprovePendingUser(user);
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
    }
    @DeleteMapping("/dataVisa/user/refusePendingUser")
    public ResponseEntity<String> refusePendingUser(@RequestBody DatavisaUserDTO user){
		Pair<String, HttpStatus> result = userService.refusePendingUser(user);
    	return new ResponseEntity<>(result.getLeft(), result.getRight());
    }
	
	@RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
	public ResponseEntity<?> handleOptions() {         
		return ResponseEntity.ok().build(); 
	}
}
