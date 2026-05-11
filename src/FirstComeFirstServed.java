import java.util.LinkedList;
import java.util.Queue;

public class FirstComeFirstServed extends Escalonador {

    public FirstComeFirstServed(LinkedList<Processo> processos) {
        super(processos);
    }

    @Override
    public void escalonar() {
        Queue<Processo> proximosProcessos = organizarProximosProcessos();

        Queue<Processo> processosProntos = new LinkedList<>();
        Queue<Processo> processosEmEspera = new LinkedList<>();

        int instanteAtual = 0;
        adicionarProcessosEmChegada(proximosProcessos, processosProntos, instanteAtual);

        while((proximosProcessos.size() + processosEmEspera.size() + processosProntos.size()) > 0) {
            RegistroExecucao execucaoAtual = new RegistroExecucao();
            execucaoAtual.setFilaDePronto(processosProntos);
            execucaoAtual.setInstanteInicial(instanteAtual);
            
            if(!processosProntos.isEmpty()) {
                Processo atual = processosProntos.poll();
    
                MetricaIndividual metrica = metricaGeral.gerarMetrica(atual, instanteAtual);

                execucaoAtual.setProcesso(atual);

                int tempoAvanco = atual.getBurstRestante();

                try {
                    instanteAtual = atual.avancar(tempoAvanco, instanteAtual);
                } catch (InterrupcaoIO e) {
                    instanteAtual = e.getNovoInstante();
                    processosEmEspera.add(atual);

                    metrica.adicionarTempoEmIO(Processo.TEMPO_BLOQUEIO_IO);
                    execucaoAtual.reportarIO();
                } catch (InterrupcaoEncerramento e) {
                    instanteAtual = e.getNovoInstante();

                    metrica.setInstanteDeTermino(instanteAtual);
                    execucaoAtual.reportarFinalizado();
                }
            } else {
                instanteAtual += Math.min(
                    processosEmEspera.isEmpty()
                        ? Integer.MAX_VALUE
                        : processosEmEspera.element().proximoRetornoDeIO(),
                    proximosProcessos.isEmpty()
                        ? Integer.MAX_VALUE
                        : proximosProcessos.element().getInstanteChegada()
                ) - instanteAtual;

                execucaoAtual.reportarOcio();
            }

            execucaoAtual.setInstanteFinal(instanteAtual);
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
}
