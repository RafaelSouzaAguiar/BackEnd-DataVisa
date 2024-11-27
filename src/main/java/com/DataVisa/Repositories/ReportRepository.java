package com.DataVisa.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.DataVisa.Models.ReportModel;

@Repository
public interface ReportRepository extends JpaRepository<ReportModel, Long>{

	List<ReportModel> findAllByEmpresaId(Long empresaId);
	
	@Query("SELECT r FROM ReportModel r WHERE " +
	           "(r.creatorEmail = :email OR " +
	           "(r.empresaId = :empresaId AND r.isPublic = 1 AND r.tablePermition <= :permissaoTabela))")
    List<ReportModel> findActives(@Param("empresaId") Long empresaId, 
	                                     @Param("email") String email, 
	                                     @Param("permissaoTabela") int permissaoTabela);
}