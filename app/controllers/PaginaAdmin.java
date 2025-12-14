package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Aluno;
import models.Turma;
import play.mvc.Controller;
import play.mvc.With;
import security.Administrador;

@With(Seguranca.class)
public class PaginaAdmin extends Controller {

	@Administrador
	public static void form1() {
       

        String matricula = session.get("usuarioLogado");
        Aluno adminLogado = Aluno.find("byMatricula", matricula).first();

        long totalAlunos = Aluno.count("perfil =  ?1", models.Perfil.ESTUDANTE);
        long totalTurmas = Turma.count();
        long alunosMatriculados = Aluno.count("perfil = ?1 AND turma IS NOT NULL", 
                models.Perfil.ESTUDANTE);
        long novasMatriculas = Aluno.count("status = ?1", models.Status.PENDENTE);

        
        render(adminLogado, totalAlunos, totalTurmas, alunosMatriculados, novasMatriculas);

       
        
    }
	
	@Administrador
	public static void listarTurmas() {
	    List<Turma> turmas = Turma.findAll();
	    
	    render(turmas); 
	}

	@Administrador
	public static void formTurma() {
	    render(); 
	}

	@Administrador
	public static void salvarTurma(Turma turma) {
	    
	    
	    validation.valid(turma);

	    if (validation.hasErrors()) {
	        
	        params.flash(); 
	        
	        flash.error("Erro ao salvar: Verifique os campos.");
	        
	        formTurma(); 
	    }

	    try {
	        turma.save();
	        flash.success("Turma '%s' salva com sucesso!", turma.nome);
	        
	    } catch (Exception e) {
	        flash.error("Ocorreu um erro inesperado ao salvar: " + e.getMessage());
	        params.flash();
	        formTurma();
	    }
	    
	    listarTurmas();
	}
	
	@Administrador
	public static void editarTurma(Long id) {
	    Turma turma = Turma.findById(id);

	    if (turma == null) {
	        flash.error("Turma não encontrada.");
	        listarTurmas();
	    }

	    render("PaginaAdmin/formTurma.html", turma);
	}
	
	@Administrador
	public static void RemoverTurma(Long id) {
	    Turma turma = Turma.findById(id);

	    if (turma == null) {
	        flash.error("Turma não encontrada.");
	        listarTurmas();
	    }

	    if (turma.alunos != null && !turma.alunos.isEmpty()) {
	        flash.error("Atenção! Não é possível inativar a turma '%s', pois ela já possui alunos.", turma.nome);
	        listarTurmas();
	    }

	    turma.status = models.Status.INATIVO;
	    
	    turma.save();
	    
	    flash.success("Turma '%s' foi inativada com sucesso.", turma.nome);
	    
	    listarTurmas();
	}
	@Administrador
	public static void reativarTurma(Long id) {
	    Turma turma = Turma.findById(id);

	    if (turma == null) {
	        flash.error("Turma não encontrada.");
	        listarTurmas();
	    }

	    turma.status = models.Status.ATIVO;
	    
	    turma.save();
	    
	    flash.success("Turma '%s' foi reativada com sucesso.", turma.nome);
	    
	    listarTurmas();
	}

	@Administrador
	public static void paginaMatricula(Long turmaId) {
	    List<Turma> turmasAtivas = Turma.find("status = ?1", models.Status.ATIVO).fetch();
	    
	    Turma turmaSelecionada = null;
	    List<Aluno> alunosMatriculados = new ArrayList<Aluno>();
	    List<Aluno> alunosDisponiveis = new ArrayList<Aluno>();

	    if (turmaId != null) {
	        turmaSelecionada = Turma.findById(turmaId);
	        
	        if(turmaSelecionada != null) {
	            // Busca quem já está na turma
	            alunosMatriculados = Aluno.find("byTurma", turmaSelecionada).fetch();
	            
	            
	            alunosDisponiveis = Aluno.find("turma is null and status = ?1 and perfil = ?2", 
	                                           models.Status.ATIVO, 
	                                           models.Perfil.ESTUDANTE).fetch();
	        }
	    }
	    
	    render(turmasAtivas, turmaSelecionada, alunosMatriculados, alunosDisponiveis);
	}

	@Administrador
	public static void matricularAlunos(Long turmaId, Long[] alunoIds) {

		Turma turma = Turma.findById(turmaId);

	    // 2. Verifica se o admin selecionou algum aluno
	    if (alunoIds != null && turma != null) {
	        for (Long idAluno : alunoIds) {
	            Aluno aluno = Aluno.findById(idAluno);
	            if (aluno != null) {

	            	aluno.turma = turma; // Atribui a turma ao aluno
	                aluno.save();
	            }
	        }
	        flash.success(alunoIds.length + " aluno(s) matriculado(s) com sucesso na turma " + turma.nome);
	    } else {
	        flash.error("Nenhum aluno foi selecionado ou a turma é inválida.");
	    }
	    
	    paginaMatricula(turmaId);
	}

}