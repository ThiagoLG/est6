package com.estagio.dao;

public interface ILoginDao {
	
	public boolean autenticarLogin(String ra, String senha);
	
	public boolean autenticarLoginEq(String ra, String senha);
	
}
