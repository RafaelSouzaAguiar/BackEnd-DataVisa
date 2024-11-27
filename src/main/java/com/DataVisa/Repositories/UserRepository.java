package com.DataVisa.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.DataVisa.Models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String>{

	@Query("SELECT user FROM UserModel user WHERE user.email = ?1 AND user.senha = ?2")
	Optional<UserModel> findByEmailAndSenha(String email, String senha);

	Optional<UserModel> findByEmail(String email);

	@Query("SELECT u FROM UserModel u WHERE u.email = :email AND u.nome = :nome AND u.empresaId = :empresaId  AND u.permissaoTabela = :permissaoTabela AND u.nivelAcesso = :nivelAcesso")
	Optional<UserModel> findByAllFields(
	    @Param("email") String email, 
	    @Param("nome") String nome,
	    @Param("empresaId") Long empresaId,
	    @Param("permissaoTabela") int permissaoTabela,
	    @Param("nivelAcesso") int nivelAcesso
	);
	
	List<UserModel> findAllByEmpresaId(Long empresaId);
	
}