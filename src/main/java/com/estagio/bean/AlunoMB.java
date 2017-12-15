package com.estagio.bean;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.estagio.dao.AdminDao;
import com.estagio.dao.AlunoDao;
import com.estagio.dao.CursoDao;
import com.estagio.dao.IAdminDao;
import com.estagio.dao.IAlunoDao;
import com.estagio.dao.ICursoDao;
import com.estagio.dao.ILoginDao;
import com.estagio.dao.LoginDao;
import com.estagio.model.Administrador;
import com.estagio.model.Aluno;
import com.estagio.model.Curso;
import com.estagio.model.Equivalencia;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;

@ManagedBean
@SessionScoped
public class AlunoMB {

	private String image;
	private boolean logado = false;
	private boolean admin = false;
	private Aluno aluno;
	private Equivalencia equivalencia;
	private Administrador administrador;
	private Curso curso;
	private List<Aluno> lstAlunos = new ArrayList<Aluno>();
	private List<Curso> lstCursos = new ArrayList<Curso>();
	private List<Curso> lstCursosEdit = new ArrayList<Curso>();
	private List<Equivalencia> lstEquivalencia = new ArrayList<Equivalencia>();
	private List<Aluno> lstPendentes = new ArrayList<Aluno>();
	private List<Aluno> lstConcluidos = new ArrayList<Aluno>();
	private IAlunoDao alunoDao = new AlunoDao();
	private String pesqNome;
	private String pesqRA;
	private String opcPesquisa;
	private int totalHoras;
	private String mes;
	private String ano;
	private String nomeArquivo;
	private String semestre;
	private int aux;
	private String tituloRel;

	// Campos de imagem
	private String termoCompromisso;
	private String planoAtividade;
	private String relatorio1;
	private String relatorio2;
	private String relatorio3;
	private String relatorio4;
	private String termoAditivo;
	private String avAluno;
	private String avEmpresa;
	private String termoRecisao;
	private String termoRealizacao;
	private String termoContrato;
	private String deferido;
	private String estagioConcluido;

	@SuppressWarnings("deprecation")
	@PostConstruct
	public void inicializar() {
		// iniciando classes
		aluno = new Aluno();
		equivalencia = new Equivalencia();
		administrador = new Administrador();
		curso = new Curso();
		aux = 1;

		// carregando campos de consulta de relatorios pendentes
		Date d = new Date();
		mes = Integer.toString(d.getMonth() + 1);
		ano = Integer.toString(d.getYear() + 1900);

		if (!mes.equals("10") && !mes.equals("11") && !mes.equals("12")) {
			mes = "0" + mes;
		}

		if (Integer.parseInt(mes) > 7) {
			semestre = "2";
		} else {
			semestre = "1";
		}

		image = "";
		try {
			carregarCursos();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("nao carregou os cursos");
			e.printStackTrace();
		}

		// image = "img/correct.png";
		// image2 = "img/incorrect.png";
	}

	// public void refresh() {
	// FacesContext context = FacesContext.getCurrentInstance();
	// Application application = context.getApplication();
	// ViewHandler viewHandler = application.getViewHandler();
	// UIViewRoot viewRoot = viewHandler.createView(context,
	// context.getViewRoot().getViewId());
	// context.setViewRoot(viewRoot);
	// context.renderResponse();
	// }

