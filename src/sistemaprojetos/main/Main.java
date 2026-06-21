package sistemaprojetos.main;

import java.util.Scanner;
import sistemaprojetos.service.SistemaGerenciamento;
import sistemaprojetos.model.*;
import sistemaprojetos.exception.RegraNegocioException;

public class Main {
    private static SistemaGerenciamento sistema = SistemaGerenciamento.getInstance();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean executando = true;

        while (executando) {
            System.out.println("\n=================================");
            System.out.println("SISTEMA DE PROJETOS UNIVERSITÁRIOS");
            System.out.println("=================================");
            System.out.println("1. Login");
            System.out.println("2. Cadastro (Aluno/Professor)");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    fazerLogin();
                    break;
                case "2":
                    fazerCadastro();
                    break;
                case "3":
                    executando = false;
                    System.out.println("Encerrando o sistema...");
                    break;
                default:
                    System.err.println("Opção inválida!");
            }
        }
        scanner.close();
    }

    private static void fazerLogin() {
        System.out.print("\nEmail: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            Usuario usuarioLogado = sistema.fazerLogin(email, senha);
            System.out.println("\nBem-vindo(a), " + usuarioLogado.getNome() + "!");
            direcionarMenu(usuarioLogado);
        } catch (RegraNegocioException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void fazerCadastro() {
        System.out.println("\n--- Cadastro ---");
        System.out.println("1. Sou Aluno");
        System.out.println("2. Sou Professor");
        System.out.print("Escolha: ");
        String tipo = scanner.nextLine();

        if (!tipo.equals("1") && !tipo.equals("2")) {
            System.err.println("Opção inválida. Operação cancelada.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        if (tipo.equals("1")) {
            sistema.cadastrarUsuario(new Aluno(nome, email, senha));
        } else {
            sistema.cadastrarUsuario(new Professor(nome, email, senha));
        }
    }

    // --- ROTEADOR DE MENUS ---
    private static void direcionarMenu(Usuario usuario) {
        boolean logado = true;
        while (logado) {
            if (usuario instanceof Aluno) {
                logado = menuAluno((Aluno) usuario);
            } else if (usuario instanceof Professor) {
                logado = menuProfessor((Professor) usuario);
            } else if (usuario instanceof Coordenador) {
                logado = menuCoordenador((Coordenador) usuario);
            }
        }
    }

    // --- MENU ALUNO ---
    private static boolean menuAluno(Aluno aluno) {
        System.out.println("\n--- PAINEL DO ALUNO ---");
        System.out.println("1. Ver projetos disponíveis");
        System.out.println("2. Solicitar participação em projeto");
        System.out.println("3. Ver meu histórico");
        System.out.println("4. Sair (Logout)");
        System.out.print("Escolha: ");
        
        switch (scanner.nextLine()) {
            case "1":
                sistema.listarProjetosDisponiveis();
                break;
            case "2":
                sistema.listarProjetosDisponiveis();
                System.out.print("\nDigite o ID do projeto que deseja ingressar: ");
                try {
                    int idProjeto = Integer.parseInt(scanner.nextLine());
                    sistema.solicitarParticipacao(aluno, idProjeto);
                } catch (NumberFormatException e) {
                    System.err.println("ID inválido.");
                }
                break;
            case "3":
                aluno.exibirHistorico();
                break;
            case "4":
                return false; // Retorna false para quebrar o loop e deslogar
            default:
                System.err.println("Opção inválida!");
        }
        return true;
    }

    // --- MENU PROFESSOR ---
    private static boolean menuProfessor(Professor prof) {
        System.out.println("\n--- PAINEL DO PROFESSOR ---");
        System.out.println("1. Criar novo projeto");
        System.out.println("2. Ver projetos disponíveis no sistema");
        System.out.println("3. Sair (Logout)");
        System.out.print("Escolha: ");
        
        switch (scanner.nextLine()) {
            case "1":
                criarProjetoInterativo(prof);
                break;
            case "2":
                sistema.listarProjetosDisponiveis();
                break;
            case "3":
                return false;
            default:
                System.err.println("Opção inválida!");
        }
        return true;
    }

    // --- MENU COORDENADOR ---
    private static boolean menuCoordenador(Coordenador coord) {
        System.out.println("\n--- PAINEL DE CONTROLE (COORDENADOR) ---");
        System.out.println("1. Ver Estatísticas do Sistema");
        System.out.println("2. Gerenciar Usuários (Listar e Ativar/Desativar)");
        System.out.println("3. Ver todos os projetos");
        System.out.println("4. Criar projeto em nome da coordenação");
        System.out.println("5. Sair (Logout)");
        System.out.print("Escolha: ");
        
        switch (scanner.nextLine()) {
            case "1":
                sistema.imprimirEstatisticasGerais(coord);
                break;
            case "2":
                sistema.listarTodosUsuarios(coord);
                System.out.print("\nDigite o ID do usuário para Ativar/Desativar (ou 0 para cancelar): ");
                try {
                    int idUser = Integer.parseInt(scanner.nextLine());
                    if (idUser != 0) sistema.alterarStatusUsuario(coord, idUser);
                } catch (NumberFormatException e) {
                    System.err.println("ID inválido.");
                }
                break;
            case "3":
                sistema.listarProjetosDisponiveis();
                break;
            case "4":
                criarProjetoInterativo(coord);
                break;
            case "5":
                return false;
            default:
                System.err.println("Opção inválida!");
        }
        return true;
    }

    // --- AUXILIAR PARA CRIAR PROJETOS ---
    private static void criarProjetoInterativo(Usuario solicitante) {
        System.out.println("\n--- Criação de Projeto ---");
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Área de Estudo: ");
        String area = scanner.nextLine();
        
        Professor orientador = null;
        if (solicitante instanceof Professor) {
            orientador = (Professor) solicitante; // O próprio professor é o orientador
        } else {
            // Se for coordenador criando, ele deve digitar o nome de um professor válido
            System.out.print("Nome do Professor Orientador cadastrado: ");
            String nomeProf = scanner.nextLine();
            orientador = sistema.buscarProfessor(nomeProf);
            if (orientador == null) {
                System.err.println("Professor não encontrado. Crie uma conta de professor primeiro.");
                return;
            }
        }

        System.out.print("Número de Vagas: ");
        try {
            int vagas = Integer.parseInt(scanner.nextLine());
            sistema.criarProjeto(solicitante, titulo, area, orientador, vagas);
        } catch (NumberFormatException e) {
            System.err.println("Número de vagas inválido. Operação cancelada.");
        }
    }
}