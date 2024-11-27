package com.DataVisa.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.DataVisa.DTO.DatavisaSessionDTO;
import com.DataVisa.DTO.DatavisaUserDTO;
import com.DataVisa.Models.PendingUserModel;
import com.DataVisa.Models.UserModel;
import com.DataVisa.Repositories.PendingUserRepository;
import com.DataVisa.Repositories.UserRepository;
import com.DataVisa.Session.DatavisaSession;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	DatavisaSession datavisaSession;
	
	@Autowired
	@Lazy
	TableSawService tableService;
	
	@Autowired
	PendingUserRepository pendingUserRepository;
	
public Pair<DatavisaSessionDTO, HttpStatus> login(String email, String senha){
		
		DatavisaSessionDTO datavisaResponse = new DatavisaSessionDTO(datavisaSession);
		if (datavisaSession.checkStatus().getRight().equals(HttpStatus.ACCEPTED)) {
			datavisaResponse.setMensagemRetorno("Usuario já logado!"
					+ "\nUsuário: " + datavisaSession.getNome());
			return Pair.of(datavisaResponse, HttpStatus.CONFLICT);
		}
		
		if(pendingUserRepository.findById(email).isPresent()) {
			datavisaResponse =  new DatavisaSessionDTO("Aguardando aprovação do acesso por parte da empresa.");
			return Pair.of(datavisaResponse, HttpStatus.FORBIDDEN);
		}
		
		try{
			UserModel user= userRepository.findByEmailAndSenha(email, senha).get();
			
			datavisaSession.startSession(user);
			datavisaSession.setEmpresaNome(tableService.getNomeEmpresa(user.getEmpresaId()));
			datavisaSession.setDepartamento(tableService.getDepartamento(user.getPermissaoTabela(), datavisaSession.getEmpresaNome()));
			
			datavisaResponse = new DatavisaSessionDTO(datavisaSession);
			
			datavisaResponse.setMensagemRetorno("Login efetuado com sucesso!"
					+ "\nUsuário: " + datavisaSession.getNome());
			
			return Pair.of(datavisaResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			datavisaSession.setStatus(false);
			datavisaResponse.setStatus(false);
			datavisaResponse.setMensagemRetorno("Credenciais inválidas!");
			return Pair.of(datavisaResponse, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	public Pair<String, HttpStatus> logout(){
		if (datavisaSession.isStatus()) {
			datavisaSession.setStatus(false);
			datavisaSession.setConexaoAtiva(false);
			return Pair.of("Logout realizado com sucesso!", HttpStatus.OK);
		}
		return Pair.of("Erro: Usuário não logado.", HttpStatus.BAD_REQUEST);
	}
	
	public Pair<String, HttpStatus> create(UserModel user) {
		
		try {
			//Verifica se o usuário já existe
			if (userRepository.findByEmail(user.getEmail()).isPresent()) {
				if(pendingUserRepository.findById(user.getEmail()).isPresent()) {
					return Pair.of("Aguardando aprovação do acesso por parte da empresa.", HttpStatus.FORBIDDEN);
				}
				throw new IllegalArgumentException("Usuário já existente.");
			}
			
			PendingUserModel pendingUser = new PendingUserModel(user); 
			user.setEmpresaId(2L);
			userRepository.save(user);
			pendingUserRepository.save(pendingUser);
			
		} catch (Exception ex){
			 return Pair.of("Usuário não cadastrado! \nErro: " + ex.getMessage(), HttpStatus.NOT_FOUND); 
		}
		return Pair.of("Usuário cadastrado com sucesso!", HttpStatus.OK);
	}
	
	public Pair<String, HttpStatus> update(UserModel user) {
		
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED))
			return Pair.of(response);
		if (!(response = datavisaSession.checkDatavisaPermition(3)).getRight().equals(HttpStatus.ACCEPTED))
			return Pair.of(response);
		
		try {
			
			//Verifica se o registro existe
			if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
				return Pair.of("Erro: Usuário informado não encontrado. Verifique os valores fornecidos.", HttpStatus.NOT_FOUND);
			}
			
			userRepository.save(user);
			
		} catch (Exception ex){
			 return Pair.of("Falhao ao atualizar usuário! \nErro: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
		}
		return Pair.of("Usuário atualizado com sucesso! As informações serão atualizadas ao realizar login novamente no usuário alterado.", HttpStatus.OK);
	}


	public Pair<String, HttpStatus> delete(UserModel user){
		
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED))
			return Pair.of(response);
		if (!(response = datavisaSession.checkDatavisaPermition(1)).getRight().equals(HttpStatus.ACCEPTED))
			return Pair.of(response);
		
			//Verifica se o registro existe
			if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
				return Pair.of("Erro: Usuário informado não encontrado. Verifique os valores fornecidos.", HttpStatus.NOT_FOUND);
			}
			
			//verifica se os dados são consistentes
			try {
				user = findByAllFields(user).get();
			
				userRepository.delete(user);
				
				//Verifica se o registro foi excluido
	            if (userRepository.findById(user.getEmail()).isPresent()) {
	            	return Pair.of("Erro: Erro interno.", HttpStatus.INTERNAL_SERVER_ERROR);
	            }
            
            		
	            return Pair.of("Usuário excluído com sucesso!", HttpStatus.OK);
			} catch (Exception e) {
	            return Pair.of("Erro: Usuário inválido. Verifique os valores fornecidos e tente novamente.", HttpStatus.NOT_FOUND);
			}
	}

	public Pair<DatavisaUserDTO, HttpStatus> findById(String email){
		
		DatavisaUserDTO userResponseDTO = new DatavisaUserDTO();
		Pair<String, HttpStatus> response;
		
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
			userResponseDTO.setMensagemRetorno(response.getLeft());
	        return Pair.of(userResponseDTO,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(1)).getRight().equals(HttpStatus.ACCEPTED)) {
			userResponseDTO.setMensagemRetorno(response.getLeft());
	        return Pair.of(userResponseDTO, response.getRight());
	    }
	    
	    try {
	    	UserModel user = userRepository.findById(email).get();
	    	userResponseDTO = new DatavisaUserDTO(user);
	    	
	    	
	    	if (!(response = datavisaSession.checkEmpresaPermition(user.getEmpresaId())).getRight().equals(HttpStatus.ACCEPTED)) {
	    		userResponseDTO = new DatavisaUserDTO(response.getLeft());
	    		return  Pair.of(userResponseDTO, response.getRight());
	    	}
	    	
	    	String nomeEmpresa;
	    	//Permissao Tabela == 100 Usuario pendente
	    	if (user.getPermissaoTabela() == 100) {
	    		PendingUserModel pendingUser = pendingUserRepository.findById(email).get();
	    		nomeEmpresa = tableService.getNomeEmpresa(pendingUser.getEmpresaId());
	    		userResponseDTO.setEmpresaId(pendingUser.getEmpresaId());
	    	} else {
		    	nomeEmpresa = tableService.getNomeEmpresa(user.getEmpresaId());
		    	userResponseDTO.setDepartamento(tableService.getDepartamento(user.getPermissaoTabela(), nomeEmpresa));
	    	}
	    	
	    	userResponseDTO.setEmpresaNome(nomeEmpresa);
    		userResponseDTO.setDepartamentos(tableService.getDatavisaCollumnFields(nomeEmpresa + "_permissoes", "nome"));
	    	
	    	
	    } catch (Exception e) {
	    	userResponseDTO = new DatavisaUserDTO("Usuário não encontrado");
		    return Pair.of(userResponseDTO,HttpStatus.NOT_FOUND);
	    }
	    return Pair.of(userResponseDTO,HttpStatus.OK);
	}

	
	public Pair<Object, HttpStatus> findAll(){
		
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(1)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response, response.getRight());
	    }

	    List<UserModel> userList = datavisaSession.getEmpresaId().equals(1L) ?
	        userRepository.findAll():
	        userRepository.findAllByEmpresaId(datavisaSession.getEmpresaId());
	    
	    try {
	        List<DatavisaUserDTO> dtoList = userList.stream().map(userModel -> {
	            DatavisaUserDTO dto = new DatavisaUserDTO(userModel);
	            try {
	                String nomeEmpresa = tableService.getNomeEmpresa(userModel.getEmpresaId());
	                dto.setEmpresaNome(nomeEmpresa);
	                dto.setDepartamento(tableService.getDepartamento(userModel.getPermissaoTabela(), nomeEmpresa));
	            } catch (Exception e) {
	                // Tratar exceção de forma apropriada, como logar o erro e definir valores padrão
	                dto.setEmpresaNome("Erro ao obter nome da empresa");
	                dto.setDepartamento("Erro ao obter departamento");
	            }
	            return dto;
	        }).collect(Collectors.toList());

	        return Pair.of(dtoList, HttpStatus.OK);
	    } catch (Exception e) {
	        return Pair.of("Erro ao processar a lista de usuários", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	}
	
	public Pair<Object, HttpStatus> getAllPending(){
		
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response,  response.getRight());
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(1)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return Pair.of(response, response.getRight());
	    }

	    List<PendingUserModel> userList = datavisaSession.getEmpresaId().equals(1L) ?
	        pendingUserRepository.findAll():
        	pendingUserRepository.findAllByEmpresaId(datavisaSession.getEmpresaId());
	    
	    try {
	        List<DatavisaUserDTO> dtoList = userList.stream().map(userModel -> {
	            DatavisaUserDTO dto = new DatavisaUserDTO(userModel);
	            try {
	                String nomeEmpresa = tableService.getNomeEmpresa(userModel.getEmpresaId());
	                dto.setEmpresaNome(nomeEmpresa);
	            } catch (Exception e) {
	                // Tratar exceção de forma apropriada, como logar o erro e definir valores padrão
	                dto.setEmpresaNome("Erro ao obter nome da empresa");
	            }
	            return dto;
	        }).collect(Collectors.toList());

	        return Pair.of(dtoList, HttpStatus.OK);
	    } catch (Exception e) {
	        return Pair.of("Erro ao processar a lista de usuários pendentes", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	public Pair<String, HttpStatus> aprovePendingUser(DatavisaUserDTO pendingUserDto){
		
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return response;
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(1)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return response;
	    }
	    
	    try {
	    	if(pendingUserRepository.findById(pendingUserDto.getEmail()).isEmpty()) {
				throw new IllegalArgumentException("Usuário não consta na lista de pendentes.");
			}
	    	
	    	UserModel user = userRepository.findById(pendingUserDto.getEmail()).get();
	    	String nomeEmpresa = tableService.getNomeEmpresa(user.getEmpresaId());
	    	int permissoes = Integer.valueOf(tableService.getDatavisaTableCollumnCount(nomeEmpresa + "_permissoes").getLeft());
	    	
	    	//verifica se os valores são válidos
	    	if(pendingUserDto.getNivelAcesso() < 0  || pendingUserDto.getNivelAcesso() > 3  || pendingUserDto.getPermissaoTabela() < 0  || pendingUserDto.getPermissaoTabela() > permissoes) {
	    		throw new IllegalArgumentException("Os valores de acesso informados não são válidos");
	    	}
	    	
	    	//verifica se o usuário logado é um ADM Datavisa para dar permissão total
	    	if (pendingUserDto.getNivelAcesso() == 0 && !(response = datavisaSession.checkDatavisaPermition(0)).getRight().equals(HttpStatus.ACCEPTED)) {
	    		return response;
	    	}
	    	
	    	user.setEmpresaId(pendingUserRepository.findById(pendingUserDto.getEmail()).get().getEmpresaId());
	    	user.setNivelAcesso(pendingUserDto.getNivelAcesso());
	    	user.setPermissaoTabela(pendingUserDto.getPermissaoTabela());
	    	
	    	userRepository.save(user);
	    	
	    	pendingUserRepository.delete(pendingUserRepository.findById(pendingUserDto.getEmail()).get());
	    	//Verifica se o registro foi excluido
            if (pendingUserRepository.findById(pendingUserDto.getEmail()).isPresent()) {
            	return Pair.of("Erro: Erro interno.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
	    	
	    } catch (Exception e) {
		    return Pair.of("Erro: " + e.getMessage(), HttpStatus.NOT_FOUND);
	    }
	    response =  Pair.of("Usuário  " + pendingUserDto.getEmail() + " aprovado com sucesso!", HttpStatus.OK);
	    return response;
	}
	
	public Pair<String, HttpStatus> refusePendingUser(DatavisaUserDTO pendingUserDto){
		
		Pair<String, HttpStatus> response;
		if (!(response = datavisaSession.checkStatus()).getRight().equals(HttpStatus.ACCEPTED)) {
	        return response;
	    }
	    if (!(response = datavisaSession.checkDatavisaPermition(1)).getRight().equals(HttpStatus.ACCEPTED)) {
	        return response;
	    }
	    
	    try {
	    	Optional<PendingUserModel> pendingUser = pendingUserRepository.findById(pendingUserDto.getEmail());
	    	if(pendingUser.isEmpty()) {
				throw new IllegalArgumentException("Usuário não consta na lista de pendentes.");
			}
	    	
	    	pendingUserRepository.delete(pendingUser.get());
	    	//Verifica se o registro foi excluido
            if (pendingUserRepository.findById(pendingUserDto.getEmail()).isPresent()) {
            	return Pair.of("Erro: Erro interno.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
	    	
	    	UserModel user = userRepository.findById(pendingUserDto.getEmail()).get();
	    	userRepository.delete(user);
			
			//Verifica se o registro foi excluido
            if (userRepository.findById(user.getEmail()).isPresent()) {
            	return Pair.of("Erro: Erro interno.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
	    	
	    } catch (Exception e) {
		    return Pair.of("Erro: " + e.getMessage(), HttpStatus.NOT_FOUND);
	    }
		response =  Pair.of("Usuário  " + pendingUserDto.getEmail() + " teve seu acesso recusado!", HttpStatus.OK);
		return response;
	}

	public Optional<UserModel> findByAllFields (UserModel user){
		return userRepository.findByAllFields(user.getEmail(), user.getNome(), user.getEmpresaId(), user.getPermissaoTabela(), user.getNivelAcesso());
	}
	
}

