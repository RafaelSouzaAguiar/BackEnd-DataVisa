package com.DataVisa.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.DataVisa.Models.BusinessModel;
import com.DataVisa.Repositories.BusinessRepository;
import com.DataVisa.Session.DatavisaSession;

@Service
public class BusinessService{
	
	@Autowired
	BusinessRepository businessRepository;
	
	@Autowired
	DatavisaSession datavisaSession;	
	
	@Autowired
	DBService dBService;

	public Optional<String> save(BusinessModel document) {
		try {
			//Verifica se o documento já existe
//			if (businessRepository.findById(document.getId()).isPresent()) {
//				throw new IllegalArgumentException("Documento já cadastrado.");
//			}
//			
			businessRepository.save(document);
//			
		} catch (Exception ex){
			 return Optional.of("Ocorreu um erro, Empresa não cadastrado! " + ex.getMessage()); 
		}
		return Optional.of("Empresa cadastrado com sucesso!");
	}

	public String delete(BusinessModel document){
		try {
			
			//Verifica se o documento existe
//			if (businessRepository.findById(document.getId()).isEmpty()) {
//                throw new RuntimeException("Usuário não encontrado.");
//            }
//			
//			businessRepository.delete(document);
			
			//Verifica se o documento foi excluido
//            if (businessRepository.findById(document.getId()).isPresent()) {
//                throw new RuntimeException("Falha ao excluir o documento.");
//            }
            
		} catch (Exception ex){
			return "Ocorreu um erro! " + ex.getMessage();			
		}
		return "Documento excluído com sucesso!";
	}

	public List<BusinessModel> findAll(){
		return businessRepository.findAll();
	}
	
}