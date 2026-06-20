package sistemaprojetos.model;

public class Professor extends Usuario{
	public Professor(String nome, String email) { 
        super(nome, email); 
    }

    @Override
    public String getTipoUsuario() { return "PROFESSOR"; }
}
