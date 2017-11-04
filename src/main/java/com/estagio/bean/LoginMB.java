package com.estagio.bean;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.estagio.dao.ILoginDao;
import com.estagio.dao.LoginDao;

public class LoginMB {
	private int ra;
	private String senha;
	private boolean logado = false;
	private boolean administrador = false;

	public String logar() {
		String pagina = "login";
		@SuppressWarnings("unused")
		ILoginDao dao = new LoginDao();

//		if (dao.autenticarLogin(ra, senha)) {
//			pagina = "dadosAluno?faces-redirect=true";
//			logado = true;
//
////			if (dao.verNivel(ra).equals("Administrador")) {
////				administrador = true;
////			}
//
//		} else {
//			logado = false;
//
//			FacesContext fc = FacesContext.getCurrentInstance();
//			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Falha de autentica��o!",
//					"Usu�rio ou senha incorretos!"));
//			// fc.addMessage("formBody:txtSenha", new
//			// FacesMessage(FacesMessage.SEVERITY_WARN,"",""));
//
//		}

		ra = 0000;
		senha = "";

		return pagina;
	}

	public String doLogout() {
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		sessao.invalidate();

		return "Login?faces-redirect=true";

	}

	public int getRa() {
		return ra;
	}

	public void setRa(int ra) {
		this.ra = ra;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isLogado() {
		return logado;
	}

	public void setLogado(boolean logado) {
		this.logado = logado;
	}

	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

}
