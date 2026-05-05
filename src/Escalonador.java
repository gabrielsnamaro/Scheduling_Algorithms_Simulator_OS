import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class Escalonador {
    protected LinkedList<Processo> processos;
    protected Map<Integer, Processo> espera;
    protected Map<Integer, Processo> retorno;
    protected int vazao;


    public Escalonador(LinkedList<Processo> processos) {
        this.processos = processos;
        espera = new HashMap<>(processos.size());
        retorno = new HashMap<>(processos.size());
    }

    protected Queue<Processo> processosOrdenados() {
        LinkedList<Processo> resultado = new LinkedList<>(this.processos);
        resultado.sort((p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
    
        return resultado;
    }

    public abstract void escalonar();

}