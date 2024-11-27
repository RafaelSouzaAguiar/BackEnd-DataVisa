package com.DataVisa.Models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class UserModel implements Serializable {
	public UserModel() {
		this.setPermissaoTabela(100);
		this.setNivelAcesso(3);
	}
	private static final long serialVersionUID = 1L;

	//email teste admin
	//pedro@fatec.sp.gov.br
	
	//email teste funcionario
	//rebeca@pizzaria.com
	
	@Id
	private String email;
	
	//senha teste
	//1234
	private String senha;

	private String nome;

	private Long empresaId;
	
	private String matricula;
	
	private int permissaoTabela;

	private int nivelAcesso;
	
	private String templates;
	
	public String getTemplates() {
		if (templates == null)
			return "[]";
		return templates;
	}
	
}
