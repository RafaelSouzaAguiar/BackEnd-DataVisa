package com.DataVisa.Services;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.DataVisa.DTO.DbDTO;
import com.DataVisa.Repositories.TableRepository;
import com.DataVisa.Session.DatavisaSession;

@Service
public class DatavisaTableService {

	@Autowired
	TableRepository tableRepository;

	@Autowired
	TableSawService tableService;
	
	@Autowired
	DatavisaSession datavisaSession;	

	public Pair<DbDTO, HttpStatus> findTablesPermitions() {
		DbDTO dto = new DbDTO();
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
			dto.setMensagemRetorno(response.getLeft());
	        return Pair.of(dto,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
			dto.setMensagemRetorno(response.getLeft());
	        return Pair.of(dto,  response.getRight());
	    }
		
		if (!datavisaSession.isConexaoAtiva()) {
			dto.setMensagemRetorno("Erro: Nenhuma conexão ativa");
	        return Pair.of(dto,  HttpStatus.FORBIDDEN);
		}
		dto.setTablesPermitions(tableRepository.findAll(datavisaSession.getNomeConexao()));
		dto.getDatabase().setNomeDb(datavisaSession.getNomeConexao());
		dto.setCargos(tableService.getDatavisaCollumnFields(datavisaSession.getEmpresaNome() + "_permissoes", "nome"));
		return  Pair.of(dto, HttpStatus.OK);
	}
	
}

