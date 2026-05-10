import java.util.LinkedList;
import java.util.Queue;

public class ShortestRemainingTimeFirst extends Escalonador {

    public ShortestRemainingTimeFirst(LinkedList<Processo> processos) {
        super(processos);
    }

    @Override
    public void escalonar() {
        LinkedList<Processo> listaProximos = new LinkedList<>(processos);
        ordenar(listaProximos, (p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));

        Queue<Processo> proximosProcessos = listaProximos;

        LinkedList<Processo> processosProntos = new LinkedList<>();
        Queue<Processo> processosEmEspera = new LinkedList<>();

        int instanteAtual = 0;

        adicionarProcessosEmChegada(proximosProcessos, processosProntos, instanteAtual);

        while ((proximosProcessos.size() + processosProntos.size() + processosEmEspera.size()) > 0) {
            Execucao execucaoAtual = new Execucao();
            execucaoAtual.setInstanteInicial(instanteAtual);

            adicionarProcessosEmChegada(proximosProcessos, processosProntos, instanteAtual);
            adicionarProcessosDaEspera(processosEmEspera, processosProntos, instanteAtual);

            ordenarFilaPorBurst(processosProntos);

            if (!processosProntos.isEmpty()) {
                Processo atual = processosProntos.poll();
                execucaoAtual.setProcesso(atual);
                execucaoAtual.setFilaDePronto(new LinkedList<>(processosProntos));

                int tempoExecucao = 1;

                try {
                    instanteAtual = atual.avancar(
                        tempoExecucao,
                        instanteAtual
                    );

                    if (atual.getBurstRestante() > 0) {
                        processosProntos.add(atual);
                    }
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
                        : processosEmEspera.element()
                            .proximoRetornoDeIO(),

                    proximosProcessos.isEmpty()
                        ? Integer.MAX_VALUE
                        : proximosProcessos.element()
                            .getInstanteChegada()

                ) - instanteAtual;
            }

            execucaoAtual.setInstanteFinal(instanteAtual);
            execucaoAtual.imprimir();
            Escritor.registrar(execucaoAtual.registro());
        }
    }

    private void adicionarProcessosEmChegada(Queue<Processo> proximosProcessos, Queue<Processo> filaDeExecucao, int instanteAtual) {
        while (!proximosProcessos.isEmpty() && proximosProcessos.element().getInstanteChegada() <= instanteAtual) {
            filaDeExecucao.add(proximosProcessos.poll());
        }
    }

    private void adicionarProcessosDaEspera(Queue<Processo> filaDeEspera,Queue<Processo> filaDeExecucao,int instanteAtual) {
        while (!filaDeEspera.isEmpty() && filaDeEspera.element().proximoRetornoDeIO() != -1 && filaDeEspera.element().proximoRetornoDeIO() <= instanteAtual) {
            filaDeExecucao.add(filaDeEspera.poll());
        }
    }

    private void ordenarFilaPorBurst(LinkedList<Processo> fila) {
        ordenar(fila,(p1, p2) -> {
                int comparacao = Integer.compare(
                    p1.getBurstRestante(),
                    p2.getBurstRestante()
                );
                if (comparacao == 0) {

                    return Integer.compare(
                        p1.getInstanteChegada(),
                        p2.getInstanteChegada()
                    );
                }
                return comparacao;
            }
        );
    }
}