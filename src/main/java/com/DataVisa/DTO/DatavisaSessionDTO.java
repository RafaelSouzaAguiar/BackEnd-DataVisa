package com.DataVisa.DTO;

import com.DataVisa.Session.DatavisaSession;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatavisaSessionDTO  {
	
	 public DatavisaSessionDTO(DatavisaSession session) {
	        // Inicializa as variáveis da classe mãe com os valores da instância fornecida
	        this.setStatus(session.isStatus());
	        this.setEmail(session.getEmail());
	        this.setNome(session.getNome());
	        this.setNivelAcesso(session.getNivelAcesso());
	        this.setEmpresa(session.getEmpresaNome());
	        this.setEmpresaId(session.getEmpresaId());
	        this.setConexaoAtiva(session.isConexaoAtiva());
	        this.setDepartamento(session.getDepartamento());
	        this.setTemplates(session.getTemplates());
	    }
	 
	 public DatavisaSessionDTO(String mensagemRetorno) {
	        this.setMensagemRetorno(mensagemRetorno);
	    }
	 
	private boolean status;
	
	private String email;
	
	private String nome;
	
	private String empresa;

	private Long empresaId;

	private boolean conexaoAtiva;
	
	private String conexao;
	
	private String nomeDb;

	private String departamento;
	
	private String templates;
	
	private int nivelAcesso;

	private String mensagemRetorno;
}
