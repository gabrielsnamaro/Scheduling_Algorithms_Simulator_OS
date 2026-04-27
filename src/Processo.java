import java.util.LinkedList;

public class Processo {
    public static final int TEMPO_BLOQUEIO_IO = 5;

    private int pid;
    private int instanteDeChegada;
    private int burstTotal;
    private int burstRestante;
    private int quandoVoltaDeIO;
    private EPrioridade prioridade;
    private LinkedList<Integer> instantesIO;

    private void init(int pid, int instanteDeChegada, int burstTotal, EPrioridade prioridade, LinkedList<Integer> instantesIO) {
        LinkedList<Integer> instantesOrdenados = new LinkedList<>(instantesIO);
        instantesOrdenados.sort((i1, i2) -> Integer.compare(i1, i2));
        
        this.pid = pid;
        this.instanteDeChegada = instanteDeChegada;
        this.burstTotal = burstTotal;
        this.prioridade = prioridade;
        this.instantesIO = instantesOrdenados;

        this.quandoVoltaDeIO = -1;
        this.burstRestante = this.burstTotal;
        this.quandoVoltaDeIO = -1;
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

    public void decrementarBurst(int qtd, int instanteAtual) throws InterrupcaoEncerramento {
        if (burstRestante - qtd > 0) {
            burstRestante -= qtd;
        } else {
            int novoInstante = instanteAtual + burstRestante;
            burstRestante = 0;
            throw new InterrupcaoEncerramento("Processo " + pid + " encerrado. ", novoInstante);
        }
    }

    public void decrementarIO() {
        for (int i = 0; i < instantesIO.size(); i++) {
            if (instantesIO.get(i) > 0) {
                instantesIO.set(i, instantesIO.get(i) - 1);
            }
        }
    }

    public int avancar(int segundos, int instanteAtual) throws InterrupcaoIO, InterrupcaoEncerramento {
        quandoVoltaDeIO = -1;
        int proxIO = proximoInstanteDeIO();

        int avancoReal = segundos;

        if(proxIO != -1 && instanteAtual + segundos >= proxIO) {
            avancoReal = proxIO - (burstTotal - burstRestante);
            quandoVoltaDeIO = instanteAtual + avancoReal + TEMPO_BLOQUEIO_IO;
            decrementarBurst(avancoReal, instanteAtual);
            throw new InterrupcaoIO("Processo " + pid + " fez IO! ", instanteAtual + avancoReal);
        } else {
            decrementarBurst(avancoReal, instanteAtual);
            avancoReal = segundos;
        }

        return instanteAtual + avancoReal;
    }

    private int proximoInstanteDeIO() {
        int resultado = -1;

        int i = instantesIO.size() - 1;
        while(i >= 0 && instantesIO.get(i) > (burstTotal - burstRestante)) {
            resultado = instantesIO.get(i);
            i--;
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

    public String estadoAtual() {
        return String.format(
            "PID: %02d | Chegada: %d | Burst Restante: %d", pid, instanteDeChegada, burstRestante
        );  
    }

    public int proximoRetornoDeIO() {
        if(burstRestante == 0) return -1;
        return quandoVoltaDeIO;
    }

    public int getBurstRestante() {
        return burstRestante;
    }

    public int getPid() {
        return pid;
    }

    public int getInstanteChegada() {
        return instanteDeChegada;
    }




}
