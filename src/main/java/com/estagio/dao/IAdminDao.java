package com.estagio.dao;

import java.sql.SQLException;

public interface IAdminDao {

	public boolean autenticarLogin(String ra, String senha) throws SQLException;
	
}
