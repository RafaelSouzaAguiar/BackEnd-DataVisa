package com.DataVisa.Models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class TemplateModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String templateName;
	
	private String sqlQuery;
	
	private String tableName;
	
	private int tablePermition;
	
	private List<String> items;
	
	private Timestamp lastModification;

	private Long empresaId;
	
	private Long conexaoId;
	
	private String conexaoName;
	
	private int isActive;
	
}  