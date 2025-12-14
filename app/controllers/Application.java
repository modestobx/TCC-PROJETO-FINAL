package controllers;

import play.*;
import play.data.validation.Valid;
import play.mvc.*;

import java.util.*;

import models.Aluno;
import models.Perfil;
import models.Status;


public class Application extends Controller {

	public static void index() {
        Aluno alunoLogado = null;
        
        // Verifica se existe alguém na sessão
        if (session.contains("usuarioLogado")) {
            String matricula = session.get("usuarioLogado");
            alunoLogado = Aluno.find("byMatricula", matricula).first();
        }
        
        // Envia o objeto (pode ser um Aluno ou null)
        render(alunoLogado);
    }
	// Abre o formulário para qualquer pessoa
	public static void preMatricula() {
		render();
	}
	public static void comprovantePre() {
		render();
	}

    // Salva o pré-cadastro
    public static void salvarPreMatricula(@Valid Aluno aluno) {
        
        // Valida senha manualmente pois é obrigatória aqui
        if (aluno.senha == null || aluno.senha.isEmpty()) {
            validation.addError("aluno.senha", "Crie uma senha para acessar o sistema.");
        }

        if (validation.hasErrors()) {
            params.flash();
            flash.error("Corrija os erros abaixo.");
            render("Application/preMatricula.html", aluno);
        }

        // Preenche dados automáticos
        aluno.matricula = Aluno.gerarProximaMatricula(); // Usa o método que movemos
        aluno.perfil = Perfil.ESTUDANTE;
        aluno.status = Status.PENDENTE; // Fica pendente até o admin confirmar
        
        aluno.save();

        // Manda para uma tela de sucesso com o número gerado
        sucesso(aluno.id);
    }
 // app/controllers/Application.java

    public static void comprovante(Long id) {
        Aluno aluno = Aluno.findById(id);
        if (aluno == null) index();
        
        // Público SEMPRE vê o comprovante de Pré-Matrícula
        render("Application/comprovantePre.html", aluno);
    }
    public static void sucesso(Long id) {
        Aluno aluno = Aluno.findById(id);
        render(aluno);
    }
}
