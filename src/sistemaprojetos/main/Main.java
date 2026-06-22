package sistemaprojetos.main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sistemaprojetos.exception.AcessoNegadoException;
import sistemaprojetos.exception.RegraNegocioException;
import sistemaprojetos.model.*;
import sistemaprojetos.service.SistemaGerenciamento;

public class Main {
    private static SistemaGerenciamento sistema = SistemaGerenciamento.getInstance();

    public static void main(String[] args) {
        boolean executando = true;

        while (executando) {
            String[] opcoesIniciais = {"Login", "Cadastro", "Sair"};
            
            
            int escolha = mostrarMenuVertical(
                    "Menu Inicial", 
                    "=== SISTEMA DE PROJETOS UNIVERSITÁRIOS ===\n\nSelecione uma opção:", 
                    opcoesIniciais
            );

            // -1 significa que o usuário fechou a janela no 'X'
            if (escolha == -1 || escolha == 2) {
                executando = false;
                JOptionPane.showMessageDialog(null, "Encerrando o sistema...", "Sair", JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            switch (escolha) {
                case 0:
                    fazerLogin();
                    break;
                case 1:
                    fazerCadastro();
                    break;
            }
        }
    }

    
    private static int mostrarMenuVertical(String titulo, String mensagem, String[] opcoes) {
        // Cria uma janela customizada
        JDialog dialog = new JDialog((Frame) null, titulo, true); 
        dialog.setSize(400, 350); 
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        // Cria o painel que vai organizar os itens verticalmente
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margens internas

        // Transforma o \n do Java em <br> do HTML para o JLabel entender a quebra de linha centralizada
        String mensagemHtml = "<html><div style='text-align: center;'>" + mensagem.replace("\n", "<br>") + "</div></html>";
        JLabel label = new JLabel(mensagemHtml);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Espaçamento entre o título e os botões

        final int[] escolha = {-1}; // Começa com -1 (fechar janela)

        // Cria os botões verticalmente iterando sobre o array de opções
        for (int i = 0; i < opcoes.length; i++) {
            JButton btn = new JButton(opcoes[i]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(250, 40)); // AUMENTA O TAMANHO DOS BOTÕES AQUI
            
            final int index = i;
            // Ação ao clicar no botão: salva a escolha e fecha a janela
            btn.addActionListener(e -> {
                escolha[0] = index;
                dialog.dispose();
            });
            
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento entre um botão e outro
        }

        dialog.add(panel);
        dialog.setVisible(true); // Bloqueia a execução do código aqui até a janela ser fechada

        return escolha[0]; // Retorna a opção clicada (0, 1, 2...) ou -1 se fechou no X
    }

    private static void fazerLogin() {
        String email = JOptionPane.showInputDialog(null, "Digite seu Email:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (email == null) return;
        
        String senha = JOptionPane.showInputDialog(null, "Digite sua Senha:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (senha == null) return;

        try {
            Usuario usuarioLogado = sistema.fazerLogin(email, senha);
            JOptionPane.showMessageDialog(null, "Bem-vindo(a), " + usuarioLogado.getNome() + "!", "Login Sucesso", JOptionPane.INFORMATION_MESSAGE);
            direcionarMenu(usuarioLogado);
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void fazerCadastro() {
        String[] opcoesCadastro = {"Sou Aluno", "Sou Professor"};
        int escolhaTipo = mostrarMenuVertical("Tipo de Cadastro", "Qual é o seu vínculo com a universidade?", opcoesCadastro);

        if (escolhaTipo == -1) return;

        String nome = JOptionPane.showInputDialog(null, "Nome completo:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        if (nome == null || nome.trim().isEmpty()) return;

        String email = JOptionPane.showInputDialog(null, "Email institucional:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        if (email == null || email.trim().isEmpty()) return;

        String senha = JOptionPane.showInputDialog(null, "Crie uma senha:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        if (senha == null || senha.trim().isEmpty()) return;

        try {
            if (escolhaTipo == 0) {
                sistema.cadastrarUsuario(new Aluno(nome, email, senha));
            } else {
                sistema.cadastrarUsuario(new Professor(nome, email, senha));
            }
            JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro no Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

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

    private static boolean menuAluno(Aluno aluno) {
        String[] opcoesAluno = {"Ver Projetos", "Solicitar Participação", "Meu Histórico", "Sair"};
        int escolha = mostrarMenuVertical("Painel do Aluno", "--- PAINEL DO ALUNO ---\nLogado como: " + aluno.getNome(), opcoesAluno);

        if (escolha == -1 || escolha == 3) return false;

        switch (escolha) {
            case 0:
                String projetos = sistema.listarProjetosDisponiveis();
                JOptionPane.showMessageDialog(null, projetos, "Projetos Disponíveis", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1:
                String listaProjetos = sistema.listarProjetosDisponiveis();
                String idStr = JOptionPane.showInputDialog(null, listaProjetos + "\nDigite o ID do projeto que deseja ingressar:", "Solicitar Participação", JOptionPane.QUESTION_MESSAGE);
                if (idStr == null) break;
                try {
                    int idProjeto = Integer.parseInt(idStr);
                    sistema.solicitarParticipacao(aluno, idProjeto);
                    JOptionPane.showMessageDialog(null, "Ingressou no projeto com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "ID inválido. Digite apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (RegraNegocioException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 2:
                String historico = aluno.obterHistoricoTexto();
                JOptionPane.showMessageDialog(null, historico, "Meu Histórico", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
        return true;
    }

    private static boolean menuProfessor(Professor prof) {
        String[] opcoesProf = {"Criar Projeto", "Ver Projetos", "Sair"};
        int escolha = mostrarMenuVertical("Painel do Professor", "--- PAINEL DO PROFESSOR ---\nLogado como: " + prof.getNome(), opcoesProf);

        if (escolha == -1 || escolha == 2) return false;

        switch (escolha) {
            case 0:
                criarProjetoInterativo(prof);
                break;
            case 1:
                String projetos = sistema.listarProjetosDisponiveis();
                JOptionPane.showMessageDialog(null, projetos, "Projetos", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
        return true;
    }

    private static boolean menuCoordenador(Coordenador coord) {
        String[] opcoesCoord = {"Estatísticas", "Gerenciar Usuários", "Ver Projetos", "Criar Projeto", "Sair"};
        int escolha = mostrarMenuVertical("Painel do Coordenador", "--- PAINEL DE CONTROLE ---\nAdministração Geral", opcoesCoord);

        if (escolha == -1 || escolha == 4) return false;

        switch (escolha) {
            case 0:
                try {
                    String stats = sistema.imprimirEstatisticasGerais(coord);
                    JOptionPane.showMessageDialog(null, stats, "Estatísticas", JOptionPane.INFORMATION_MESSAGE);
                } catch (AcessoNegadoException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1:
                try {
                    String users = sistema.listarTodosUsuarios(coord);
                    String idStr = JOptionPane.showInputDialog(null, users + "\nDigite o ID do usuário para Ativar/Desativar (ou cancele para sair):", "Gerenciar Usuários", JOptionPane.QUESTION_MESSAGE);
                    if (idStr == null) break;
                    int idUser = Integer.parseInt(idStr);
                    sistema.alterarStatusUsuario(coord, idUser);
                    JOptionPane.showMessageDialog(null, "Status do usuário alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (AcessoNegadoException | RegraNegocioException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 2:
                String projetos = sistema.listarProjetosDisponiveis();
                JOptionPane.showMessageDialog(null, projetos, "Projetos", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 3:
                criarProjetoInterativo(coord);
                break;
        }
        return true;
    }

    private static void criarProjetoInterativo(Usuario solicitante) {
        String titulo = JOptionPane.showInputDialog(null, "Título do Projeto:", "Novo Projeto", JOptionPane.QUESTION_MESSAGE);
        if (titulo == null || titulo.trim().isEmpty()) return;

        String area = JOptionPane.showInputDialog(null, "Área de Estudo:", "Novo Projeto", JOptionPane.QUESTION_MESSAGE);
        if (area == null || area.trim().isEmpty()) return;

        Professor orientador = null;
        if (solicitante instanceof Professor) {
            orientador = (Professor) solicitante;
        } else {
            String nomeProf = JOptionPane.showInputDialog(null, "Nome do Professor Orientador cadastrado:", "Novo Projeto", JOptionPane.QUESTION_MESSAGE);
            if (nomeProf == null) return;
            orientador = sistema.buscarProfessor(nomeProf);
            if (orientador == null) {
                JOptionPane.showMessageDialog(null, "Professor não encontrado. Crie uma conta de professor primeiro.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String vagasStr = JOptionPane.showInputDialog(null, "Número de Vagas:", "Novo Projeto", JOptionPane.QUESTION_MESSAGE);
        if (vagasStr == null) return;

        try {
            int vagas = Integer.parseInt(vagasStr);
            sistema.criarProjeto(solicitante, titulo, area, orientador, vagas);
            JOptionPane.showMessageDialog(null, "Projeto '" + titulo + "' criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número de vagas inválido. Operação cancelada.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AcessoNegadoException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
        }
    }
}