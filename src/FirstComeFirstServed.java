import java.util.LinkedList;
import java.util.Queue;

public class FirstComeFirstServed extends Escalonador {
    private class Execucao {
        private int instanteInicial;
        private int instanteFinal;
        private Processo processo;
        private String filaDePronto;
        private Boolean houveIO;
        private Boolean terminou;
        private Boolean cpuOciosa;

        public Execucao() {
            houveIO = false;
            terminou = false;
            cpuOciosa = false;
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

        public void setFilaDePronto(Queue<Processo> fila) {
            StringBuilder builder = new StringBuilder();

            LinkedList<Processo> lista = (LinkedList<Processo>) fila;

            for(int i = 0; i < lista.size(); i++) {
                String pontuacao = i == lista.size() - 1
                    ? ". "
                    : ", ";
                
                builder.append(lista.get(i).getPid() + pontuacao);
            }

            this.filaDePronto = builder.toString();
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

        public void imprimir() {
            System.out.println(registro());
        }

        public String registro() {
            String registro = "********** " + instanteInicial + "s até " + instanteFinal + "s **********\n";
            
            if(!cpuOciosa) {
                registro += "* Processo executado: " + processo.estadoAtual() + "\n* Fila de pronto: " + filaDePronto + "\n";

                if(houveIO)
                    registro += "* Fez IO!";

                if(terminou)
                    registro += "* Processo terminou!";
            } else {
                registro += "* CPU ociosa...";
            }

            registro += "\n\n";

            return registro;
        }
    }


    public FirstComeFirstServed(LinkedList<Processo> processos) {
        super(processos);
    }

    @Override
    public void escalonar() {
        Queue<Processo> proximosProcessos = processosOrdenados();
        Queue<Processo> processosProntos = new LinkedList<>();
        Queue<Processo> processosEmEspera = new LinkedList<>();

        int instanteAtual = 0;
        adicionarProcessosEmChegada(proximosProcessos, processosProntos, instanteAtual);

        while((proximosProcessos.size() + processosEmEspera.size() + processosProntos.size()) > 0) {
            Execucao execucaoAtual = new Execucao();

            execucaoAtual.setInstanteInicial(instanteAtual);

            if(!processosProntos.isEmpty()) {
                Processo atual = processosProntos.poll();
                int tempoAvanco = atual.getBurstRestante();

                execucaoAtual.setFilaDePronto(processosProntos);
                execucaoAtual.setProcesso(atual);

                try {
                    instanteAtual = atual.avancar(tempoAvanco, instanteAtual);
                } catch (InterrupcaoIO e) {
                    instanteAtual = e.getNovoInstante();
                    processosEmEspera.add(atual);
                    execucaoAtual.reportarIO();
                } catch (InterrupcaoEncerramento e) {
                    instanteAtual = e.getNovoInstante();
                    execucaoAtual.reportarFinalizado();
                }
            } else {
                execucaoAtual.reportarOcio();

                instanteAtual += Math.min(
                    processosEmEspera.isEmpty()
                        ? Integer.MAX_VALUE
                        : processosEmEspera.element().proximoRetornoDeIO(),
                    proximosProcessos.isEmpty()
                        ? Integer.MAX_VALUE
                        : proximosProcessos.element().getInstanteChegada()
                ) - instanteAtual;
            }

            execucaoAtual.setInstanteFinal(instanteAtual);

            execucaoAtual.imprimir();
            Escritor.registrar(execucaoAtual.registro());

            adicionarProcessosEmChegada(proximosProcessos, processosProntos, instanteAtual);
            adicionarProcessosDaEspera(processosEmEspera, processosProntos, instanteAtual);
        }
    }
    
    private void adicionarProcessosEmChegada(Queue<Processo> proximosProcessos, Queue<Processo> filaDeExecucao, int instanteAtual) {
        while(!proximosProcessos.isEmpty() && proximosProcessos.element().getInstanteChegada() <= instanteAtual)
            filaDeExecucao.add(proximosProcessos.poll());
    }

    private void adicionarProcessosDaEspera(Queue<Processo> filaDeEspera, Queue<Processo> filaDeExecucao, int instanteAtual) {

        while(!filaDeEspera.isEmpty() 
            && filaDeEspera.element().proximoRetornoDeIO() != -1
            && filaDeEspera.element().proximoRetornoDeIO() <= instanteAtual)
            filaDeExecucao.add(filaDeEspera.poll());
    }

    private static void printList(Queue<Processo> lista) {
        for(Processo p : lista) {
            System.out.print(p.getPid() + " | ");
        }

        System.out.println();
    }

    /* APAGAR
    public static void esperarEnter() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Pressione Enter para continuar...");
        scanner.nextLine();  // Aguarda o Enter
        // Não precisa fechar o scanner se ele for usado novamente depois
    }*/
}
