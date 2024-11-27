package com.DataVisa.DTO;

import java.sql.Date;

import com.DataVisa.Models.PendingUserModel;
import com.DataVisa.Models.UserModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DatavisaUserDTO {
	public DatavisaUserDTO(UserModel userModel){
        this.email = userModel.getEmail();
        this.nome = userModel.getNome();
        this.senha = userModel.getSenha();
        this.empresaId = userModel.getEmpresaId();
        this.matricula = userModel.getMatricula();
        this.permissaoTabela = userModel.getPermissaoTabela();
        this.nivelAcesso = userModel.getNivelAcesso();
        this.templates = userModel.getTemplates();
    }
	
	public DatavisaUserDTO(PendingUserModel pendingUserModel){
        this.email = pendingUserModel.getEmail();
        this.nome = pendingUserModel.getNome();
        this.empresaId = pendingUserModel.getEmpresaId();
        this.data_solicitacao = pendingUserModel.getData_solicitacao();
    }
	
	public DatavisaUserDTO(String mensagemRetorno) {
		this.mensagemRetorno = mensagemRetorno;
	}
	
	private String email;

	private String nome;
	
	private String senha;

	private Long empresaId;
	
	private String empresaNome;
	
	private String matricula;
	
	private int permissaoTabela;

	private int nivelAcesso;

	private String templates;
	
	private String departamento;
	
	private String departamentos;
	
	private Date data_solicitacao;
	
	private String mensagemRetorno;
	
}
