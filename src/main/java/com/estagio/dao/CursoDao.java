package com.estagio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.estagio.model.Curso;

public class CursoDao implements ICursoDao {

	@Override
	public List<Curso> listarCursos() throws SQLException {
		List<Curso> lstCursos = new ArrayList<Curso>();
		Connection con;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "Select * from curso";

		try {
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				Curso c = new Curso();
				c.setId_curso(rs.getInt("id_curso"));
				c.setTurno(rs.getString("turno"));
				c.setDescricao(rs.getString("descricao")+" - "+c.getTurno());

				lstCursos.add(c);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			rs.close();
		}

		return lstCursos;
	}

}