	public String logar() throws SQLException {
		String pagina = "";
		ILoginDao loginDao = new LoginDao();
		IAdminDao adminDao = new AdminDao();
		aluno.setSenha("123");

		if (adminDao.autenticarLogin(administrador.getUsuario(), administrador.getSenha())) {
			System.out.println("logou admin");
			logado = true;
			admin = true;
			pagina = "areaAdmin?faces-redirect=true";

		} else if (loginDao.autenticarLogin(aluno.getRa(), aluno.getSenha())) {
			System.out.println("logou aluno");
			logado = true;

			aluno = alunoDao.pesquisar(aluno.getRa(), aluno.getSenha());

			if (!aluno.getRa().equals("")) {
				carregarImagens();

				try {
					contarHoras(aluno);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				datasLimite();
				pagina = "dadosAluno?faces-redirect=true";

			} else {
				logado = false;
				pagina = "index?faces-redirect=true";
			}

		} else if (loginDao.autenticarLoginEq(aluno.getRa(), aluno.getSenha())) {
			System.out.println("logou aluno eq");
			logado = true;

			equivalencia = alunoDao.pesquisarEq(aluno.getRa(), aluno.getSenha());

			if (!equivalencia.getRa().equals("")) {
				carregarImagensEq();
				pagina = "dadosEquivalencia?faces-redirect=true";
				aluno.setRa("");
				aluno.setSenha("");

			} else {
				logado = false;
				pagina = "index?faces-redirect=true";
			}

		} else {
			System.out.println("não logou ninguem e a página é: " + pagina + "!");
			logado = false;
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Não foi possível realizar consulta!", "RA inválido ou não cadastrado!"));
			// fc.addMessage("formBody:txtSenha", new
			fc.addMessage("formAdm", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Usuário ou senha incorretos!", ""));

			// FacesMessage(FacesMessage.SEVERITY_WARN,"",""));

		}

		return pagina;
	}

	public String deslogar() {
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		sessao.invalidate();

		return "index?faces-redirect=true";
	}

	public String adicionar() throws SQLException {
		String pagina = "";

		// aluno.setId_curso(curso.getId_curso());

		if (validarDados()) {
			if (alunoDao.estagioAtivo(aluno.getRa())) {

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Não foi possível registrar o aluno", "É permitido somente um registro de estágio ativo!"));

			} else {

				if (alunoDao.adicionar(aluno)) {

					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage("formBody",
							new FacesMessage(FacesMessage.SEVERITY_INFO, "Aluno registrado com sucesso!", ""));
					limpar();

				} else {

					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
							"Falha ao registrar o aluno", "Verifique os campos e tente novamente"));

				}
			}
		}

		return pagina;
	}

