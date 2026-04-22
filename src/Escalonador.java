import java.util.LinkedList;

public abstract interface Escalonador {
    public Escalonamento escalonar(LinkedList<Processo> processos);
}