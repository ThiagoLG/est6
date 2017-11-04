package com.estagio.dao;

import java.sql.SQLException;
import java.util.List;

import com.estagio.model.Curso;

public interface ICursoDao {

	public List<Curso> listarCursos()throws SQLException;
	
}
