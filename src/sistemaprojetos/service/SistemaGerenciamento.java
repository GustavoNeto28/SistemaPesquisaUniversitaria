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
        usuarios.add(new Coordenador("Admin", "admin", "admin123"));
    }

    public static SistemaGerenciamento getInstance() {
        if (instancia == null) {
            instancia = new SistemaGerenciamento();
        }
        return instancia;
    }

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

    public void cadastrarUsuario(Usuario u) throws RegraNegocioException {
        for (Usuario existente : usuarios) {
            if (existente.getEmail().equalsIgnoreCase(u.getEmail())) {
                throw new RegraNegocioException("Já existe um usuário com este email.");
            }
        }
        usuarios.add(u);
    }

    public void criarProjeto(Usuario solicitante, String titulo, String area, Professor orientador, int vagas) throws AcessoNegadoException {
        if (!(solicitante instanceof Professor) && !(solicitante instanceof Coordenador)) {
            throw new AcessoNegadoException("Apenas Professores ou Coordenadores podem criar projetos.");
        }
        Projeto novoProjeto = new Projeto(titulo, area, orientador, vagas);
        projetos.add(novoProjeto);
    }

    public void solicitarParticipacao(Aluno aluno, int idProjeto) throws RegraNegocioException {
        Projeto projeto = buscarProjetoPorId(idProjeto);
        if (projeto == null) {
            throw new RegraNegocioException("Projeto não encontrado.");
        }
        projeto.adicionarParticipante(aluno);
    }

    private Projeto buscarProjetoPorId(int id) {
        for (Projeto p : projetos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public String listarProjetosDisponiveis() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Projetos Disponíveis ---\n\n");
        boolean achou = false;
        for (Projeto p : projetos) {
            if (p.getStatus().equals(Projeto.STATUS_ABERTO)) {
                sb.append("ID: ").append(p.getId())
                  .append(" | Área: ").append(p.getAreaEstudo()).append("\n");
                achou = true;
            }
        }
        if (!achou) sb.append("Nenhum projeto com vagas abertas no momento.\n");
        return sb.toString();
    }
    
    public String listarTodosUsuarios(Usuario solicitante) throws AcessoNegadoException {
        if (!(solicitante instanceof Coordenador)) {
            throw new AcessoNegadoException("Acesso Negado.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- Lista de Usuários ---\n\n");
        for (Usuario u : usuarios) {
            sb.append("[").append(u.getTipoUsuario()).append("] ID: ").append(u.getId())
              .append(" | ").append(u.getNome()).append(" (").append(u.getEmail())
              .append(") - Ativo: ").append(u.isAtivo() ? "Sim" : "Não").append("\n");
        }
        return sb.toString();
    }

    public void alterarStatusUsuario(Usuario solicitante, int idUsuario) throws AcessoNegadoException, RegraNegocioException {
        if (!(solicitante instanceof Coordenador)) {
            throw new AcessoNegadoException("Acesso Negado.");
        }
        for (Usuario u : usuarios) {
            if (u.getId() == idUsuario) {
                if (u instanceof Coordenador) {
                    throw new RegraNegocioException("Não é possível alterar o status do coordenador geral.");
                }
                u.setAtivo(!u.isAtivo());
                return;
            }
        }
        throw new RegraNegocioException("Usuário não encontrado.");
    }

    public String imprimirEstatisticasGerais(Usuario solicitante) throws AcessoNegadoException {
        if (!(solicitante instanceof Coordenador)) {
            throw new AcessoNegadoException("Acesso Negado.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- Estatísticas Gerais do Sistema ---\n\n");
        sb.append("Total de Projetos: ").append(projetos.size()).append("\n");
        sb.append("Total de Usuários Cadastrados: ").append(usuarios.size()).append("\n");
        long profsAtivos = usuarios.stream().filter(u -> u instanceof Professor && u.isAtivo()).count();
        sb.append("Professores Ativos: ").append(profsAtivos).append("\n");
        return sb.toString();
    }
    
    public Professor buscarProfessor(String nomeProfessor) {
        for (Usuario u : usuarios) {
            if (u instanceof Professor && u.getNome().equalsIgnoreCase(nomeProfessor)) {
                return (Professor) u;
            }
        }
        return null;
    }
}