package com.DataVisa.Models.Inferfaces;

import java.sql.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class ArtifactModel {

	@Id
	@GeneratedValue
	private Long id;
	
	private String nome;
	
	private String query;
	
	private Long empresaId;
	
	private Date dataCriacao;
} 