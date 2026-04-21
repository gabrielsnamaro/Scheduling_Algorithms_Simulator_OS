import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Processo {
    private int pid;
    private int instanteDeChegada;
    private int burstTotal;
    private EPrioridade prioridade;
    private LinkedList<Integer> instantesIO;

    private void init(int pid, int instanteDeChegada, int burstTotal, EPrioridade prioridade, LinkedList<Integer> instantesIO) {
        this.pid = pid;
        this.instanteDeChegada = instanteDeChegada;
        this.burstTotal = burstTotal;
        this.prioridade = prioridade;
        this.instantesIO = instantesIO;
    }

    public Processo(int pid, int instanteDeChegada, int burstTotal, EPrioridade prioridade, LinkedList<Integer> instantesIO) {
        init(pid, instanteDeChegada, burstTotal, prioridade, instantesIO);
    }

    public Processo(String linhaDeDados) {
        String[] dados = linhaDeDados.split(";");

        init(
            Integer.parseInt(dados[0]), 
            Integer.parseInt(dados[1]), 
            Integer.parseInt(dados[2]), 
            EPrioridade.doValor(Integer.parseInt(dados[3])), 
            dados.length == 5 ? extrairInstantes(dados[4]) : new LinkedList<>()
        );
    }

    private LinkedList<Integer> extrairInstantes(String linha) {
        String[] instantes = linha.split(",");

        LinkedList<Integer> resultado = new LinkedList<>();
        
        for(int i = 0; i < instantes.length; i++) {
            resultado.add(Integer.parseInt(instantes[i]));
        }

        return resultado;
    }

    private String instantesToString() {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < instantesIO.size(); i++) {
            String pontuacao = i == instantesIO.size() - 1 
                ? ". "
                : ", ";  

            builder.append(instantesIO.get(i) + pontuacao);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format(
            "PID: %02d | Chegada: %d | Burst Total: %d | Prioridade: %s | Instantes: %s", pid, instanteDeChegada, burstTotal, prioridade.name(), instantesToString()
        );
    }


}
