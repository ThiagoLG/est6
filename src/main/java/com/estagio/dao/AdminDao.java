package com.estagio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao implements IAdminDao {

	@Override
	public boolean autenticarLogin(String login, String senha) throws SQLException {
		String sql = "SELECT * FROM administrador WHERE login like ? and senha like ?";
		boolean autenticado = false;
		try {
			Connection con = DBResourceManager.getInstance().getCon();
			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, login);
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
