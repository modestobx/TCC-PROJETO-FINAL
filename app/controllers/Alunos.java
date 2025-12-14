package controllers;

import java.time.LocalDate;
import java.util.List;
import models.Aluno;
import models.Status;
import models.Turma;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;        
import security.Administrador;

@With(Seguranca.class) 
public class Alunos extends Controller {

	 
		@Administrador
	    public static void listar(String termo) { 
	        List<Aluno> alunos = null;

	        if (termo == null || termo.isEmpty()) {
	            alunos = Aluno.find("perfil = ?1", models.Perfil.ESTUDANTE).fetch();
	        } else {
	            alunos = Aluno.find("perfil = ?1 AND lower(nome) like ?2", 
	                                models.Perfil.ESTUDANTE, 
	                                "%" + termo.toLowerCase() + "%").fetch();
	        }
	        
	        render(alunos, termo); 
	    }	
		@Administrador
	    public static void listarAjax(String termo) {
	        List<Aluno> alunos = null;

	        // 1. Busca no banco
	        if (termo == null || termo.isEmpty()) {
	            alunos = Aluno.find("perfil = ?1", models.Perfil.ESTUDANTE).fetch();
	        } else {
	            alunos = Aluno.find("perfil = ?1 AND lower(nome) like ?2", 
	                                models.Perfil.ESTUDANTE, 
	                                "%" + termo.toLowerCase() + "%").fetch();
	        }

	        List<java.util.Map<String, Object>> listaJson = new java.util.ArrayList<java.util.Map<String, Object>>();

	        for (Aluno a : alunos) {
	            java.util.Map<String, Object> mapa = new java.util.HashMap<String, Object>();
	            mapa.put("id", a.id);
	            mapa.put("nome", a.nome);
	            mapa.put("matricula", a.matricula);
	            
	            if (a.turma != null) {
	                mapa.put("turmaNome", a.turma.nome);
	            } else {
	                mapa.put("turmaNome", "Sem Turma");
	            }
	            
	            mapa.put("status", a.status.toString());
	            
	            listaJson.add(mapa);
	        }

	        renderJSON(listaJson);
	    }
		
		

		@Administrador
		public static void comprovante(Long id) {
		    Aluno aluno = Aluno.findById(id);
		    
		    // LÓGICA INTELIGENTE:
		    if (aluno.status == models.Status.PENDENTE) {
		        // Se ainda está pendente, imprime o Protocolo
		        render("Application/comprovantePre.html", aluno);
		    } else {
		        // Se já está ativo/matriculado, imprime o Oficial
		        render("Alunos/comprovante.html", aluno);
		    }
		}
   


    
	
    @Administrador
    public static void form() { 
        List<Turma> turmas = Turma.find("status = ?1", models.Status.ATIVO).fetch();
        render(turmas);
    }

    @Administrador
    public static void editar(Long id) { 
        Aluno aluno = Aluno.findById(id);
        List<Turma> turmas = Turma.find("status = ?1", models.Status.ATIVO).fetch();
        
        if (aluno == null) {
            flash.error("Aluno não encontrado.");
            listar("");
        }
        
        // Renderiza a view do formulário
        render("Alunos/form.html", aluno, turmas); 
    }
    @Administrador
    public static void salvar(Aluno aluno) {
        
        boolean isNew = (aluno.id == null);

        if (isNew) {
        	
        	// Gera a matrícula ANTES de validar
            aluno.matricula = Aluno.gerarProximaMatricula();
        	
            aluno.perfil = models.Perfil.ESTUDANTE;
            aluno.status = models.Status.ATIVO;
        }

        
        if (isNew) {
            Aluno alunoDuplicado = Aluno.find("byMatricula", aluno.matricula).first();
            if (alunoDuplicado != null) {
                validation.addError("aluno.matricula", "Esta matrícula já está em uso.");
            }
            if (aluno.senha == null || aluno.senha.isEmpty()) {
                validation.addError("aluno.senha", "A senha é obrigatória para novos alunos.");
            }
        } else {
            if (aluno.senha == null || aluno.senha.isEmpty()) {
                Aluno alunoAntigo = Aluno.findById(aluno.id);
                aluno.senha = alunoAntigo.senha;
            }
        }
        
        validation.valid(aluno);
        
        if(validation.hasErrors()) {
            params.flash(); 
            
            flash.error("Erro ao salvar: Verifique os campos.");
            List<Turma> turmas = Turma.find("status = ?1", models.Status.ATIVO).fetch();
            render("Alunos/form.html", aluno, turmas);
        }

        aluno.save();
        
        String acao = isNew ? "cadastrado" : "atualizado";
        flash.success("Aluno '%s' (Matrícula: %s) %s com sucesso!", aluno.nome, aluno.matricula, acao);
        
        listar("");
    }
   


    @Administrador
    public static void inativar(Long id) { 
        Aluno aluno = Aluno.findById(id);
        if(aluno != null) {
            aluno.status = models.Status.INATIVO;
            aluno.save();
            flash.success("Aluno '%s' inativado com sucesso.", aluno.nome);
        } else {
            flash.error("Aluno não encontrado.");
        }
        listar("");
    }

    @Administrador
    public static void reativar(Long id) {
        Aluno aluno = Aluno.findById(id);
        if(aluno != null) {
            aluno.status = models.Status.ATIVO;
            aluno.save();
            flash.success("Aluno '%s' reativado com sucesso.", aluno.nome);
        } else {
            flash.error("Aluno não encontrado.");
        }
        listar("");
    }
}