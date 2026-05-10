import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobin extends Escalonador {
    private static class ExecucaoRR extends Execucao {
        private Boolean quantumTerminou;

        public ExecucaoRR() {
            super();
            quantumTerminou = false;
        }

        public void reportarQuantum() {
            quantumTerminou = true;
        }

        public String registro() {
            String registro = "********** " + instanteInicial + "s até " + instanteFinal + "s **********\n";
            
            if(!cpuOciosa) {
                registro += "* Fila de pronto: " + filaDePronto + "\n* Processo executado: " + processo.estadoAtual() + "\n";

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

    public RoundRobin(LinkedList<Processo> processos) {
        super(processos);
    }

    @Override
    public void escalonar() {
        Queue<Processo> proximos = organizarProximosProcessos();
        Queue<Processo> prontos = new LinkedList<>();

        int instanteAtual = 0;

        while((proximos.size() + prontos.size() + espera.size()) > 0) {
            ExecucaoRR execucao = new ExecucaoRR();

            int quantum = 2;
            adicionarEmChegada(prontos, proximos, instanteAtual);
            adicionarDaEspera(prontos, instanteAtual);

            Processo atual = null;

            execucao.setInstanteInicial(instanteAtual);
            execucao.setFilaDePronto(prontos);

            System.out.println();
            if(!prontos.isEmpty()) {
                try {
                    atual = prontos.poll();
                    execucao.setProcesso(atual);
                    instanteAtual = atual.avancar(quantum, instanteAtual);
                    prontos.add(atual);
                    execucao.reportarQuantum();
                } catch(InterrupcaoIO e) {
                    instanteAtual = e.getNovoInstante();
                    espera.add(atual);
                    execucao.reportarIO();
                } catch(InterrupcaoEncerramento e) {
                    instanteAtual = e.getNovoInstante();
                    execucao.reportarFinalizado();
                }
            } else {
                execucao.reportarOcio();
                instanteAtual++;
            }

            execucao.setInstanteFinal(instanteAtual);

            execucao.imprimir();
            Escritor.registrar(execucao.registro());
        }
    }

    private void adicionarEmChegada(Queue<Processo> prontos, Queue<Processo> proximos, int instante) {
        while (!proximos.isEmpty() && proximos.element().getInstanteChegada() <= instante) {
            prontos.add(proximos.poll());
        }
    }

    private void adicionarDaEspera(Queue<Processo> prontos, int instante) {
        while(!espera.isEmpty() && espera.element().proximoRetornoDeIO() <= instante) {
            prontos.add(espera.poll());
        }
    }

    private static void printList(List<Processo> lista) {
        for(Processo p : lista) {
            System.out.println(p);
        }
    }
    
}
