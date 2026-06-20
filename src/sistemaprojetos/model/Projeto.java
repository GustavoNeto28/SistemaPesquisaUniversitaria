package sistemaprojetos.model;

import java.util.ArrayList;
import java.util.List;
import sistemaprojetos.exception.RegraNegocioException;


public class Projeto implements Imprimivel{
	public static final String STATUS_ABERTO = "ABERTO";
    public static final String STATUS_ANDAMENTO = "EM ANDAMENTO";
    public static final String STATUS_CONCLUIDO = "CONCLUIDO";
    
    private static int contadorId = 1;
    private int id;
    private String titulo;
    private String areaEstudo;
    private Professor orientador;
    private int vagas;
    private String status;
    private List<Aluno> participantes;
    
    public Projeto(String titulo, String areaEstudo, Professor orientador, int vagas) {
        this.id = contadorId++;
        this.titulo = titulo;
        this.areaEstudo = areaEstudo;
        this.orientador = orientador;
        this.vagas = vagas;
        this.status = STATUS_ABERTO;
        this.participantes = new ArrayList<>();
    }
    
    public int getId() { return id; }
    public String getStatus() { return status; }
    public String getAreaEstudo() { return areaEstudo; }
    public Professor getOrientador() { return orientador; }
    
    public void adicionarParticipante(Aluno aluno) throws RegraNegocioException {
        if (participantes.size() >= vagas) {
            throw new RegraNegocioException("Vagas esgotadas para o projeto: " + titulo);
        }
        participantes.add(aluno);
        aluno.adicionarAoHistorico(this);
    }
    
    @Override
    public void exibirDetalhes() {
        System.out.println("Projeto #" + id + " - " + titulo + " | Área: " + areaEstudo + 
                           " | Orientador: " + orientador.getNome() + 
                           " | Vagas: " + participantes.size() + "/" + vagas + 
                           " | Status: " + status);
    }
}
