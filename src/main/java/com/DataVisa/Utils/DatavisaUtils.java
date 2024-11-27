package com.DataVisa.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.DataVisa.DTO.DatavisaUserDTO;
import com.DataVisa.Models.UserModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tech.tablesaw.api.Table;

public class DatavisaUtils {

    public static List<DatavisaUserDTO> convertToDTOList(List<UserModel> userModels) {
        return userModels.stream()
                .map(DatavisaUserDTO::new) 
                .collect(Collectors.toList());
    }
    
    public static String createJsonNumberList(String input, String split) throws JsonProcessingException {
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
        ObjectNode jsonObject = mapper.createObjectNode();

        // Divide a string nos elementos usando '\r\n' como delimitador
        String[] elements = input.split(split);

        // Itera sobre os elementos e adiciona ao objeto JSON
        for (int i = 0; i < elements.length; i++) {
            jsonObject.put(String.valueOf(i), elements[i]);
        }
        // Converte o objeto JSON para uma string JSON formatada
        return mapper.writeValueAsString(jsonObject);
    }
    
    
    public static String tableNameMapper(String query) {
        // Expressão regular para capturar o nome da tabela após "FROM"
        String regex = "(?i)FROM\\s+([\\w\\d_]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);
        
        // Verifica se há correspondência e retorna a tabela após FROM
        if (matcher.find()) {
            return matcher.group(1); // Retorna o nome da tabela após FROM
        }
        
        return null; // Retorna null se nenhuma tabela for encontrada
    }
    
    public static List<String> tableFieldsMapper(String query) {
        List<String> aliases = new ArrayList<>();

        // Remove quebras de linha e múltiplos espaços
        query = query.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();

        // Extrai a parte da consulta antes do FROM
        String selectPart = query.split("(?i)FROM")[0].trim();

        // Regex para capturar apenas aliases explicitamente declarados com AS
        String aliasRegex = "(?i)\\bAS\\s+(\\w+)\\b";

        // Matcher para capturar os aliases
        Matcher aliasMatcher = Pattern.compile(aliasRegex).matcher(selectPart);

        while (aliasMatcher.find()) {
            String alias = aliasMatcher.group(1); // Captura o alias
            if (alias != null) {
                alias = alias.replace("_", " "); // Substitui _ por espaço
                alias = capitalizeFirstLetter(alias); // Capitaliza a primeira letra
                aliases.add(alias); // Adiciona à lista de aliases
            }
        }

        return aliases;
    }


    public static String columnExtractorByType(Table table, String columnType, String columnName) {
        String response;
        StringBuilder formattedValues = new StringBuilder();
        switch (columnType.toLowerCase()) {
            case "int":
            case "integer":
            	table.intColumn(columnName).forEach(value -> formattedValues.append(value).append(", "));
                break;
            case "varchar":
            case "json":
            case "text":
            case "string":
            	table.stringColumn(columnName).forEach(value -> {
                    if (value != null && !value.trim().isEmpty()) {
                        // Aqui, para separar corretamente, usamos o split por vírgula mas mantendo palavras compostas
                        String[] values = value.split("\\s*,\\s*"); // Divida por vírgula e espaços
                        for (String v : values) {
                            // Remover espaços extras e adicionar ao StringBuilder
                            formattedValues.append(v.trim()).append(", ");
                        }
                    }
                });
                break;
            case "date":
            	response = table.dateColumn(columnName).print();
                break;
            case "datetime":
            	response = table.dateTimeColumn(columnName).print();
                break;
            case "float":
            	table.floatColumn(columnName).forEach(value -> 
                formattedValues.append(String.format(Locale.US, "%.2f", value)).append(", "));
            	break;
            case "double":
            case "decimal":
            	table.doubleColumn(columnName).forEach(value -> 
                formattedValues.append(String.format(Locale.US, "%.2f", value)).append(", "));
            	break;
            case "boolean":
            case "bool":
            	response = table.booleanColumn(columnName).print();
                break;
            default:
            	response = table.stringColumn(columnName).print();
                return response.contains("\n") ? response.substring(response.indexOf('\n') + 1).replaceAll("\\r\\n", "").trim() : response.trim();
        }

        if (formattedValues.length() > 0) {
            formattedValues.setLength(formattedValues.length() - 2);
        }
        return formattedValues.toString();
    }
        
    public static String sanitizeQuery(String query) {
        if (query == null || query.isEmpty()) {
        	throw new IllegalArgumentException("A query não pode ser nula ou vazia.");
        }

        // Remove conteúdo após ';' juntamente com o caractere indesejado
        int semicolonIndex = query.indexOf(';');
        if (semicolonIndex != -1) {
            query = query.substring(0, semicolonIndex);
        }

        // Adiciona "select " no início, caso não esteja presente
        if (!query.trim().toLowerCase().startsWith("select ")) {
            query = "select " + query.trim();
        }
        
        // Considera a query inválida por conter palavras reservadas
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("update ") || lowerQuery.contains("delete ") || 
            lowerQuery.contains("insert ") || lowerQuery.contains("drop ") || 
            lowerQuery.contains("alter ")) {
            throw new IllegalArgumentException("Query inválida: somente comandos com a cláusula SELECT são permitidos.");
        }
        
        return query.replaceAll("\\r\\n", " ").trim();
    }

    private static String capitalizeFirstLetter(String input) { 
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Capitaliza apenas a primeira letra e mantém o restante da string como está
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }
    
    public static String limitQueryToOne(String query) {
        if (query == null || query.isEmpty()) {
        	throw new IllegalArgumentException("A query não pode ser nula ou vazia.");
        }
        
        // Regex para encontrar a cláusula LIMIT na query
        String limitRegex = "(?i)\\s+limit\\s+\\d+";
        if (query.matches(".*" + limitRegex + ".*")) {
            // Substitui o valor atual de LIMIT por 1
            return query.replaceAll(limitRegex, " LIMIT 1");
        } else {
            // Adiciona "LIMIT 1" ao final, caso não exista
            return query + " LIMIT 1";
        }
    }
}
