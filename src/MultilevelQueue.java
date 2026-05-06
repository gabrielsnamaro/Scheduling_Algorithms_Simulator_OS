import java.util.LinkedList;
import java.util.List;
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
        LinkedList<Processo> listaTodos = new LinkedList<>(processos);
        ordenar(listaTodos, (p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
        Queue<Processo> todos = listaTodos;
        filaAlta = new LinkedList<>();
        filaBaixa = new LinkedList<>();
        espera = new LinkedList<>();

        int instanteAtual = 0;

        while((todos.size() + espera.size() + filaAlta.size() + filaBaixa.size()) > 0) {
            System.out.println(String.format("%d - %d - %d - %d", todos.size(), espera.size(), filaAlta.size(), filaBaixa.size()));

            Execucao execucao = new Execucao();

            adicionarProcessosChegando(todos, instanteAtual);

            execucao.setFilaAlta(filaAlta);
            execucao.setFilaBaixa(filaBaixa);

            adicionarDaEspera(instanteAtual);
            adicionarDaEspera(instanteAtual);

            if(!filaAlta.isEmpty())
                instanteAtual = executarFilaAlta(instanteAtual, execucao);
            else if(!filaBaixa.isEmpty())
                instanteAtual = executarFilaBaixa(instanteAtual, execucao);
            else {
                execucao.setInstanteInicial(instanteAtual);
                execucao.reportarOcio();
                instanteAtual++;
                execucao.setInstanteFinal(instanteAtual);
            }

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
        Processo atual = filaBaixa.element();
        int novoInstante;

        execucao.setProcesso(atual);
        execucao.setInstanteInicial(instante);

        try {
            atual.avancar(1, instante);
            novoInstante = instante + 1;
        } catch(InterrupcaoIO e) {
            novoInstante = e.getNovoInstante();
            espera.add(filaBaixa.poll());
            execucao.reportarIO();
        } catch(InterrupcaoEncerramento e) {
            novoInstante = e.getNovoInstante();
            execucao.reportarFinalizado();
            filaBaixa.poll();
        } 

        execucao.setInstanteFinal(novoInstante);

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

        restaurar(espera, filas);
        LinkedList<Processo> lista = new LinkedList<>(espera);
        ordenar(lista, (p1, p2) -> Integer.compare(p1.proximoRetornoDeIO(), p2.proximoRetornoDeIO()));
        transferirListaParaFila(espera, lista);
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

        restaurar(todos, filas);
        LinkedList<Processo> lista = new LinkedList<>(todos);
        ordenar(lista, (p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
        transferirListaParaFila(todos, lista);
    }

    private void restaurar(Queue<Processo> todos, Queue<Processo>[] filas) {
        while(!filas[0].isEmpty())
            todos.add(filas[0].poll());

        while(!filas[1].isEmpty())
            todos.add(filas[1].poll());
    }

    private void transferirListaParaFila(Queue<Processo> fila, List<Processo> lista) {
        for(int i = 0; i < lista.size(); i++) {
            fila.add(lista.get(i));
        }
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

    private void esperar() {
        teclado.nextLine();
    }
}
