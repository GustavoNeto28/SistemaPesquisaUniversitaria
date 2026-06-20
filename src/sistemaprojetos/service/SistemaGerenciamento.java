package sistemaprojetos.service;

import java.util.ArrayList;
import java.util.List;

import sistemaprojetos.exception.AcessoNegadoException;
import sistemaprojetos.exception.RegraNegocioException;
import sistemaprojetos.model.*;

public class SistemaGerenciamento {
	private static SistemaGerenciamento instancia;
    private List<Usuario> usuarios;
    private List<Projeto> projetos;

    private SistemaGerenciamento() {
        usuarios = new ArrayList<>();
        projetos = new ArrayList<>();
    }

    public static SistemaGerenciamento getInstance() {
        if (instancia == null) {
            instancia = new SistemaGerenciamento();
        }
        return instancia;
    }

    public void cadastrarUsuario(Usuario u) {
        usuarios.add(u);
    }

    public void criarProjeto(Usuario solicitante, String titulo, String area, Professor orientador, int vagas) {
        try {
            if (!(solicitante instanceof Professor) && !(solicitante instanceof Coordenador)) {
                throw new AcessoNegadoException("Apenas Professores ou Coordenadores podem criar projetos.");
            }
            Projeto novoProjeto = new Projeto(titulo, area, orientador, vagas);
            projetos.add(novoProjeto);
            System.out.println("Projeto '" + titulo + "' criado com sucesso!");
        } catch (AcessoNegadoException e) {
            System.err.println("Erro de Permissão: " + e.getMessage());
        }
    }

    public void solicitarParticipacao(Aluno aluno, int idProjeto) {
        try {
            Projeto projeto = buscarProjetoPorId(idProjeto);
            if (projeto == null) {
                throw new RegraNegocioException("Projeto não encontrado.");
            }
            projeto.adicionarParticipante(aluno);
            System.out.println("Sucesso: " + aluno.getNome() + " ingressou no projeto '" + projeto.getId() + "'.");
        } catch (RegraNegocioException e) {
            System.err.println("Erro ao ingressar no projeto: " + e.getMessage());
        }
    }

    private Projeto buscarProjetoPorId(int id) {
        for (Projeto p : projetos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public void imprimirEstatisticasGerais(Usuario solicitante) {
        if (!(solicitante instanceof Coordenador)) {
            System.err.println("Acesso Negado: Apenas o Coordenador pode visualizar as estatísticas.");
            return;
        }
        
        System.out.println("\n--- Estatísticas Gerais do Sistema ---");
        System.out.println("Total de Projetos: " + projetos.size());
        System.out.println("Total de Usuários Cadastrados: " + usuarios.size());
        
        long profsAtivos = usuarios.stream().filter(u -> u instanceof Professor && u.isAtivo()).count();
        System.out.println("Professores Ativos: " + profsAtivos);
        System.out.println("--------------------------------------\n");
    }

    public void listarProjetosDisponiveis() {
        System.out.println("\n--- Projetos Disponíveis ---");
        for (Projeto p : projetos) {
            if (p.getStatus().equals(Projeto.STATUS_ABERTO)) {
                p.exibirDetalhes();
            }
        }
    }
}
