package com.DataVisa.Repositories;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.DataVisa.Models.TemplateModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class TemplateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // Método para adicionar um novo registro
    @Transactional
    public void save(TemplateModel template) throws SQLException{
    	String tableName = "templates_" + template.getEmpresaId();
        String query = "INSERT INTO " + tableName + " (templateName, sqlQuery, tableName, tablePermition, items, lastModification, empresaId, conexaoId, conexaoName, isActive) " +
                       "VALUES (:templateName, :sqlQuery, :tableName, :tablePermition, :items,  :lastModification, :empresaId, :conexaoId, :conexaoName, isActive = :isActive)";
        try {
            Query insertQuery = entityManager.createNativeQuery(query);
            insertQuery.setParameter("templateName", template.getTemplateName().trim());
            insertQuery.setParameter("sqlQuery", template.getSqlQuery());
            insertQuery.setParameter("tableName", template.getTableName());
            insertQuery.setParameter("tablePermition", template.getTablePermition());
            
            Gson gson = new Gson();
            String itemsJson = gson.toJson(template.getItems());
            insertQuery.setParameter("items", itemsJson);

            Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()));
            insertQuery.setParameter("lastModification", currentTimestamp);
            
            insertQuery.setParameter("empresaId", template.getEmpresaId());
            insertQuery.setParameter("conexaoId", template.getConexaoId());
            insertQuery.setParameter("conexaoName", template.getConexaoName());
            insertQuery.setParameter("isActive", template.getIsActive());
            
	        insertQuery.executeUpdate();
        } catch (Exception e) {
            // Caso queira lançar uma exceção personalizada
            throw new SQLException("Falha ao salvar o template no banco de dados. \nErro: " + e);
        }
    }
    
    public TemplateModel findById(Long id, Long empresaId) {
    	String tableName = "templates_" + empresaId;
        String query = "SELECT id, templateName, sqlQuery, tableName, tablePermition, items, lastModification, empresaId, conexaoId, conexaoName, isActive FROM " + tableName + " WHERE id = :id LIMIT 1";

        try {
            Object[] result = (Object[]) entityManager.createNativeQuery(query)
                    .setParameter("id", id)
                    .getSingleResult();

            // Cria e retorna um TemplateModel a partir do resultado
            TemplateModel template = new TemplateModel();
            template.setId(((Number) result[0]).longValue());
            template.setTemplateName((String) result[1]);
            template.setSqlQuery((String) result[2]);
            template.setTableName((String) result[3]);
            template.setTablePermition(((Number) result[4]).intValue());
            Gson gson = new Gson();
            String itemsJson = (String) result[5]; 
            List<String> items = gson.fromJson(itemsJson, new TypeToken<List<String>>(){}.getType());
            template.setItems(items);
            template.setLastModification((Timestamp) result[6]);
            template.setEmpresaId(((Number) result[7]).longValue());
            template.setConexaoId(((Number) result[8]).longValue());
            template.setConexaoName((String) result[9]);
            template.setIsActive(((Number) result[10]).intValue());

            return template;
        } catch (NoResultException e) {
            return null; // Retorna null se nenhum registro for encontrado
        }
    }
    
    public TemplateModel findByName(String name, Long empresaId) {
        String tableName = "templates_" + empresaId;
        String query = "SELECT id, templateName, sqlQuery, tableName, tablePermition, items, lastModification, empresaId, conexaoId, conexaoName, isActive " +
                "FROM " + tableName + " WHERE templateName = :templateName LIMIT 1";  

        try {
            Object[] result = (Object[]) entityManager.createNativeQuery(query)
                    .setParameter("templateName", name)
                    .getSingleResult();

            TemplateModel template = new TemplateModel();
            template.setId(((Number) result[0]).longValue());
            template.setTemplateName((String) result[1]);
            template.setSqlQuery((String) result[2]);
            template.setTableName((String) result[3]);
            template.setTablePermition(((Number) result[4]).intValue());

            Gson gson = new Gson();
            String itemsJson = (String) result[5]; 
            List<String> items = gson.fromJson(itemsJson, new TypeToken<List<String>>(){}.getType());
            template.setItems(items);

            template.setLastModification((Timestamp) result[6]);
            template.setEmpresaId(((Number) result[7]).longValue());
            template.setConexaoId(((Number) result[8]).longValue());
            template.setConexaoName((String) result[9]);
            template.setIsActive(((Number) result[10]).intValue());

            return template;
        } catch (NoResultException e) {
            return null; // Retorna null se nenhum registro for encontrado
        }
    }
    
    @Transactional
    public void updateTemplate(TemplateModel template) throws SQLException {
        String tableName = "templates_" + template.getEmpresaId();
        String query = "UPDATE " + tableName + 
                       " SET templateName = :templateName, sqlQuery = :sqlQuery, tableName = :tableName, tablePermition = :tablePermition, items = :items, " +
                       "lastModification = :lastModification, conexaoId = :conexaoId, conexaoName = :conexaoName, isActive = :isActive " +
                       "WHERE id = :id";
        try {
	        Query updateQuery = entityManager.createNativeQuery(query);
	        updateQuery.setParameter("templateName", template.getTemplateName());
	        updateQuery.setParameter("sqlQuery", template.getSqlQuery());
	        updateQuery.setParameter("tableName", template.getTableName());
	        updateQuery.setParameter("tablePermition", template.getTablePermition());
	        Gson gson = new Gson();
	        String itemsJson = gson.toJson(template.getItems());
	        updateQuery.setParameter("items", itemsJson);
	        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()));
	        updateQuery.setParameter("lastModification", currentTimestamp);
	        
	        updateQuery.setParameter("conexaoId", template.getConexaoId());
	        updateQuery.setParameter("conexaoName", template.getConexaoName());
	        updateQuery.setParameter("isActive", template.getIsActive());
	        updateQuery.setParameter("id", template.getId());
	
	        updateQuery.executeUpdate();
        } catch (Exception e) {
            // Caso queira lançar uma exceção personalizada
            throw new SQLException(e);
        }
    }

    // Método para remover um registro pelo ID
    @Transactional
    public void delete(TemplateModel template) {
        String tableName = "templates_" + template.getEmpresaId();
        String query = "DELETE FROM " + tableName + " WHERE id = :id";
        
        Query deleteQuery = entityManager.createNativeQuery(query);
        deleteQuery.setParameter("id", template.getId());

        deleteQuery.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<TemplateModel> getAll(Long empresaId) {
        String tableName = "templates_" + empresaId;
        String query = "SELECT id, templateName, sqlQuery, tableName, tablePermition, items, lastModification, empresaId, conexaoId, conexaoName, isActive FROM " + tableName;
        
        List<Object[]> results;
        try {
            results = entityManager.createNativeQuery(query).getResultList();
        

	        List<TemplateModel> templates = new ArrayList<>();
	        for (Object[] result : results) {
	            TemplateModel template = new TemplateModel();
	            template.setId(((Number) result[0]).longValue());
	            template.setTemplateName((String) result[1]);
	            template.setSqlQuery((String) result[2]);
	            template.setTableName((String) result[3]);
	            template.setTablePermition(((Number) result[4]).intValue());
	
	            Gson gson = new Gson();
	            String itemsJson = (String) result[5];
	            List<String> items = gson.fromJson(itemsJson, new TypeToken<List<String>>(){}.getType());
	            template.setItems(items);
	
	            template.setLastModification((Timestamp) result[6]);
	            template.setEmpresaId(((Number) result[7]).longValue());
	            template.setConexaoId(((Number) result[8]).longValue());
	            template.setConexaoName((String) result[9]);
	            template.setIsActive(((Number) result[10]).intValue());
	
	            templates.add(template);
	        }
	        return templates;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executrar a query: " + query, e);
        }

    }
}