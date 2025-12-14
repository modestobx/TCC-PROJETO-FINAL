package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Turma extends Model {


	@Required
	public String nome; // Ex: "8º Ano B"
	@Required
	public int anoLetivo; // Ex: 2025
	@Required
	public String periodo; // Ex: "Manhã", "Tarde"
    
    @OneToMany(mappedBy = "turma")
    public List<Aluno> alunos;
    
    @Required
    @Enumerated(EnumType.STRING)
    public Status status;
    
    public Turma() {
        this.status = Status.ATIVO; 
    }
	
	
}
