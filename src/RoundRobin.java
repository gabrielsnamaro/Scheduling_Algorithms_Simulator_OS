import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobin extends Escalonador {
    private static class ExecucaoRR extends RegistroExecucao {

        private int quantumPrevisto;
        private Boolean quantumTerminou;

        public ExecucaoRR() {
            super();
            quantumTerminou = false;
        }

        public void reportarQuantum() {
            quantumTerminou = true;
        }

        public String registro() {
            String registro = "********** " + instanteInicial + "ms até " + instanteFinal + "ms **********\n";
            
            if(!cpuOciosa) {
                registro += "* Fila de pronto: " + filaDePronto + "\n* Processo executado: " + processo.estadoAtual() + "\n* Surto (quantum) previsto: " + quantumPrevisto + "ms\n";

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

        public void setQuantumPrevisto(int quantum) {
            quantumPrevisto = quantum;
        }
    }
    
    private static final double PESO = 0.5d; 
    private static final int PREDICAO_INICIAL = 10;
    private int previsaoAnterior;
    private int surtoAnterior;

    public RoundRobin(LinkedList<Processo> processos) {
        super(processos);
        surtoAnterior = previsaoAnterior = PREDICAO_INICIAL;
    }

    @Override
    public void escalonar() {
        Queue<Processo> proximos = organizarProximosProcessos();
        Queue<Processo> prontos = new LinkedList<>();

        int instanteAtual = 0;

        while((proximos.size() + prontos.size() + espera.size()) > 0) {
            ExecucaoRR execucao = new ExecucaoRR();

            int quantum = previsaoAnterior = proximoSurtoPrevisto();
            execucao.setQuantumPrevisto(quantum);
            int instanteInicial = instanteAtual;

            adicionarEmChegada(prontos, proximos, instanteAtual);
            adicionarDaEspera(prontos, instanteAtual);

            execucao.setInstanteInicial(instanteAtual);
            execucao.setFilaDePronto(prontos);

            if(!prontos.isEmpty()) {
                Processo atual = prontos.poll();

                MetricaIndividual metrica = metricaGeral.gerarMetrica(atual, instanteAtual);

                try {
                    execucao.setProcesso(atual);
                    instanteAtual = atual.avancar(quantum, instanteAtual);
                    prontos.add(atual);

                    execucao.reportarQuantum();
                } catch(InterrupcaoIO e) {
                    instanteAtual = e.getNovoInstante();
                    espera.add(atual);

                    metrica.adicionarTempoEmIO(Processo.TEMPO_BLOQUEIO_IO);
                    execucao.reportarIO();
                } catch(InterrupcaoEncerramento e) {
                    instanteAtual = e.getNovoInstante();
                
                    metrica.setInstanteDeTermino(instanteAtual);
                    execucao.reportarFinalizado();
                }
            } else {
                execucao.reportarOcio();
                instanteAtual++;
            }

            surtoAnterior = instanteAtual - instanteInicial;

            execucao.setInstanteFinal(instanteAtual);

            Escritor.registrar(execucao.registro());
        }
    }

    private int proximoSurtoPrevisto() {
        return (int) Math.round(PESO * surtoAnterior + (1 - PESO) * previsaoAnterior);
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
