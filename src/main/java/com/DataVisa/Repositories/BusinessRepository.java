package com.DataVisa.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.DataVisa.Models.BusinessModel;

@Repository
public interface BusinessRepository extends JpaRepository<BusinessModel, Long>{
	
}