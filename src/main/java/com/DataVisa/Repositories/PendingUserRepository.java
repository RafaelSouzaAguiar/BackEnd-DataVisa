package com.DataVisa.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.DataVisa.Models.PendingUserModel;


@Repository
public interface PendingUserRepository extends JpaRepository<PendingUserModel, String>{
	
	List<PendingUserModel> findAllByEmpresaId(Long empresaId);
}