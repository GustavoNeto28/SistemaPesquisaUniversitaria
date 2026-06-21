package sistemaprojetos.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {
    private List<Projeto> historicoProjetos;

    public Aluno(String nome, String email, String senha) {
        super(nome, email, senha);
        this.historicoProjetos = new ArrayList<>();
    }

    @Override
    public String getTipoUsuario() { return "ALUNO"; }

    public void adicionarAoHistorico(Projeto p) { historicoProjetos.add(p); }
    
    public void exibirHistorico() {
        System.out.println("\n--- Histórico de Projetos ---");
        if (historicoProjetos.isEmpty()) {
            System.out.println("Nenhum projeto no histórico.");
        } else {
            for (Projeto p : historicoProjetos) {
                System.out.println("- " + p.getId() + " | Área: " + p.getAreaEstudo());
            }
        }
    }
}