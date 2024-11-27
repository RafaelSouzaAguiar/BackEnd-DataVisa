package com.DataVisa.Models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reports")
@Data
public class ReportModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String reportName;
	
	private String creatorEmail;
	
	private String creatorName;
	
	private String templateName;
	
	private String sqlQuery;
	
	private String selectedLabel;
	
	private String selectedItem;
	
	private String graphType;
	
	private List<String> reportLabels;
	
	private List<String> reportValues;
	
	private Long empresaId;
	
	private Long conexaoId;
	
	private String conexaoName;
	
	private int tablePermition;
	
	private Timestamp creationDate;
	
	private int isPublic;
	
}  