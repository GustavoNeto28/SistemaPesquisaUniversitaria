package sistemaprojetos.model;

public abstract class Usuario implements Imprimivel {
    protected static int contadorId = 1; 
    protected int id;
    protected String nome;
    protected String email;
    protected String senha; // Novo atributo
    protected boolean ativo;

    public Usuario(String nome, String email, String senha) {
        this.id = contadorId++;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    // Método para validar o login
    public boolean validarSenha(String senhaDigitada) {
        return this.senha.equals(senhaDigitada);
    }

    public abstract String getTipoUsuario();

    @Override
    public void exibirDetalhes() {
        System.out.println("[" + getTipoUsuario() + "] ID: " + id + " | " + nome + " (" + email + ") - Ativo: " + (ativo ? "Sim" : "Não"));
    }
}