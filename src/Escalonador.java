import java.util.LinkedList;

public abstract class Escalonador {
    private int tempoDeEsperaMedio; 
    private int tempoDeRetorno;
    private int vazao;
    private LinkedList<Processo> processos;

    public Escalonador(LinkedList<Processo> processos) {
        this.processos = processos;

        calcular();
    }

    protected abstract void calcular();

    public int[] getDados() {
        return new int[]{tempoDeEsperaMedio, tempoDeRetorno, vazao};
    }
}