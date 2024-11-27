package com.DataVisa.Repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.DataVisa.Models.TableModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TableRepository {
	
	@PersistenceContext
    private EntityManager entityManager;

	public List<TableModel> findAll(String tableName) {
        List<TableModel> results = new ArrayList<>();
        String query = "SELECT id, nome, conexaoId, permissaoAcesso FROM tabelas_" + tableName;
        List<?> rows = entityManager.createNativeQuery(query).getResultList();
        for (Object row : rows) {
            Object[] rowArray = (Object[]) row;
            String nome = ((String) rowArray[1]).replaceAll("[\\r\\n]", "").trim();
            TableModel tableModel = new TableModel(
                ((Number) rowArray[0]).longValue(), // id
                nome,               // nome
                ((Number) rowArray[2]).longValue(), // conexaoId
                ((Number) rowArray[3]).intValue()   // permissaoAcesso
            );
            results.add(tableModel);
        }
        return results;
    }
	
	public void updateAll(List<TableModel> tableModels, String tableName) {
        String query = "UPDATE " + tableName + " SET nome = ?, conexaoId = ?, permissaoAcesso = ? WHERE id = ?";
        for (TableModel tableModel : tableModels) {
            entityManager.createNativeQuery(query)
                .setParameter(1, tableModel.getNome())
                .setParameter(2, tableModel.getConexaoId())
                .setParameter(3, tableModel.getPermissaoAcesso())
                .setParameter(4, tableModel.getId())
                .executeUpdate();
        }
    }
	
	@Transactional
    public void delete(String tableName) {
        // Valida o nome da tabela para evitar possíveis erros ou injeções de SQL
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da tabela não pode ser nulo ou vazio.");
        }

        String sql = "DROP TABLE IF EXISTS " + tableName;

        entityManager.createNativeQuery(sql).executeUpdate();
    }
}