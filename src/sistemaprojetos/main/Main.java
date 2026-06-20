package sistemaprojetos.main;

import sistemaprojetos.service.SistemaGerenciamento;
import sistemaprojetos.model.*;

public class Main {

	public static void main(String[] args) {
		SistemaGerenciamento sistema = SistemaGerenciamento.getInstance();

        // Cadastro de Usuários
        Coordenador coord = new Coordenador("Ana Souza", "ana.coord@universidade.edu");
        Professor prof = new Professor("Dr. Carlos", "carlos.prof@universidade.edu");
        Aluno aluno1 = new Aluno("João Silva", "joao.aluno@universidade.edu");
        Aluno aluno2 = new Aluno("Maria Rita", "maria.aluno@universidade.edu");

        sistema.cadastrarUsuario(coord);
        sistema.cadastrarUsuario(prof);
        sistema.cadastrarUsuario(aluno1);
        sistema.cadastrarUsuario(aluno2);

        // Gestão de Projetos (Criação)
        sistema.criarProjeto(prof, "Inteligência Artificial na Medicina", "Ciência da Computação", prof, 1);
        sistema.criarProjeto(coord, "Algoritmos Quânticos", "Física/Computação", prof, 3);
        
        System.out.println("\n-- Testando Permissões --");
        sistema.criarProjeto(aluno1, "Projeto Hacker", "Segurança", prof, 2);

        // Participações
        System.out.println("\n-- Alunos solicitando participação --");
        sistema.listarProjetosDisponiveis();
        
        sistema.solicitarParticipacao(aluno1, 1);
        sistema.solicitarParticipacao(aluno2, 1);

        // Relatórios e Estatísticas
        sistema.imprimirEstatisticasGerais(coord);

	}

}
