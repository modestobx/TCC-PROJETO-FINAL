package models;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.data.validation.Match;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Aluno extends Model{

	@Required(message="O nome é obrigatório")
	@Match(value="^[^\\d\\p{Punct}]+$", message="O nome deve conter apenas letras e espaços")
    public String nome;

    @Required(message="O CPF é obrigatório")
    @Match(value="^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message="Formato de CPF inválido (use xxx.xxx.xxx-xx)")
    public String cpf;
    
	public String endereco;
	
	
    @Column(unique = true)
    public String matricula;
	
	
	@MinSize(value=6, message="A senha deve ter no mínimo 6 caracteres")
	public String senha;
	
	@Required
	@Enumerated(EnumType.STRING)
	public Perfil perfil;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataNascimento;
	
	@Transient
	public Integer idade;
	
	@Required
	@Enumerated(EnumType.STRING)
    public Status status;
	
	@ManyToOne
    public Turma turma;
	
	public Aluno() {
        this.status = Status.ATIVO;
        this.perfil = perfil.ESTUDANTE;
    }
	
    public static String gerarProximaMatricula() {
        int anoAtual = java.time.LocalDate.now().getYear();
        String maxMatricula = Aluno.find("select max(a.matricula) from Aluno a where a.matricula like ?1", anoAtual + "%").first();
        
        if (maxMatricula == null) {
            return anoAtual + "001";
        }
        
        String sequenciaStr = maxMatricula.substring(4); 
        int sequencia = Integer.parseInt(sequenciaStr);
        sequencia++;
        
        return anoAtual + String.format("%03d", sequencia);
    }
	
	
	
	
}
