package sistemaprojetos.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario{
	private List<Projeto> historicoProjetos;

    public Aluno(String nome, String email) {
        super(nome, email);
        this.historicoProjetos = new ArrayList<>();
    }

    @Override
    public String getTipoUsuario() { return "ALUNO"; }

    public void adicionarAoHistorico(Projeto p) { 
        historicoProjetos.add(p); 
    }
}
