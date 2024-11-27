package com.DataVisa.Services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.DataVisa.DTO.DatavisaDbDTO;
import com.DataVisa.DTO.DatavisaSessionDTO;
import com.DataVisa.DTO.DbDTO;
import com.DataVisa.Models.DBModel;
import com.DataVisa.Models.TableModel;
import com.DataVisa.Repositories.DBRepository;
import com.DataVisa.Session.DatavisaSession;
import com.google.common.base.Optional;

@Service
public class DBService{
	public DBService(@Value("${spring.datasource.url}") String url, 
			@Value("${spring.datasource.username}") String username, 
			@Value("${spring.datasource.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
	
	@Autowired
	DBRepository databaseRepository;
	
	@Autowired
	DatavisaSession datavisaSession;
	
	@Autowired
	UserService userService;

	@Autowired
	TableSawService tableSawService;
	
	private final String url;
	
	private final String username;
	
	private final String password;
	
	public Pair<String, HttpStatus> saveDb(DBModel database) {
		
		Pair<String, HttpStatus> response;;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
		
		try {
			
			//Verifica se o banco já existe
			if (database.getId() != null && databaseRepository.findById(database.getId()).isPresent()) {
				throw new IllegalArgumentException("Banco já cadastrado.");
			}
			
			database = databaseRepository.save(database);
			setConnection(database.getNomeConexao());
			
			//cria a tabela de controle de permissão
			tableSawService.addPermitionsTable(database);
			
			 response = Pair.of("Banco cadastrado com sucesso!",HttpStatus.OK);
			
		} catch (Exception ex){
			Optional<DBModel> db = databaseRepository.findByNomeDb(database.getNomeDb());
			if (db.isPresent()) {
				databaseRepository.delete(database);
			}
			response = Pair.of("Ocorreu um erro, Banco não cadastrado! " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			return response; 
		}
		return response;
	}
	
	public Pair<String, HttpStatus> updateDb(DbDTO dto) {
		
		Pair<String, HttpStatus> response;;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
		
		try {
			Optional<DBModel> db = databaseRepository.findByNomeConexao(dto.getDatabase().getNomeConexao());
			//Verifica se o banco já existe
			if (!db.isPresent()) {
				throw new IllegalArgumentException("Banco não cadastrado.");
			}
			
			dto.getDatabase().setId(db.get().getId());
			dto.getDatabase().setLastModification(Timestamp.from(Instant.now()));
			dto.setDatabase(databaseRepository.save(dto.getDatabase()));
			
			tableSawService.updatePermitionsTable(dto.getDatabase().getNomeConexao(), dto);
			
			 response = Pair.of("Banco atualizado com sucesso!", HttpStatus.OK);
			
		} catch (Exception ex){
			response = Pair.of("Ocorreu um erro, Banco não cadastrado! " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			return response; 
		}
		return response;
	}

	public Pair<String, HttpStatus> delete(String nomeConexao){
		Pair<String, HttpStatus> response;;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response);
	    }
		
		try {
			
			Optional<DBModel> dbModel = databaseRepository.findByNomeConexao(nomeConexao);
			//Verifica se o banco existe
			if (!dbModel.isPresent()) {
                throw new RuntimeException("Banco não encontrado.");
            }
			
			DBModel database = dbModel.get();
			tableSawService.deletePermitionsTable(database);
			databaseRepository.delete(database);
			
			//Verifica se o banco foi excluido
            if (databaseRepository.findById(database.getId()).isPresent()) {
                throw new RuntimeException("Falha ao excluir o banco.");
            }
            
		} catch (Exception ex){
			return Pair.of("Erro: Banco de dados não excluído! \nErro: " + ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
		return Pair.of("Banco excluído com sucesso!",HttpStatus.OK);
	}

	public Pair<Object, HttpStatus> findAll(){
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response, response.getRight());
	    }

	    List<DBModel> dbList = datavisaSession.getEmpresaId().equals(1L) ?  
	    		databaseRepository.findAll() : 
    			databaseRepository.findAllByEmpresaId(datavisaSession.getEmpresaId());
	    
	    try {
	        List<DatavisaDbDTO> dtoList = dbList.stream().map(dbModel -> {
	            DatavisaDbDTO dto = new DatavisaDbDTO(dbModel);
	            try {
	                String nomeEmpresa = tableSawService.getNomeEmpresa(dbModel.getEmpresaId());
	                dto.setEmpresaNome(nomeEmpresa);
	            } catch (Exception e) {
	            	throw new RuntimeException("Erro ao obter nome da empresa");
	            }
	            return dto;
	        }).collect(Collectors.toList());

	        return Pair.of(dtoList, HttpStatus.OK);
	    } catch (Exception e) {
	        return Pair.of("Erro ao processar a lista de bancos de dados \n" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	public Pair<Object, HttpStatus> findActives(){
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(3)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response, response.getRight());
	    }

	    List<DBModel> dbList = datavisaSession.getEmpresaId().equals(1L) ?  
	    		databaseRepository.findAll() : 
    			databaseRepository.findAllByEmpresaId(datavisaSession.getEmpresaId());
	    
	    try {
	        List<DatavisaDbDTO> dtoList = dbList.stream()
	        		.filter(dbModel -> dbModel.getIsActive() != 0)
	        		.map(dbModel -> {
	            DatavisaDbDTO dbDTO = new DatavisaDbDTO(dbModel);
	            try {
	                String nomeEmpresa = tableSawService.getNomeEmpresa(dbModel.getEmpresaId());
	                dbDTO.setEmpresaNome(nomeEmpresa);
	            } catch (Exception e) {
	            	throw new RuntimeException("Erro ao obter nome da empresa");
	            }
	            return dbDTO;
	        }).collect(Collectors.toList());

	        return Pair.of(dtoList, HttpStatus.OK);
	    } catch (Exception e) {
	        return Pair.of("Erro ao processar a lista de bancos de dados \n" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	

	public  Pair<DatavisaDbDTO, HttpStatus> findById(Long id){
		Pair<String, HttpStatus> response;
		DatavisaDbDTO datavisaDbResponse;
		
		
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
			datavisaDbResponse =  new DatavisaDbDTO(response.getLeft());
	        return Pair.of(datavisaDbResponse, response.getRight());
	    }
		 try {
			 
			 
			 DBModel db = databaseRepository.findById(id).get();
			 if (!datavisaSession.getEmpresaId().equals(db.getEmpresaId()) && !datavisaSession.getEmpresaId().equals(1L)){
				datavisaDbResponse = new DatavisaDbDTO("Erro: O usuário não pertence a empresa correspondente ao banco de dados informado.");
	    		return  Pair.of(datavisaDbResponse, HttpStatus.FORBIDDEN);
			 }
			 
			 if (!(response = datavisaSession.checkDatavisaPermition(3)).getRight().equals(HttpStatus.ACCEPTED)) {
				 datavisaDbResponse = new DatavisaDbDTO(response.getLeft());
		    		return  Pair.of(datavisaDbResponse, response.getRight());
		    	}
			 
			 datavisaDbResponse = new DatavisaDbDTO(db, tableSawService.getNomeEmpresa(db.getEmpresaId()));
		 } catch (Exception e) {
			 datavisaDbResponse= new DatavisaDbDTO("Banco de dados não encontrado");
		    return Pair.of(datavisaDbResponse,HttpStatus.NOT_FOUND);
	    }
		 	
		    return Pair.of(datavisaDbResponse,HttpStatus.OK);
	}

	public Pair<DatavisaSessionDTO, HttpStatus> setConnection(String nome) {
		
		Pair<String, HttpStatus> response;
		DatavisaSessionDTO datavisaConnectionResponse;
		
		
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
			datavisaConnectionResponse =  new DatavisaSessionDTO(response.getLeft());
	        return Pair.of(datavisaConnectionResponse, response.getRight());
	    }
		
		datavisaConnectionResponse = new DatavisaSessionDTO(datavisaSession);
		
		try {
			DBModel db = databaseRepository.findByNomeConexao(nome).get();
			if(db.getEmpresaId().equals(datavisaSession.getEmpresaId()) || datavisaSession.getEmpresaId().equals(1L)) {
				setSessionConection(db);
				datavisaConnectionResponse.setConexaoAtiva(true);
				datavisaConnectionResponse.setConexao(db.getNomeConexao());
				datavisaConnectionResponse.setNomeDb(db.getNomeDb());
				datavisaConnectionResponse.setMensagemRetorno("Banco " + db.getNomeConexao() + " selecionado!");
				return Pair.of(datavisaConnectionResponse, HttpStatus.OK);
			}
		} catch (NoSuchElementException e) {
			datavisaConnectionResponse.setMensagemRetorno("Conexão não efetuada! \nErro: A conexão informada não existe!");
			return  Pair.of(datavisaConnectionResponse, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			datavisaConnectionResponse.setMensagemRetorno("Conexão não efetuada! \nErro: " + e.getMessage() + " " + e.getClass().toString());
			return Pair.of(datavisaConnectionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		datavisaConnectionResponse.setMensagemRetorno("Conexão não efetuada! \nErro: Usuário não percence a empresa desta conexão");
		return Pair.of(datavisaConnectionResponse, HttpStatus.FORBIDDEN);
	}
	
	
	public Pair<String, HttpStatus> testConnection(DBModel db) {
		
		Pair<String, HttpStatus> response;
		
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return response;
	    }
		if (!(response = datavisaSession.checkDatavisaPermition(2)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return response;
	    }
		
		try (Connection connection = DriverManager.getConnection(createDbUrl(db), db.getUsuarioDb(), db.getSenhaDb())){
			return Pair.of("Teste de conexão executado com sucesso!", HttpStatus.OK);
			
		} catch (NoSuchElementException e) {
			return  Pair.of("Conexão não efetuada! \nErro: A conexão informada não existe!", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return Pair.of("Conexão não efetuada! \nErro: " + e.getMessage() + " " + e.getClass().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public Pair<List<TableModel>, HttpStatus> load(Long id) {
		
		return null;
	}

	public Connection DatavisaConnection() throws SQLException{
		
		return DriverManager.getConnection(url, username, password);
	}

	public void setSessionConection(DBModel db) {
		datavisaSession.setConexao(db.getId());				
		datavisaSession.setDbUrl(createDbUrl(db));
		datavisaSession.setDbUser(db.getUsuarioDb());
		datavisaSession.setNomeConexao(db.getNomeConexao());
		datavisaSession.setDbName(db.getNomeDb());
		datavisaSession.setDbPassword(db.getSenhaDb());
		
		datavisaSession.setConexaoAtiva(true);
	}
	
	public String createDbUrl (DBModel db) {
		return "jdbc:"+ db.getTipoDb().toLowerCase() + "://"+ db.getHostName() +":" + String.valueOf(db.getPortDb()) + db.getCaminhoDb();
	}
	
}
