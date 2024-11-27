package com.DataVisa.DTO;

import java.sql.Timestamp;

import com.DataVisa.Models.DBModel;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DatavisaDbDTO {
	public DatavisaDbDTO(DBModel dbModel){
        this.id = dbModel.getId();
        this.nomeConexao = dbModel.getNomeConexao();
        this.tipoDb = dbModel.getTipoDb();
        this.nomeDb = dbModel.getNomeDb();
        this.nomeDb = dbModel.getNomeDb();
        this.usuarioDb = dbModel.getUsuarioDb();
        this.senhaDb = dbModel.getSenhaDb();
        this.hostName = dbModel.getHostName();
        this.portDb = dbModel.getPortDb();
        this.caminhoDb = dbModel.getCaminhoDb();
        this.lastModification = dbModel.getLastModification();
		this.isActive = dbModel.getIsActive();
        this.empresaId = dbModel.getEmpresaId();
    }
	
	public DatavisaDbDTO(DBModel dbModel, String empresaNome) {
		this(dbModel);
        this.empresaNome = empresaNome;
	}
	
	public DatavisaDbDTO(String mensagemRetorno) {
		this.mensagemRetorno = mensagemRetorno;
	}
	

	@Id
	@GeneratedValue
	private Long id;
	
	private String nomeConexao;
	
	private String tipoDb;
	
	//nome teste
	//datavisa
	private String nomeDb;

	//usuario teste
	//root
	private String usuarioDb;
	
	//senha teste
	//1234
	private String senhaDb;
	
	private String hostName;
	
	private int portDb;
	
	private String caminhoDb;
	
	private String empresaNome;
	
	private Long empresaId;
	
	private Timestamp lastModification;
	
	private int isActive;
	
	private String mensagemRetorno;
	
}
