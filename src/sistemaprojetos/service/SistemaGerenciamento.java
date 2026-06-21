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
        
        // Coordenador Padrão (Login único e imutável no início)
        usuarios.add(new Coordenador("Admin", "admin", "admin123"));
    }

    public static SistemaGerenciamento getInstance() {
        if (instancia == null) {
            instancia = new SistemaGerenciamento();
        }
        return instancia;
    }

    // --- AUTENTICAÇÃO ---
    public Usuario fazerLogin(String email, String senha) throws RegraNegocioException {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.validarSenha(senha)) {
                if (!u.isAtivo()) {
                    throw new RegraNegocioException("Usuário inativo. Procure o coordenador.");
                }
                return u;
            }
        }
        throw new RegraNegocioException("Email ou senha incorretos.");
    }

    public void cadastrarUsuario(Usuario u) {
        // Verifica se o email já existe
        for (Usuario existente : usuarios) {
            if (existente.getEmail().equalsIgnoreCase(u.getEmail())) {
                System.err.println("Erro: Já existe um usuário com este email.");
                return;
            }
        }
        usuarios.add(u);
        System.out.println("Cadastro realizado com sucesso!");
    }

    // --- PROJETOS ---
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
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private Projeto buscarProjetoPorId(int id) {
        for (Projeto p : projetos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // --- LISTAGENS E GERENCIAMENTO ---
    public void listarProjetosDisponiveis() {
        System.out.println("\n--- Projetos Disponíveis ---");
        boolean achou = false;
        for (Projeto p : projetos) {
            if (p.getStatus().equals(Projeto.STATUS_ABERTO)) {
                p.exibirDetalhes();
                achou = true;
            }
        }
        if (!achou) System.out.println("Nenhum projeto com vagas abertas no momento.");
    }
    
    public void listarTodosUsuarios(Usuario solicitante) {
        if (solicitante instanceof Coordenador) {
            System.out.println("\n--- Lista de Usuários ---");
            for (Usuario u : usuarios) {
                u.exibirDetalhes();
            }
        } else {
            System.err.println("Acesso Negado.");
        }
    }

    public void alterarStatusUsuario(Usuario solicitante, int idUsuario) {
        if (!(solicitante instanceof Coordenador)) {
            System.err.println("Acesso Negado.");
            return;
        }
        for (Usuario u : usuarios) {
            if (u.getId() == idUsuario) {
                if (u instanceof Coordenador) {
                    System.err.println("Não é possível alterar o status do coordenador geral.");
                    return;
                }
                u.setAtivo(!u.isAtivo());
                System.out.println("Status do usuário alterado com sucesso! Ativo: " + u.isAtivo());
                return;
            }
        }
        System.err.println("Usuário não encontrado.");
    }

    public void imprimirEstatisticasGerais(Usuario solicitante) {
        if (!(solicitante instanceof Coordenador)) {
            System.err.println("Acesso Negado.");
            return;
        }
        System.out.println("\n--- Estatísticas Gerais do Sistema ---");
        System.out.println("Total de Projetos: " + projetos.size());
        System.out.println("Total de Usuários Cadastrados: " + usuarios.size());
        long profsAtivos = usuarios.stream().filter(u -> u instanceof Professor && u.isAtivo()).count();
        System.out.println("Professores Ativos: " + profsAtivos);
        System.out.println("--------------------------------------\n");
    }
    
    // Auxiliar para a UI encontrar professor ao criar projeto
    public Professor buscarProfessor(String nomeProfessor) {
        for (Usuario u : usuarios) {
            if (u instanceof Professor && u.getNome().equalsIgnoreCase(nomeProfessor)) {
                return (Professor) u;
            }
        }
        return null;
    }
}