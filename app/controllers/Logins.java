package controllers;

import models.Aluno;
import models.Perfil;
import play.mvc.Controller;

public class Logins extends Controller {

	public static void form() {
		if (session.contains("usuarioLogado")) {

			
			Application.index();

		} else {
			render();
		}

	}

	public static void logar(String matricula, String senha) {
		Aluno aluno = Aluno.find("matricula = ?1 and senha = ?2", matricula, senha).first();
		if (aluno == null) {
			flash.error("Matricula ou senha inválidos");
			form();

		} else {
			// --- INÍCIO DO CÓDIGO DE DIAGNÓSTICO ---
			System.out.println("========================================");
			System.out.println("LOGIN BEM-SUCEDIDO!");
			System.out.println("Aluno encontrado: " + aluno.nome);
			System.out.println("Perfil do Aluno no Banco: " + aluno.perfil); // Isso deve imprimir ADMIN

			// --- FIM DO CÓDIGO DE DIAGNÓSTICO ---
			session.put("usuarioLogado", aluno.matricula);
			session.put("usuarioPerfil", aluno.perfil.name());
			flash.success("Logado com sucesso!");
			
			if (aluno.perfil == models.Perfil.ADMIN) {
	             // 1. ADMIN vai para o painel de admin
	             PaginaAdmin.form1(); 
	        } else {
	             // 2. ALUNO (ou qualquer outro perfil) vai para a página principal
	             Application.index();
	        }

		}

		
	}
	

	public static void logout() {
		session.clear();
		flash.success("Você saiu do sistema!");
		form();
	}

}
