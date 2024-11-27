package com.DataVisa.DTO;

import java.util.List;

import com.DataVisa.Models.DBModel;
import com.DataVisa.Models.TableModel;

import lombok.Data;

@Data
public class DbDTO {
	
	DBModel database = new DBModel();
	
	String cargos;
	
	List<TableModel> tablesPermitions;
	
	String mensagemRetorno;

}
