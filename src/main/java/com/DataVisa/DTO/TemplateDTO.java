package com.DataVisa.DTO;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.DataVisa.Models.TemplateModel;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemplateDTO implements Serializable {
	public TemplateDTO(String mensagemRetorno){
		this.mensagemRetorno = mensagemRetorno;
	}
	
	public TemplateDTO(TemplateModel templateModel) {
        this.id = templateModel.getId();
        this.nome = templateModel.getTemplateName();
        this.query = templateModel.getSqlQuery();
        this.tableName = templateModel.getTableName();
        this.tablePermition = templateModel.getTablePermition();
        this.items = templateModel.getItems();
        this.lastModification = templateModel.getLastModification();
        this.empresaId = templateModel.getEmpresaId();
        this.conexaoId = templateModel.getConexaoId();
		this.isActive = templateModel.getIsActive();
    }

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	
	private String query;
	
	private String tableName;
	
	private int tablePermition;
	
	private List<String> items;

	private List<String> values;
	
	private Timestamp lastModification;

	private Long empresaId;
	
	private String conexaoName;
	
	private Long conexaoId;
	
	private String mensagemRetorno;
	
	private int isActive;
}  