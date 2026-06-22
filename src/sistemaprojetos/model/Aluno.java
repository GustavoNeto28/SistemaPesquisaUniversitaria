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
    
    public String obterHistoricoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Histórico de Projetos ---\n\n");
        if (historicoProjetos.isEmpty()) {
            sb.append("Nenhum projeto no histórico.");
        } else {
            for (Projeto p : historicoProjetos) {
                sb.append("- ID: ").append(p.getId()).append(" | Área: ").append(p.getAreaEstudo()).append("\n");
            }
        }
        return sb.toString();
    }
}