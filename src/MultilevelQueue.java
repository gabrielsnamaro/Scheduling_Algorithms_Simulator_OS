import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class MultilevelQueue extends Escalonador {
    private class Execucao {
        private int instanteInicial;
        private int instanteFinal;
        private Processo processo;
        private String filaAlta;
        private String filaBaixa;
        private Boolean houveIO;
        private Boolean terminou;
        private Boolean cpuOciosa;
        private Boolean quantumTerminou;

        public Execucao() {
            houveIO = false;
            terminou = false;
            cpuOciosa = false;
            quantumTerminou = false;
        }

        public void setInstanteInicial(int instanteInicial) {
            this.instanteInicial = instanteInicial;
        }

        public void setInstanteFinal(int instanteFinal) {
            this.instanteFinal = instanteFinal;
        }

        public void setProcesso(Processo processo) {
            this.processo = processo;
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

        public void reportarIO() {
            houveIO = true;
        }

        public void reportarFinalizado() {
            terminou = true;
        }

        public void reportarOcio() {
            cpuOciosa = true;
        }

        public void reportarQuantum() {
            quantumTerminou = true;
        }

        public void imprimir() {
            System.out.println(registro());
        }

        public String registro() {
            String registro = "********** " + instanteInicial + "s até " + instanteFinal + "s **********\n";
            
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


    Scanner teclado;
    Queue<Processo> filaAlta;
    Queue<Processo> filaBaixa;
    Queue<Processo> espera;    
    static final int QUANTUM = 5;

    public MultilevelQueue(LinkedList<Processo> processos) {
        super(processos);
        teclado = new Scanner(System.in);
    }

    @Override
    public void escalonar() {
        Queue<Processo> todos = processosOrdenados();
        filaAlta = new LinkedList<>();
        filaBaixa = new LinkedList<>();
        espera = new LinkedList<>();

        int instanteAtual = 0;

        while((todos.size() + espera.size() + filaAlta.size() + filaBaixa.size()) > 0) {
            System.out.println(String.format("%d - %d - %d - %d", todos.size(), espera.size(), filaAlta.size(), filaBaixa.size()));

            Execucao execucao = new Execucao();

            adicionarProcessosChegando(todos, filaAlta, EPrioridade.ALTA, instanteAtual);
            adicionarProcessosChegando(todos, filaBaixa, EPrioridade.BAIXA, instanteAtual);

            execucao.setFilaAlta(filaAlta);
            execucao.setFilaBaixa(filaBaixa);

            adicionarDaEspera(filaAlta, EPrioridade.ALTA, instanteAtual);
            adicionarDaEspera(filaBaixa, EPrioridade.BAIXA, instanteAtual);

            if(!filaAlta.isEmpty())
                instanteAtual = executarFilaAlta(instanteAtual, execucao);
            else if(!filaBaixa.isEmpty())
                instanteAtual = executarFilaBaixa(instanteAtual, execucao);
            else
                execucao.reportarOcio();

            esperar();
            execucao.imprimir();
            Escritor.registrar(execucao.registro());
        }

        teclado.close();

    }

    private int executarFilaAlta(int instante, Execucao execucao) {
        Processo atual = filaAlta.poll();
        int novoInstante;

        execucao.setInstanteInicial(instante);
        execucao.setProcesso(atual);

        try {
            atual.avancar(QUANTUM, instante);
            novoInstante = instante + QUANTUM;
            filaAlta.add(atual);
            execucao.reportarQuantum();
        } catch(InterrupcaoIO e) {
            novoInstante = e.getNovoInstante();
            espera.add(atual);
            execucao.reportarIO();
        } catch(InterrupcaoEncerramento e) {
            novoInstante = e.getNovoInstante();
            execucao.reportarFinalizado();
        } 

        execucao.setInstanteFinal(novoInstante);

        return novoInstante;
    }

    private int executarFilaBaixa(int instante, Execucao execucao) {
        Processo atual = filaBaixa.poll();
        int novoInstante;

        execucao.setProcesso(atual);
        execucao.setInstanteInicial(instante);

        try {
            atual.avancar(1, instante);
            novoInstante = instante + 1;
        } catch(InterrupcaoIO e) {
            novoInstante = e.getNovoInstante();
            espera.add(atual);
            execucao.reportarIO();
        } catch(InterrupcaoEncerramento e) {
            novoInstante = e.getNovoInstante();
            execucao.reportarFinalizado();
        } 

        execucao.setInstanteFinal(novoInstante);

        return novoInstante;
    }

    private void adicionarDaEspera(Queue<Processo> fila, EPrioridade prioridade, int instante) {
        LinkedList<Processo> naPrioridade = porPrioridade(prioridade, espera);

        while(!espera.isEmpty() 
            && espera.element().proximoRetornoDeIO() != -1
            && espera.element().proximoRetornoDeIO() <= instante)
        fila.add(espera.poll());
    }

    private void adicionarProcessosChegando(Queue<Processo> todos, Queue<Processo> fila, EPrioridade prioridade, int instante) {
        LinkedList<Processo> naPrioridade = porPrioridade(prioridade, todos);

        while(!todos.isEmpty() && todos.element().getInstanteChegada() >= instante) {
            fila.add(todos.poll());
        }
    }

    private LinkedList<Processo> porPrioridade(EPrioridade prioridade, Queue<Processo> processos) {
        LinkedList<Processo> resultado = new LinkedList<>();

       

        for(Processo processo : processos) {
            if(processo.getPrioridade().equals(prioridade))
                resultado.add(processo);
        }

        return resultado;
    }

    private void esperar() {
        teclado.nextLine();
    }
}
