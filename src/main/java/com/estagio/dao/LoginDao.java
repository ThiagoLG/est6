package com.estagio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDao implements ILoginDao {

	@Override
	public boolean autenticarLogin(String ra, String senha) {
		String sql = "SELECT * FROM infoAlunos WHERE ra like ? and senha like ? and ativo = true";
		boolean autenticado = false;
//		boolean aluno = true;
		try {
			Connection con = DBResourceManager.getInstance().getCon();
			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, ra);
			ps.setString(2, senha);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				autenticado = true;
			}

			return autenticado;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return autenticado;
	}

	@Override
	public boolean autenticarLoginEq(String ra, String senha) {
		String sql = "SELECT * FROM infoAlunosEq WHERE ra like ? and senha like ? and ativo = true";
		boolean autenticado = false;
//		boolean aluno = true;
		try {
			Connection con = DBResourceManager.getInstance().getCon();
			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, ra);
			ps.setString(2, senha);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				autenticado = true;
			}

			return autenticado;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return autenticado;
	}

}
