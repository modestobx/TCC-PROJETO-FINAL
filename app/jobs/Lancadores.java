package jobs;

import java.util.Date;

import models.Aluno;
import models.Perfil;
import models.Status;
import models.Turma;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Lancadores extends Job {
	
	public void doJob() throws Exception {
		
     if (Aluno.count() == 0) {
            
            System.out.println(">>> Banco de dados vazio. Populando com dados iniciais...");
		
			Aluno admin = new Aluno();
			admin.nome = "admin";	
			admin.cpf = "000.000.000-00";
            admin.endereco = "N/A";
			admin.matricula= "2000000";
			admin.senha="11111";
			admin.perfil = Perfil.ADMIN;
			admin.save();
			
			Aluno maria = new Aluno();
			maria.nome = "maria da Silva";
			maria.cpf = "894.125.486-06";
			maria.endereco = "joao camara";
			maria.dataNascimento = new Date();
			maria.matricula= "2025001";
			maria.senha="22222";
			maria.perfil = Perfil.ESTUDANTE;
			maria.save();
			
			Turma SextoAno = new Turma();
			SextoAno.nome ="6° Ano";
			SextoAno.anoLetivo = 2025;
			SextoAno.periodo = "Tarde";
			SextoAno.save();
			
			
	
			System.out.println(">>> Dados iniciais cadastrados com sucesso.");
     }
	}

	

}



