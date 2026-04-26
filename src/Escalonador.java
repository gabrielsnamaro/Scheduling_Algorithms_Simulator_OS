import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class Escalonador {
    protected LinkedList<Processo> processos;
    protected Map<Integer, Processo> espera;
    protected Map<Integer, Processo> retorno;
    protected Queue<Processo> processosEmEspera;
    protected int vazao;


    public Escalonador(LinkedList<Processo> processos) {
        this.processos = processos;
        espera = new HashMap<>(processos.size());
        retorno = new HashMap<>(processos.size());
        processosEmEspera = new LinkedList<>();
    }

    public abstract void escalonar();

}