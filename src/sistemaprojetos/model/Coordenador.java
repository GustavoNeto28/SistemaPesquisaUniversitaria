package sistemaprojetos.model;

public class Coordenador extends Usuario{
	public Coordenador(String nome, String email) { 
        super(nome, email); 
    }

    @Override
    public String getTipoUsuario() { return "COORDENADOR"; }
}
