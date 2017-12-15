package com.estagio.dao;

import java.sql.SQLException;
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
	
	public boolean excluir(int id_al) throws SQLException;
	
	public boolean excluirEq(int id_eq) throws SQLException;

	public List<Aluno> pesquisarRa (String ra) throws SQLException;
	
	public List<Equivalencia> pesquisarRaEq (String ra) throws SQLException;
	
	public boolean editarEq(Equivalencia eq) throws SQLException;
	
	public boolean estagioAtivo(String ra) throws SQLException;
	
	public boolean restaurarExcluido (int id_al) throws SQLException;
	
	public boolean restaurarExcluidoEq (int id_eq) throws SQLException;
	
	public List<Aluno> pesquisarNomeExc(String nome) throws SQLException;
	
	public List<Equivalencia> pesquisarNomeEqExc (String nome) throws SQLException;
	
}