	public String adicionarEq() throws SQLException {
		String pagina = "";

		// equivalencia.setId_curso(curso.getId_curso());
		if (validarDadosEq()) {
			if (alunoDao.adicionarEq(equivalencia)) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody",
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Aluno registrado com sucesso!", ""));
				limpar();
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Falha ao registrar o aluno",
						"Verifique os campos e tente novamente"));
			}
		}

		return pagina;
	}

	public void limpar() {
		aluno = new Aluno();
		equivalencia = new Equivalencia();
	}

	public String doLogout() {
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		sessao.invalidate();

		return "login?faces-redirect=true";

	}

	public void carregarImagensEq() {
		if (equivalencia.isDeferido()) {
			deferido = "img/correct.png";
		} else {
			deferido = "img/incorrect.png";
		}
	}

	public void carregarImagens() {
		if (aluno.isTermoCompromisso()) {
			termoCompromisso = "img/correct.png";
		} else {
			termoCompromisso = "img/incorrect.png";
		}

		if (aluno.isPlanoAtividade()) {
			planoAtividade = "img/correct.png";
		} else {
			planoAtividade = "img/incorrect.png";
		}

		if (aluno.isRelatorio1()) {
			relatorio1 = "img/correct.png";
		} else {
			relatorio1 = "img/incorrect.png";
		}

		if (aluno.isRelatorio2()) {
			relatorio2 = "img/correct.png";
		} else {
			relatorio2 = "img/incorrect.png";
		}

		if (aluno.isRelatorio3()) {
			relatorio3 = "img/correct.png";
		} else {
			relatorio3 = "img/incorrect.png";
		}

		if (aluno.isRelatorio4()) {
			relatorio4 = "img/correct.png";
		} else {
			relatorio4 = "img/incorrect.png";
		}

		if (aluno.isTermoAditivo()) {
			termoAditivo = "img/correct.png";
		} else {
			termoAditivo = "img/incorrect.png";
		}

		if (aluno.isAvAluno()) {
			avAluno = "img/correct.png";
		} else {
			avAluno = "img/incorrect.png";
		}

		if (aluno.isAvEmpresa()) {
			avEmpresa = "img/correct.png";
		} else {
			avEmpresa = "img/incorrect.png";
		}

		if (aluno.isTermoRecisao()) {
			termoRecisao = "img/correct.png";
		} else {
			termoRecisao = "img/incorrect.png";
		}

		if (aluno.isTermoRealizacao()) {
			termoRealizacao = "img/correct.png";
		} else {
			termoRealizacao = "img/incorrect.png";
		}

		if (aluno.isTermoContrato()) {
			termoContrato = "img/correct.png";
		} else {
			termoContrato = "img/incorrect.png";
		}

		if (aluno.isConcluido()) {
			estagioConcluido = "img/correct.png";
		} else {
			estagioConcluido = "img/incorrect.png";
		}

		System.out.println("carregou tudo!");

	}

	public void pesquisarNome() throws SQLException {
		lstAlunos = alunoDao.pesquisarNome(pesqNome);
		lstEquivalencia = alunoDao.pesquisarNomeEq(pesqNome);

		if (lstEquivalencia.isEmpty() && lstAlunos.isEmpty()) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN, "Nenhum aluno encontrado",
					"Confira o nome e tente novamente"));
		} else {
			aux = 1;
		}

	}

	public void pesquisarNomeExc() throws SQLException {
		lstAlunos = alunoDao.pesquisarNomeExc(pesqNome);
		lstEquivalencia = alunoDao.pesquisarNomeEqExc(pesqNome);

		if (lstEquivalencia.isEmpty() && lstAlunos.isEmpty()) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN, "Nenhum aluno encontrado",
					"Confira o nome e tente novamente"));
		}
	}
	
	public void pesquisarRA() throws SQLException {
		// lstAlunos.clear();
		// lstEquivalencia.clear();
		lstAlunos = alunoDao.pesquisarRa(pesqRA);
		lstEquivalencia = alunoDao.pesquisarRaEq(pesqRA);

		// if (aluno.getRa() == 0) {
		// System.out.println("n�o foram encontrados alunos com o ra");
		// FacesContext fc = FacesContext.getCurrentInstance();
		// fc.addMessage("formBody", new
		// FacesMessage(FacesMessage.SEVERITY_WARN, "Nenhum aluno encontrado",
		// "Confira o RA e tente novamente"));
		// }
		// lstAlunos.add(aluno);
		// lstEquivalencia.add(equivalencia);

		if (lstEquivalencia.isEmpty() && lstAlunos.isEmpty()) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN, "Nenhum aluno encontrado",
					"Confira o RA e tente novamente"));
		} else {
			aux = 1;
		}
	}

	public void carregarCursos() throws SQLException {
		ICursoDao cursoDao = new CursoDao();
		lstCursos = cursoDao.listarCursos();

	}

	public void carregarCursosEdit(int id_curso) throws SQLException {
		ICursoDao cursoDao = new CursoDao();
		lstCursosEdit = cursoDao.listarCursos();

		lstCursosEdit.add(0, lstCursosEdit.get(id_curso - 1));
		lstCursosEdit.remove(id_curso);
	}

	public String editarAluno(Aluno a) {

		aluno = a;

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// a.setDtIni(sdf.format(a.getInicio()));

		try {
			carregarCursosEdit(a.getId_curso());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("nao carregou os cursos para o edit");
		}

		return "editarAluno?faces-redirect=true";
	}

	public String editarEquivalencia(Equivalencia eq) {
		equivalencia = eq;

		try {
			carregarCursosEdit(eq.getId_curso());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("nao carregou os cursos para o edit");
		}

		return "editarEquivalencia?faces-redirect=true";
	}

	public String excluirAluno(Aluno a) {
		aluno = a;
		carregarImagens();
		return "excluirAluno?faces-redirect=true";
	}

	public String excluirEquivalencia(Equivalencia eq) {
		equivalencia = eq;
		carregarImagensEq();
		return "excluirEquivalencia?faces-redirect=true";
	}

	public String restaurarAluno (Aluno a){
		aluno = a;
		carregarImagens();
		return "restaurarAluno?faces-redirect=true";
	}
	
	public String restaurarEquivalencia (Equivalencia eq){
		equivalencia = eq;
		carregarImagensEq();
		return "restaurarEquivalencia?faces-redirect=true";
	}
	
	public String redirectAdmin() {
		lstAlunos.clear();
		aluno = new Aluno();
		equivalencia = new Equivalencia();
		lstEquivalencia.clear();

		return "consultarAluno?faces-redirect=true";
	}

	public String salvarEdit() throws SQLException {
		IAlunoDao alunoDao = new AlunoDao();
		String pagina = "";

		if (validarDados()) {
			if (alunoDao.editar(aluno)) {
				aluno = new Aluno();
				lstAlunos.clear();
				equivalencia = new Equivalencia();
				lstEquivalencia.clear();
				pagina = "consultarAluno?faces-redirect=true";
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Não foi possível atualizar as informações!", "Confira os dados e tente novamente."));
			}
		}
		return pagina;
	}

	public String salvarEditEq() throws SQLException {
		IAlunoDao alunoDao = new AlunoDao();
		String pagina = "";

		if (validarDadosEq()) {
			if (alunoDao.editarEq(equivalencia)) {
				aluno = new Aluno();
				lstAlunos.clear();
				equivalencia = new Equivalencia();
				lstEquivalencia.clear();
				pagina = "consultarAluno?faces-redirect=true";
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Não foi poss�vel atualizar as informações!", "Confira os dados e tente novamente."));
			}
		}
		return pagina;
	}

	public String salvarExc() throws SQLException {
		String pagina = "";
		IAlunoDao alunoDao = new AlunoDao();

		if (alunoDao.excluir(aluno.getId_al())) {
			aluno = new Aluno();
			equivalencia = new Equivalencia();
			lstEquivalencia.clear();
			lstAlunos.clear();
			pagina = "consultarAluno?faces-redirect=true";
		}

		return pagina;
	}

	public String salvarExcEq() throws SQLException {
		String pagina = "";
		IAlunoDao alunoDao = new AlunoDao();

		if (alunoDao.excluirEq(equivalencia.getId_eq())) {
			aluno = new Aluno();
			equivalencia = new Equivalencia();
			lstEquivalencia.clear();
			lstAlunos.clear();
			pagina = "consultarAluno?faces-redirect=true";
		}

		return pagina;
	}

	public String salvarRest() throws SQLException{
		String pagina = "";
		IAlunoDao alunoDao = new AlunoDao();

		if (alunoDao.restaurarExcluido(aluno.getId_al())) {
			aluno = new Aluno();
			equivalencia = new Equivalencia();
			lstEquivalencia.clear();
			lstAlunos.clear();
			pagina = "restaurarAlunos?faces-redirect=true";
		}

		return pagina;
	}

	public String salvarRestEq() throws SQLException{
		String pagina = "";
		IAlunoDao alunoDao = new AlunoDao();

		if (alunoDao.restaurarExcluidoEq(equivalencia.getId_eq())) {
			aluno = new Aluno();
			equivalencia = new Equivalencia();
			lstEquivalencia.clear();
			lstAlunos.clear();
			pagina = "restaurarAlunos?faces-redirect=true";
		}

		return pagina;
	}
	
	public String visualizarAluno(Aluno a) {
		String pagina = "visualizarAluno?faces-redirect=true";
		aluno = a;
		carregarImagens();

		if (a.getDtIni() == null) {
			pagina = "visualizarConcluidoEq?faces-redirect=true";
		} else if (a.getDtShow() != null) {
			pagina = "visualizarPendencia?faces-redirect=true";
			try {
				datasLimite();
				contarHoras(aluno);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (aux == 0 && a.getDtIni() != null) {
			pagina = "visualizarConcluido?faces-redirect=true";
			try {
				datasLimite();
				contarHoras(aluno);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			try {
				datasLimite();
				contarHoras(aluno);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return pagina;
	}

	public String visualizarEquivalencia(Equivalencia eq) {
		equivalencia = eq;
		carregarImagensEq();
		return "visualizarEquivalencia?faces-redirect=true";
	}

	public void contarHoras(Aluno a) throws ParseException {
		float horas = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String ini = sdf.format(a.getInicio());
		String fim = sdf.format(a.getTermino());

		Calendar inicio = Calendar.getInstance();
		Calendar termino = Calendar.getInstance();

		inicio.setTime(sdf.parse(ini));
		termino.setTime(sdf.parse(fim));
		// System.out.println(data1 + " data 1");

		long dt = (termino.getTimeInMillis() - inicio.getTimeInMillis());
		float dias = (dt / 86400000L);

		// int dias = termino.get(Calendar.DAY_OF_YEAR) -
		// inicio.get(Calendar.DAY_OF_YEAR);
		// System.out.println((String.valueOf(dias))+" dias");

		float sem = (dias / 7);
		System.out.println(sem + " semanas");

		horas = (sem * a.getCargaHoraria());
		System.out.println(horas + " horas" + " - " + a.getCargaHoraria() + " carga");

		setTotalHoras((int) horas);
	}

	public void datasLimite() {

		String re1 = aluno.getRel1();
		String re2 = aluno.getRel2();
		String re3 = aluno.getRel3();
		String re4 = aluno.getRel4();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date termino = aluno.getTermino();
			Date r1 = sdf.parse(re1);
			Date r2 = sdf.parse(re2);
			Date r3 = sdf.parse(re3);
			Date r4 = sdf.parse(re4);

			if (r1.after(termino) || r1.equals(termino)) {
				aluno.setRel1(sdf.format(termino));
				aluno.setRel2("");
				aluno.setRel3("");
				aluno.setRel4("");

			} else if (r2.after(termino) || r2.equals(termino)) {
				aluno.setRel2(sdf.format(termino));
				aluno.setRel3("");
				aluno.setRel4("");
			} else if (r3.after(termino) || r3.equals(termino)) {
				aluno.setRel3(sdf.format(termino));
				aluno.setRel4("");
			} else if (r4.after(termino) || r4.equals(termino)) {
				aluno.setRel4(sdf.format(termino));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public boolean validarDados() {
		boolean ok = true;

		FacesContext fc = FacesContext.getCurrentInstance();

		// VALIDAR DATA
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String ini = sdf.format(aluno.getInicio());
		String fim = sdf.format(aluno.getTermino());

		Calendar inicio = Calendar.getInstance();
		Calendar termino = Calendar.getInstance();

		try {
			inicio.setTime(sdf.parse(ini));
			termino.setTime(sdf.parse(fim));
			if (inicio.after(termino)) {
				ok = false;
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
						"Não foi possível registrar o aluno!", "Data de término deve ser posterior a date de início"));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// VALIDAR RA
		try {
			Float.parseFloat(aluno.getRa());
		} catch (Exception e) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Não foi possível registrar o aluno!", "RA deve conter apenas números"));
		}

		// VALIDAR NOME
		// try {
		// Float.parseFloat(aluno.getNome());
		// } catch (Exception e) {
		// ok = false;
		// fc.addMessage("formBody", new
		// FacesMessage(FacesMessage.SEVERITY_FATAL,
		// "N�o foi poss�vel registrar o aluno!", "Nome n�o pode conter
		// n�meros"));
		// }

		// VALIDAR CARGA HORARIA
		try {
			@SuppressWarnings("unused")
			float f = aluno.getCargaHoraria() / 2;
			if (aluno.getCargaHoraria() == 0) {
				ok = false;
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
						"Não foi poss�vel registrar o aluno!", "Carga Horária não pode ser igual a zero"));
			}
		} catch (Exception e) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Não foi possível registrar o aluno!", "Carga Horária deve conter apenas números"));
		}

		// VALIDAR EMAIL
		if (!aluno.getEmail().contains("@")) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Não foi possível registrar o aluno!", "E-mail inválido"));
		}

		return ok;

	}

	public boolean validarDadosEq() {
		boolean ok = true;

		FacesContext fc = FacesContext.getCurrentInstance();

		// VALIDAR DATA
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dtEq = sdf.format(equivalencia.getDtDeferimento());
		String hj = sdf.format(new Date());

		Calendar dataEq = Calendar.getInstance();
		Calendar hoje = Calendar.getInstance();

		try {
			dataEq.setTime(sdf.parse(dtEq));
			hoje.setTime(sdf.parse(hj));
			if (dataEq.after(hoje)) {
				ok = false;
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
						"Não foi possível registrar o aluno!", "Equival�ncia não pode ter uma data futura"));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// VALIDAR RA
		try {
			Float.parseFloat(equivalencia.getRa());
		} catch (Exception e) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Não foi possível registrar o aluno!", "RA deve conter apenas números"));
		}

		// VALIDAR EMAIL
		if (!equivalencia.getEmail().contains("@")) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Não foi possível registrar o aluno!", "E-mail inválido"));
		}

		return ok;

	}

	public void pesquisarPendentes() {
		lstPendentes.clear();
		AlunoDao dao = new AlunoDao();
		String data = mes + "/" + ano;

		nomeArquivo = "relatorios-pendentes-" + mes + "_" + ano;
		
		tituloRel = "Alunos com relatórios pendentes para o mês/ano: "+mes+"/"+ano;

		if (ano.equals("")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_INFO, "Preencha o ano!", ""));

		} else {
			try {
				lstAlunos = dao.pesquisarNome("");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			for (Aluno a : lstAlunos) {

				String re1 = a.getRel1();
				String re2 = a.getRel2();
				String re3 = a.getRel3();
				String re4 = a.getRel4();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				try {
					Date termino = a.getTermino();
					Date r1 = sdf.parse(re1);
					Date r2 = sdf.parse(re2);
					Date r3 = sdf.parse(re3);
					Date r4 = sdf.parse(re4);

					if (r1.after(termino) || r1.equals(termino)) {
						a.setRel1(sdf.format(termino));
						a.setRel2("");
						a.setRel3("");
						a.setRel4("");

					} else if (r2.after(termino) || r2.equals(termino)) {
						a.setRel2(sdf.format(termino));
						a.setRel3("");
						a.setRel4("");
					} else if (r3.after(termino) || r3.equals(termino)) {
						a.setRel3(sdf.format(termino));
						a.setRel4("");
					} else if (r4.after(termino) || r4.equals(termino)) {
						a.setRel4(sdf.format(termino));
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (a.getRel1().contains(data) && a.isRelatorio1() == false) {
					a.setDtShow(a.getRel1());
					lstPendentes.add(a);
				} else if (a.getRel2().contains(data) && a.isRelatorio2() == false) {
					a.setDtShow(a.getRel2());
					lstPendentes.add(a);
				} else if (a.getRel3().contains(data) && a.isRelatorio3() == false) {
					a.setDtShow(a.getRel3());
					lstPendentes.add(a);
				} else if (a.getRel4().contains(data) && a.isRelatorio4() == false) {
					a.setDtShow(a.getRel4());
					lstPendentes.add(a);
				}
			}

			for (Aluno a : lstPendentes) {
				System.out.println(a.getRa() + " - " + a.getNome() + " - " + a.getDtShow());
			}

			if (lstPendentes.isEmpty()) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Nenhum aluno com relatório pendente para " + mes + "/" + ano, ""));

			} else {
				aux = 1;
			}
		}
	}

	public void pesquisarConcluidos() {
		lstConcluidos.clear();
		AlunoDao dao = new AlunoDao();

		nomeArquivo = "estagios-concluidos-" + semestre + "_" + ano;
		
		tituloRel = "Alunos que concluiram estágio no semestre/ano: "+semestre+"/"+ano;

		if (ano.equals("")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_INFO, "Preencha o ano!", ""));

		} else {
			try {
				lstAlunos = dao.pesquisarNome("");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			for (Aluno a : lstAlunos) {

				// String conc = a.getDtConc();
				// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				if (a.getDtConc() != null) {
					if (semestre.equals("1")) {
						if (a.getDtConc().contains("01/" + ano) || a.getDtConc().contains("02/" + ano)
								|| a.getDtConc().contains("03/" + ano) || a.getDtConc().contains("04/" + ano)
								|| a.getDtConc().contains("05/" + ano) || a.getDtConc().contains("06/" + ano)
								|| a.getDtConc().contains("07/" + ano)) {

							lstConcluidos.add(a);

						}
					} else {
						if (a.getDtConc().contains("08/" + ano) || a.getDtConc().contains("08/" + ano)
								|| a.getDtConc().contains("10/" + ano) || a.getDtConc().contains("11/" + ano)
								|| a.getDtConc().contains("12/" + ano)) {

							lstConcluidos.add(a);

						}
					}
				}
			}

			// CARREGAR OS ALUNOS FINALIZADOS POR EQUIVALÊNCIA
			try {
				lstEquivalencia = dao.pesquisarNomeEq("");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			for (Equivalencia eq : lstEquivalencia) {
				if (eq.getDataDef() != null) {
					if (semestre.equals("1")) {
						if (eq.getDataDef().contains("01/" + ano) || eq.getDataDef().contains("02/" + ano)
								|| eq.getDataDef().contains("03/" + ano) || eq.getDataDef().contains("04/" + ano)
								|| eq.getDataDef().contains("05/" + ano) || eq.getDataDef().contains("06/" + ano)
								|| eq.getDataDef().contains("07/" + ano)) {

							Aluno a = new Aluno();
							a.setRa(eq.getRa());
							a.setNome(eq.getNome());
							a.setEmail(eq.getEmail());
							a.setEmpresa(eq.getEmpresa());
							a.setConcluido(eq.isDeferido());
							a.setDtConc(eq.getDataDef());
							lstConcluidos.add(a);

						}
					} else {
						if (eq.getDataDef().contains("08/" + ano) || eq.getDataDef().contains("09/" + ano)
								|| eq.getDataDef().contains("10/" + ano) || eq.getDataDef().contains("11/" + ano)
								|| eq.getDataDef().contains("12/" + ano)) {

							Aluno a = new Aluno();
							a.setRa(eq.getRa());
							a.setNome(eq.getNome());
							a.setEmail(eq.getEmail());
							a.setEmpresa(eq.getEmpresa());
							a.setConcluido(eq.isDeferido());
							a.setDtConc(eq.getDataDef());
							lstConcluidos.add(a);

						}
					}
				}
			}

			// MOSTRAR LISTA
			for (Aluno a : lstConcluidos) {
				System.out.println(a.getRa() + " - " + a.getNome() + " - " + a.getDtConc());
				System.out.println("Semestre " + semestre);
			}

			// MENSAGEM CASO NADA FOR ENCONTRADO
			if (lstConcluidos.isEmpty()) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Nenhum aluno concluiu o estágio supervisionado no semestre " + semestre + "/" + ano, ""));

			} else {
				aux = 0;
			}
		}
	}

	public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
		Document pdf = (Document) document;
		pdf.open();
		pdf.setPageSize(PageSize.A4);

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String logo = externalContext.getRealPath("") + File.separator + "img" + File.separator + "logoFatec.jpg";
		pdf.add(Image.getInstance(logo));

		Paragraph p = new Paragraph(tituloRel+"\n\n");
		p.setAlignment("center");
		pdf.add(p);

		System.out.println("passou por aqui");
	}
	// GET E SET

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isLogado() {
		return logado;
	}

	public void setLogado(boolean logado) {
		this.logado = logado;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Equivalencia getEquivalencia() {
		return equivalencia;
	}

	public void setEquivalencia(Equivalencia equivalencia) {
		this.equivalencia = equivalencia;
	}

	public Administrador getAdministrador() {
		return administrador;
	}

	public void setAdministrador(Administrador administrador) {
		this.administrador = administrador;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public List<Aluno> getLstAlunos() {
		return lstAlunos;
	}

	public void setLstAlunos(List<Aluno> lstAlunos) {
		this.lstAlunos = lstAlunos;
	}

	public List<Curso> getLstCursos() {
		return lstCursos;
	}

	public void setLstCursos(List<Curso> lstCursos) {
		this.lstCursos = lstCursos;
	}

	public List<Curso> getLstCursosEdit() {
		return lstCursosEdit;
	}

	public void setLstCursosEdit(List<Curso> lstCursosEdit) {
		this.lstCursosEdit = lstCursosEdit;
	}

	public List<Equivalencia> getLstEquivalencia() {
		return lstEquivalencia;
	}

	public void setLstEquivalencia(List<Equivalencia> lstEquivalencia) {
		this.lstEquivalencia = lstEquivalencia;
	}

	public List<Aluno> getLstPendentes() {
		return lstPendentes;
	}

	public void setLstPendentes(List<Aluno> lstPendentes) {
		this.lstPendentes = lstPendentes;
	}

	public IAlunoDao getAlunoDao() {
		return alunoDao;
	}

	public void setAlunoDao(IAlunoDao alunoDao) {
		this.alunoDao = alunoDao;
	}

	public String getTermoCompromisso() {
		return termoCompromisso;
	}

	public void setTermoCompromisso(String termoCompromisso) {
		this.termoCompromisso = termoCompromisso;
	}

	public String getPlanoAtividade() {
		return planoAtividade;
	}

	public void setPlanoAtividade(String planoAtividade) {
		this.planoAtividade = planoAtividade;
	}

	public String getRelatorio1() {
		return relatorio1;
	}

	public void setRelatorio1(String relatorio1) {
		this.relatorio1 = relatorio1;
	}

	public String getRelatorio2() {
		return relatorio2;
	}

	public void setRelatorio2(String relatorio2) {
		this.relatorio2 = relatorio2;
	}

	public String getRelatorio3() {
		return relatorio3;
	}

	public void setRelatorio3(String relatorio3) {
		this.relatorio3 = relatorio3;
	}

	public String getRelatorio4() {
		return relatorio4;
	}

	public void setRelatorio4(String relatorio4) {
		this.relatorio4 = relatorio4;
	}

	public String getTermoAditivo() {
		return termoAditivo;
	}

	public void setTermoAditivo(String termoAditivo) {
		this.termoAditivo = termoAditivo;
	}

	public String getAvAluno() {
		return avAluno;
	}

	public void setAvAluno(String avAluno) {
		this.avAluno = avAluno;
	}

	public String getAvEmpresa() {
		return avEmpresa;
	}

	public void setAvEmpresa(String avEmpresa) {
		this.avEmpresa = avEmpresa;
	}

	public String getTermoRecisao() {
		return termoRecisao;
	}

	public void setTermoRecisao(String termoRecisao) {
		this.termoRecisao = termoRecisao;
	}

	public String getTermoRealizacao() {
		return termoRealizacao;
	}

	public void setTermoRealizacao(String termoRealizacao) {
		this.termoRealizacao = termoRealizacao;
	}

	public String getTermoContrato() {
		return termoContrato;
	}

	public void setTermoContrato(String termoContrato) {
		this.termoContrato = termoContrato;
	}

	public String getDeferido() {
		return deferido;
	}

	public void setDeferido(String deferido) {
		this.deferido = deferido;
	}

	public String getPesqNome() {
		return pesqNome;
	}

	public void setPesqNome(String pesqNome) {
		this.pesqNome = pesqNome;
	}

	public String getPesqRA() {
		return pesqRA;
	}

	public void setPesqRA(String pesqRA) {
		this.pesqRA = pesqRA;
	}

	public String getOpcPesquisa() {
		return opcPesquisa;
	}

	public void setOpcPesquisa(String opcPesquisa) {
		this.opcPesquisa = opcPesquisa;
	}

	public int getTotalHoras() {
		return totalHoras;
	}

	public void setTotalHoras(int totalHoras) {
		this.totalHoras = totalHoras;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getEstagioConcluido() {
		return estagioConcluido;
	}

	public void setEstagioConcluido(String estagioConcluido) {
		this.estagioConcluido = estagioConcluido;
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public List<Aluno> getLstConcluidos() {
		return lstConcluidos;
	}

	public void setLstConcluidos(List<Aluno> lstConcluidos) {
		this.lstConcluidos = lstConcluidos;
	}

	public int getAux() {
		return aux;
	}

	public void setAux(int aux) {
		this.aux = aux;
	}

	public String getImgRel() {
		return tituloRel;
	}

	public void setImgRel(String imgRel) {
		this.tituloRel = imgRel;
	}

}
