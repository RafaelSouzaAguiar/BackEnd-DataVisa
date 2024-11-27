package com.DataVisa.Models;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "conexoes")
@Data
public class DBModel implements Serializable {

	private static final long serialVersionUID = 1L;

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
	
	private Timestamp lastModification;
	
	private int isActive;
	
	private Long empresaId;
}
