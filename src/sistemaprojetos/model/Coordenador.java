package sistemaprojetos.model;

public class Coordenador extends Usuario {
    public Coordenador(String nome, String email, String senha) { 
        super(nome, email, senha); 
    }

    @Override
    public String getTipoUsuario() { return "COORDENADOR"; }
}