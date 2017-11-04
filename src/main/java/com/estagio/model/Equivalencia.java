package com.estagio.model;

import java.util.Date;

public class Equivalencia {

	int id_curso;
	String ra;
	String nome;
	String email;
	String empresa;
	String senha;
	boolean deferido;
	Date dtDeferimento;

	// String de exibi��o
	String dataDef;
	
	

	public String getRa() {
		return ra;
	}

	public void setRa(String ra) {
		this.ra = ra;
	}

	public int getId_curso() {
		return id_curso;
	}

	public void setId_curso(int id_curso) {
		this.id_curso = id_curso;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isDeferido() {
		return deferido;
	}

	public void setDeferido(boolean deferido) {
		this.deferido = deferido;
	}

	public Date getDtDeferimento() {
		return dtDeferimento;
	}

	public void setDtDeferimento(Date dtDeferimento) {
		this.dtDeferimento = dtDeferimento;
	}

	public String getDataDef() {
		return dataDef;
	}

	public void setDataDef(String dataDef) {
		this.dataDef = dataDef;
	}

}
