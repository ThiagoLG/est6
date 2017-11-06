package com.estagio.model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.Application;
//import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.estagio.bean.AlunoMB;

//import bean.LoginMB;

public class Listener implements PhaseListener {

	private static final long serialVersionUID = 8137086901172772454L;
	public String nomePagina;

	@Override
	public void afterPhase(PhaseEvent e) {

		List<String> lstPaginas = new ArrayList<String>();
		lstPaginas.add("/index.xhtml");
		lstPaginas.add("/login.xhtml");
		lstPaginas.add("/loginAdm.xhtml");

		List<String> lstPaginasAdmin = new ArrayList<String>();
		lstPaginasAdmin.add("/areaAdmin.xhtml");
		lstPaginasAdmin.add("/consultarAluno.xhtml");
		lstPaginasAdmin.add("/adicionarAluno.xhtml");
		lstPaginasAdmin.add("/editarAluno.xhtml");
		lstPaginasAdmin.add("/excluirAluno.xhtml");
		lstPaginasAdmin.add("/visualizarAluno.xhtml");
		lstPaginasAdmin.add("/visualizarEquivalencia.xhtml");

		FacesContext fc = FacesContext.getCurrentInstance();
		nomePagina = fc.getViewRoot().getViewId();

		if (!lstPaginas.contains(nomePagina)) {
			Application app = fc.getApplication();

			AlunoMB aluno = app.evaluateExpressionGet(fc, "#{alunoMB}", AlunoMB.class);

			// LoginMB login = app.evaluateExpressionGet(fc, "#{loginMB}",
			// LoginMB.class);
			if (aluno.isLogado()) {

				if (!aluno.isAdmin()) {
					if (lstPaginasAdmin.contains(nomePagina)) {
						NavigationHandler nav = app.getNavigationHandler();
						nav.handleNavigation(fc, "", "loginAdm?faces-redirect=true");
						fc.renderResponse();
					}
				}

			} else {

				if (lstPaginasAdmin.contains(nomePagina)) {
					NavigationHandler nav = app.getNavigationHandler();
					nav.handleNavigation(fc, "", "loginAdm?faces-redirect=true");
					fc.renderResponse();
				} else {
					NavigationHandler nav = app.getNavigationHandler();
					nav.handleNavigation(fc, "", "login?faces-redirect=true");
					fc.renderResponse();
				}
			}

		}
	}

	@Override
	public void beforePhase(PhaseEvent e) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
