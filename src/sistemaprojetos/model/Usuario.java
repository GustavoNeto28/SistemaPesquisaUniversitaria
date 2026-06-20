package sistemaprojetos.model;

public abstract class Usuario implements Imprimivel{
protected static int contadorId = 1; 
    
    protected int id;
    protected String nome;
    protected String email;
    protected boolean ativo;

    public Usuario(String nome, String email) {
        this.id = contadorId++;
        this.nome = nome;
        this.email = email;
        this.ativo = true;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public abstract String getTipoUsuario();

    @Override
    public void exibirDetalhes() {
        System.out.println("[" + getTipoUsuario() + "] " + nome + " (" + email + ") - Ativo: " + ativo);
    }
}
