package com.DataVisa.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.DataVisa.Models.UserModel;

import lombok.Data;
import tech.tablesaw.api.Table;

@Component
@SessionScope
@Data
public class DatavisaSession {
	public DatavisaSession() {
		setStatus(false);
		setConexaoAtiva(false);
	}

	private boolean status;
	
	private String email;
	
	private String password;
	
	private String nome;
	
	private Long empresaId;
	
	private String empresaNome;

	private boolean conexaoAtiva;
	
	private Long conexao;
	
	private String nomeConexao;
	
	private String dbUrl;
	
	private String dbUser;
	
	private String dbName;

	private String dbPassword;
	
	private int permissaoTabela;
	
	private String departamento;
	
	private String cargosEmpresa;

	private int nivelAcesso;

	private String templates;
	
	public void setTemplates(String templates) {
		if (templates == null || templates.isEmpty())
			this.templates = "{}";
		else
			this.templates = templates;
	}
	
	public void startSession (UserModel user) throws Exception {
		setStatus(true);
		setEmail(user.getEmail());
		setNome(user.getNome());
		setEmpresaId(user.getEmpresaId());
		setPermissaoTabela(user.getPermissaoTabela());
		setNivelAcesso(user.getNivelAcesso());
		setTemplates(user.getTemplates());
		
	}
	
	public Pair<String, HttpStatus> checkStatus() {
		if (!isStatus())
			return Pair.of("Erro: Login não efetuado!", HttpStatus.UNAUTHORIZED);
		return Pair.of("",HttpStatus.ACCEPTED);
	}
	
	//Access Levels
	//0 = Datavisa Admin
	//1 = Business Admin
	//2 = Data Analyst
	//3 = User
	public Pair<String, HttpStatus> checkDatavisaPermition(int accessLevel) {
		if(getNivelAcesso() > accessLevel)
			return Pair.of("Erro: O usuário não possui permissão para realizar a esta ação.", HttpStatus.FORBIDDEN);
		return Pair.of("", HttpStatus.ACCEPTED);
	}
	
	public Pair<String, HttpStatus> checkEmpresaPermition(Long empresaid) {
		if(empresaid != getEmpresaId() && !empresaid.equals(2L) && !getEmpresaId().equals(1L))
			return  Pair.of("Erro: O usuário não possui permissão para realizar a esta ação.", HttpStatus.FORBIDDEN);
	return Pair.of("", HttpStatus.ACCEPTED);
	}
	
	public Pair<String, HttpStatus> checkConnection() {
		if(!isConexaoAtiva())
			return Pair.of("Erro: Nenhuma conexão ativa! \nConecte a um banco e tente novamente.", HttpStatus.BAD_REQUEST);
		return Pair.of("", HttpStatus.ACCEPTED);
	}

	public Table getCustomerConnection(String query, String tableName) throws Exception {
		Connection clientConnection = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
		PreparedStatement stmt = clientConnection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		Table table = Table.read().db(rs, tableName);
		clientConnection.close();
		return table;
	}
	
}
