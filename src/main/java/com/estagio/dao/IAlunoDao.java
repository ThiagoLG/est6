package com.estagio.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.estagio.model.Aluno;
import com.estagio.model.Equivalencia;

public interface IAlunoDao {

	public boolean adicionar(Aluno a) throws SQLException;

	public boolean adicionarEq(Equivalencia e) throws SQLException;
	
	public List<Aluno> pesquisarNome(String nome) throws SQLException;
	
	public List<Equivalencia> pesquisarNomeEq (String nome) throws SQLException;
	
	public Aluno pesquisar(String id, String senha) throws SQLException;
	
	public Equivalencia pesquisarEq(String id, String senha) throws SQLException;
	
	public boolean editar(Aluno a) throws SQLException;
	
	public boolean excluir(String ra, Date inicio) throws SQLException;
	
	public boolean excluirEq(String ra, Date def) throws SQLException;

	public List<Aluno> pesquisarRa (String ra) throws SQLException;
	
	public List<Equivalencia> pesquisarRaEq (String ra) throws SQLException;
	
	public boolean editarEq(Equivalencia eq) throws SQLException;
	
	public boolean estagioAtivo(String ra) throws SQLException;
	
}
