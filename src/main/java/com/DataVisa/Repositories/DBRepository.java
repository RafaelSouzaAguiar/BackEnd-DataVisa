package com.DataVisa.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.DataVisa.Models.DBModel;
import com.google.common.base.Optional;

@Repository
public interface DBRepository extends JpaRepository<DBModel, Long>{

	List<DBModel> findAllByEmpresaId(Long empresaId);
	
	Optional<DBModel> findByNomeDb(String nomeDb);
	
	Optional<DBModel> findByNomeConexao(String nomeConexao);
}
