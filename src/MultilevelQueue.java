import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class MultilevelQueue extends Escalonador {
    private static class ExecucaoMLQ extends Execucao {
        private String filaAlta;
        private String filaBaixa;
        private Boolean quantumTerminou;

        public ExecucaoMLQ() {
            super();
            quantumTerminou = false;
        }

        public void setFilaAlta(Queue<Processo> fila) {
            StringBuilder builder = new StringBuilder();

            LinkedList<Processo> lista = (LinkedList<Processo>) fila;

            for(int i = 0; i < lista.size(); i++) {
                String pontuacao = i == lista.size() - 1
                    ? ". "
                    : ", ";
                
                builder.append(lista.get(i).getPid() + pontuacao);
            }

            this.filaAlta = builder.toString();
        }

        public void setFilaBaixa(Queue<Processo> fila) {
            StringBuilder builder = new StringBuilder();

            LinkedList<Processo> lista = (LinkedList<Processo>) fila;

            for(int i = 0; i < lista.size(); i++) {
                String pontuacao = i == lista.size() - 1
                    ? ". "
                    : ", ";
                
                builder.append(lista.get(i).getPid() + pontuacao);
            }

            this.filaBaixa = builder.toString();
        }

        public void reportarQuantum() {
            quantumTerminou = true;
        }

        public String registro() {
            String registro = "********** " + instanteInicial + "ms até " + instanteFinal + "ms **********\n";
            
            if(!cpuOciosa) {
                registro += "* Processo executado: " + processo.estadoAtual() + "\n* Fila alta: " + filaAlta + "\n* Fila baixa: " + filaBaixa + "\n";

                if(houveIO)
                    registro += "* Fez IO!";

                if(terminou)
                    registro += "* Processo terminou!";

                if(quantumTerminou)
                    registro += "* Quantum terminou!";
            } else {
                registro += "* CPU ociosa...";
            }

            registro += "\n\n";

            return registro;
        }
    }

    Queue<Processo> filaAlta;
    Queue<Processo> filaBaixa;
    private static final int QUANTUM = 5;

    public MultilevelQueue(LinkedList<Processo> processos) {
        super(processos);
        filaAlta = new LinkedList<>();
        filaBaixa = new LinkedList<>();
    }

    @Override
    public void escalonar() {
        Queue<Processo> todos = organizarProximosProcessos();

        int instanteAtual = 0;

        while((todos.size() + espera.size() + filaAlta.size() + filaBaixa.size()) > 0) {
            ExecucaoMLQ execucao = new ExecucaoMLQ();

            adicionarProcessosChegando(todos, instanteAtual);

            adicionarDaEspera(instanteAtual);

            if(!filaAlta.isEmpty())
                instanteAtual = executarFilaAlta(instanteAtual, execucao);
            else if(!filaBaixa.isEmpty())
                instanteAtual = executarFilaBaixa(instanteAtual, execucao);
            else {
                instanteAtual++;
            }
        }

    }

    private int executarFilaAlta(int instante, ExecucaoMLQ execucao) {
        Processo atual = filaAlta.poll();
        int novoInstante;

        try {
            atual.avancar(QUANTUM, instante);
            novoInstante = instante + QUANTUM;
            filaAlta.add(atual);
        } catch(InterrupcaoIO e) {
            novoInstante = e.getNovoInstante();
            espera.add(atual);
        } catch(InterrupcaoEncerramento e) {
            novoInstante = e.getNovoInstante();
        }

        return novoInstante;
    }

    private int executarFilaBaixa(int instante, ExecucaoMLQ execucao) {
        Processo atual = filaBaixa.element();
        int novoInstante;

        try {
            atual.avancar(1, instante);
            novoInstante = instante + 1;
        } catch(InterrupcaoIO e) {
            novoInstante = e.getNovoInstante();
            espera.add(filaBaixa.poll());
        } catch(InterrupcaoEncerramento e) {
            novoInstante = e.getNovoInstante();
            filaBaixa.poll();
        }

        return novoInstante;
    }

    private void adicionarDaEspera(int instante) {
        Queue<Processo>[] filas = extrairParaFilas(espera);
        Queue<Processo> naPrioridadeAlta = filas[0];
        Queue<Processo> naPrioridadeBaixa = filas[1];

        while(!naPrioridadeAlta.isEmpty() 
            && naPrioridadeAlta.element().proximoRetornoDeIO() != -1
            && naPrioridadeAlta.element().proximoRetornoDeIO() <= instante)
        filaAlta.add(naPrioridadeAlta.poll());

        while(!naPrioridadeBaixa.isEmpty() 
            && naPrioridadeBaixa.element().proximoRetornoDeIO() != -1
            && naPrioridadeBaixa.element().proximoRetornoDeIO() <= instante)
        filaBaixa.add(naPrioridadeBaixa.poll());

        restaurarDasFilas(espera, filas, (p1, p2) -> Integer.compare(p1.proximoRetornoDeIO(), p2.proximoRetornoDeIO()));
    }

    private void adicionarProcessosChegando(Queue<Processo> todos, int instante) {
        Queue<Processo>[] filas = extrairParaFilas(todos);
        Queue<Processo> naPrioridadeAlta = filas[0];
        Queue<Processo> naPrioridadeBaixa = filas[1];

        while(!naPrioridadeAlta.isEmpty() && naPrioridadeAlta.element().getInstanteChegada() <= instante) {
            filaAlta.add(naPrioridadeAlta.poll());
        }

        while(!naPrioridadeBaixa.isEmpty() && naPrioridadeBaixa.element().getInstanteChegada() <= instante) {
            filaBaixa.add(naPrioridadeBaixa.poll());
        }

        restaurarDasFilas(todos, filas, (p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
    }

    private void restaurarDasFilas(Queue<Processo> todos, Queue<Processo>[] filas, Comparator<Processo> comparador) {
        while(!filas[0].isEmpty())
            todos.add(filas[0].poll());

        while(!filas[1].isEmpty())
            todos.add(filas[1].poll());

        LinkedList<Processo> lista = new LinkedList<>(todos);
        ordenar(lista, comparador);
        transferirListaParaFila(todos, lista);
    }

    private Queue<Processo>[] extrairParaFilas(Queue<Processo> processos) {
        Queue<Processo>[] filas = (Queue<Processo>[]) new Queue[2];
        filas[0] = new LinkedList<>();
        filas[1] = new LinkedList<>();

        while(!processos.isEmpty()) {
            if(processos.element().getPrioridade().equals(EPrioridade.ALTA))
                filas[0].add(processos.poll());
            else if(processos.element().getPrioridade().equals(EPrioridade.BAIXA))
                filas[1].add(processos.poll());
        }

        return filas;
    }
}
