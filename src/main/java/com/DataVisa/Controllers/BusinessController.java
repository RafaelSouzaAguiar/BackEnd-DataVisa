package com.DataVisa.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DataVisa.Models.BusinessModel;
import com.DataVisa.Services.BusinessService;


@RestController
public class BusinessController {
	
	@Autowired
	BusinessService businessService;
    
    @GetMapping("/dataVisa/business/getAll")
	public List<BusinessModel> getAll(){
		return businessService.findAll();
	}
	
}
