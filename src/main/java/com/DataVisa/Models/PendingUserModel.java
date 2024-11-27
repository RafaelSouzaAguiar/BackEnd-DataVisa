package com.DataVisa.Models;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios_pendentes")
@Data
@NoArgsConstructor
public class PendingUserModel implements Serializable {
	public PendingUserModel(UserModel userModel){
		this.email = userModel.getEmail();
	    this.nome = userModel.getNome();
	    this.empresaId = userModel.getEmpresaId();
	    this.data_solicitacao = new Date(System.currentTimeMillis());
	}
	
	private static final long serialVersionUID = 1L;

	@Id
	private String email;

	private String nome;

	private Long empresaId;
	
	private Date data_solicitacao;
	

}
