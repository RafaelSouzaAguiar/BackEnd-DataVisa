package com.DataVisa.Services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.DataVisa.DTO.TemplateDTO;
import com.DataVisa.Models.TemplateModel;
import com.DataVisa.Repositories.DBRepository;
import com.DataVisa.Repositories.TemplateRepository;
import com.DataVisa.Session.DatavisaSession;
import com.DataVisa.Utils.DatavisaUtils;

@Service
public class TemplateService{
	
	@Autowired
	TemplateRepository templateRepository;
	
	@Autowired
	DatavisaSession datavisaSession;	
	
	@Autowired
	DBService dBService;
	
	@Autowired
	DBRepository dbRepository;
	
	@Autowired
	TableSawService tableSawService;

	public Pair<String, HttpStatus> addTemplate(TemplateModel template) {
		
		Pair<String, HttpStatus> response;;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
		
		try {
			if (datavisaSession.getConexao() == null) {
				response = Pair.of("Ocorreu um erro, Template não cadastrado! \nErro: Conexão não selecionada." , HttpStatus.BAD_REQUEST);
				return response; 
			}
				
			template.setConexaoId(datavisaSession.getConexao());
			template.setConexaoName(datavisaSession.getNomeConexao());;
			template.setTableName(DatavisaUtils.tableNameMapper(template.getSqlQuery()));
			template.setEmpresaId(dbRepository.findById(datavisaSession.getConexao()).get().getEmpresaId());
			template.setTablePermition(tableSawService.getTablePermition(template.getTableName(), template.getConexaoName()));
			template.setLastModification(Timestamp.from(Instant.now()));
			
			//Verifica se o template já existe
			if (templateRepository.findByName(template.getTemplateName(), template.getEmpresaId()) != null) {
				throw new IllegalArgumentException("Template já existente.");
			}
			
			templateRepository.save(template);
			response = Pair.of("Template cadastrado com sucesso!",HttpStatus.OK);
			return response;
			
		} catch (Exception ex){
			response = Pair.of("Ocorreu um erro, Template não cadastrado! \nErro: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			return response; 
		}
	}
	
	public Pair<String, HttpStatus> updateTemplate(TemplateModel template) {
		
		Pair<String, HttpStatus> response;;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
		
		try {
			
			if (datavisaSession.getConexao() == null) {
				response = Pair.of("Ocorreu um erro, Template não atualizado! \nErro: Conexão não selecionada." , HttpStatus.BAD_REQUEST);
				return response; 
			}
			
			//Verifica se o Template já existe
			if (templateRepository.findById(template.getId(), datavisaSession.getEmpresaId()) == null) {
				throw new IllegalArgumentException("Template não cadastrado.");
			}
			
			template.setConexaoId(datavisaSession.getConexao());
			template.setConexaoName(datavisaSession.getNomeConexao());;
			template.setTableName(DatavisaUtils.tableNameMapper(template.getSqlQuery()));
			template.setEmpresaId(dbRepository.findById(datavisaSession.getConexao()).get().getEmpresaId());
			template.setTablePermition(tableSawService.getTablePermition(template.getTableName(), template.getConexaoName()));
			template.setLastModification(Timestamp.from(Instant.now()));
			templateRepository.updateTemplate(template);
			
			response = Pair.of("Banco atualizado com sucesso!", HttpStatus.OK);
			
		} catch (Exception ex){
			response = Pair.of("Falha ao atualizar o template no banco de dados. \nErro: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			return response; 
		}
		return response;
	}

	public Pair<String, HttpStatus> delete(String nomeTemplate){
		Pair<String, HttpStatus> response;;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
		
		try {
			
			TemplateModel template = templateRepository.findByName(nomeTemplate, datavisaSession.getEmpresaId());
			
			//Verifica se o template existe
			if (template == null) {
                throw new RuntimeException("Template não encontrado.");
            }
			
			templateRepository.delete(template);
			
			//Verifica se o template foi excluido
            if (templateRepository.findById(template.getId(), template.getEmpresaId()) != null) {
                throw new RuntimeException("Falha ao excluir o template.");
            }
            
		} catch (Exception ex){
			return Pair.of("Template não excluído! \nErro: " + ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
		return Pair.of("Template excluído com sucesso!",HttpStatus.OK);
	}

	public  Pair<TemplateDTO, HttpStatus> findById(Long id){
		Pair<String, HttpStatus> response;
		TemplateDTO templateResponse;
		
		
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
			templateResponse =  new TemplateDTO(response.getLeft());
	        return Pair.of(templateResponse, response.getRight());
	    }
		 try {
			 
			 
			 templateResponse = new TemplateDTO(templateRepository.findById(id , datavisaSession.getEmpresaId()));
			 templateResponse.setConexaoName(dbRepository.findById(templateResponse.getConexaoId()).get().getNomeConexao());
			 if (!datavisaSession.getEmpresaId().equals(templateResponse.getEmpresaId()) && !datavisaSession.getEmpresaId().equals(1L)){
				templateResponse = new TemplateDTO("Erro: O usuário não pertence a empresa correspondente ao template informado.");
	    		return  Pair.of(templateResponse, HttpStatus.FORBIDDEN);
			 }
			 
		 } catch (Exception e) {
			 templateResponse= new TemplateDTO("Template não encontrado");
		    return Pair.of(templateResponse,HttpStatus.NOT_FOUND);
	    }
		 	
		    return Pair.of(templateResponse,HttpStatus.OK);
	}
	
	public Pair<Object, HttpStatus> getAll(){
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response, response.getRight());
	    }
	    try {
		    List<TemplateModel> templatesResponse = templateRepository.getAll(datavisaSession.getEmpresaId());
		    List<TemplateDTO> dtos = new ArrayList<>();
		    for (TemplateModel template : templatesResponse) {
	            TemplateDTO dto = new TemplateDTO(template);
	            dto.setConexaoName(dbRepository.findById(template.getConexaoId()).get().getNomeConexao());
	            dtos.add(dto);
	        }
		  
	        return Pair.of(dtos, HttpStatus.OK);
	    } catch (Exception e) {
	    	return Pair.of("Erro ao buscar a lista de templates! \nErro: ",HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	public Pair<Object, HttpStatus> getActives(){
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response, response.getRight());
	    }
	    try {
		    List<TemplateModel> templatesResponse = templateRepository.getAll(datavisaSession.getEmpresaId());
		    List<TemplateDTO> dtos = new ArrayList<>();
		    for (TemplateModel template : templatesResponse) {
		    	if (template.getIsActive() == 1 && template.getTablePermition() >= datavisaSession.getPermissaoTabela()) {
		            TemplateDTO dto = new TemplateDTO(template);
		            dto.setConexaoName(dbRepository.findById(template.getConexaoId()).get().getNomeConexao());
		            dtos.add(dto);
		    	}
	        }
		  
	        return Pair.of(dtos, HttpStatus.OK);
	    } catch (Exception e) {
	    	return Pair.of("Erro ao buscar a lista de templates! \nErro: ",HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	public Pair<TemplateDTO, HttpStatus> validateQuery(String query){
		TemplateDTO dto = new TemplateDTO();
		Pair<String, HttpStatus> response ;
		
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED) || 
				!(response = datavisaSession.checkConnection()).getRight().equals(HttpStatus.ACCEPTED)) {
			dto.setMensagemRetorno(response.getLeft());
	        return Pair.of(dto, response.getRight());
		}
		
		try {
			query = DatavisaUtils.sanitizeQuery(query);
			String limitedQuery = DatavisaUtils.limitQueryToOne(query);
			
			dto.setQuery(query);
			String tableName = DatavisaUtils.tableNameMapper(query);
			dto.setTableName(tableName);
			List<String> items = DatavisaUtils.tableFieldsMapper(query);
			dto.setItems(items);
			List<String> valores = IntStream.range(0, items.size())
			        .mapToObj(index -> tableSawService.extractCustomizesdCollumnFields(limitedQuery, tableName, index))
			        .collect(Collectors.toList());
		    dto.setValues(valores);
		    dto.setEmpresaId(datavisaSession.getEmpresaId());
		    dto.setConexaoId(datavisaSession.getConexao());
		    
		    if(dto.getTableName() == null || dto.getItems().isEmpty()){
		    	 throw new IllegalArgumentException("Query inválida.");
		    }
		    
		    dto.setMensagemRetorno("Query válida.");
			return Pair.of(dto, HttpStatus.OK);
		
		}catch (IllegalArgumentException e) {
			dto = new TemplateDTO();
			dto.setMensagemRetorno(e.getMessage());
			return Pair.of(dto, HttpStatus.BAD_REQUEST);
		}
	}
	
}