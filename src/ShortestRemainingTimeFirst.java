import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class ShortestRemainingTimeFirst extends Escalonador {
    private static final Comparator<Processo> COMPARADOR_PADRAO = (p1, p2) -> Integer.compare(p1.getBurstRestante(), p2.getBurstRestante());

    public ShortestRemainingTimeFirst(LinkedList<Processo> processos) {
        super(processos);
    }

    @Override
    public void escalonar() {
        Queue<Processo> proximos = organizarProximosProcessos();
        Queue<Processo> prontos = new LinkedList<Processo>();

        int instanteAtual = 0;
        while((prontos.size() + proximos.size() + espera.size()) > 0) {
            RegistroExecucao execucao = new RegistroExecucao();

            adicionarEmChegada(prontos, proximos, instanteAtual);
            adicionarDaEspera(prontos, instanteAtual);

            LinkedList<Processo> listaProntos = new LinkedList<>(prontos);
            ordenar(listaProntos, COMPARADOR_PADRAO);
            transferirListaParaFila(prontos, listaProntos);

            int avanco = calcularAvanco(proximos, instanteAtual);

            execucao.setFilaDePronto(prontos);
            execucao.setInstanteInicial(instanteAtual);

            if(!prontos.isEmpty()) {
                Processo atual = prontos.element();
                execucao.setProcesso(atual);

                try {
                    instanteAtual = atual.avancar(avanco, instanteAtual);
                } catch(InterrupcaoIO e) {
                    instanteAtual = e.getNovoInstante();
                    espera.add(prontos.poll());
                    execucao.reportarIO();
                } catch(InterrupcaoEncerramento e) {
                    instanteAtual = e.getNovoInstante();
                    prontos.poll();
                    execucao.reportarFinalizado();
                }
            } else {
                execucao.reportarOcio();
                instanteAtual += avanco;
            }

            execucao.setInstanteFinal(instanteAtual);
            execucao.imprimir();
            Escritor.registrar(execucao.registro());
        }
    }

    private int calcularAvanco(Queue<Processo> proximos, int instante) {
        return Integer.max(
            Integer.min(
                proximos.isEmpty() ? Integer.MAX_VALUE : proximos.element().getInstanteChegada(),
                espera.isEmpty() ? Integer.MAX_VALUE : espera.element().proximoRetornoDeIO()
            ) - instante, 1
        );
    }

    private void esperar(Scanner teclado) {
        teclado.nextLine();
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
