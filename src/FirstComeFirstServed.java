import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FirstComeFirstServed extends Escalonador {

    public FirstComeFirstServed(LinkedList<Processo> processos) {
        super(processos);
    }

    @Override
    public void escalonar() {
        Queue<Processo> proximosProcessos = processosOrdenados();
        Queue<Processo> processosEmExecucao = new LinkedList<>();

        int instanteAtual = 0;
        while(processosEmExecucao.size() >= 0) {
            adicionarProcessosEmExecucao(proximosProcessos, processosEmExecucao, instanteAtual);

            if(!processosEmEspera.isEmpty() && processosEmEspera.element().proximoRetornoDeIO() <= instanteAtual)
                processosEmExecucao.add(processosEmEspera.poll());

            Processo atual = processosEmExecucao.poll();

            int tempoAvanco = atual.getBurstReal();

            try {
                instanteAtual = atual.avancar(tempoAvanco, instanteAtual);
            } catch (InterrupcaoIO e) {
                instanteAtual = e.getNovoInstante();
                processosEmEspera.add(atual);
            } catch (InterrupcaoEncerramento e) {
                instanteAtual = e.getNovoInstante();
            }
        }
    }
    
    private void adicionarProcessosEmExecucao(Queue<Processo> proximosProcessos, Queue<Processo> filaDeExecucao, int instanteAtual) {
        while(!proximosProcessos.isEmpty() && proximosProcessos.element().getInstanteChegada() <= instanteAtual)
            filaDeExecucao.add(proximosProcessos.poll());
    }

    private Queue<Processo> processosOrdenados() {
        LinkedList<Processo> resultado = new LinkedList<>(this.processos);
        resultado.sort((p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
    
        return resultado;
    }

    private static void printList(Queue<Processo> lista) {
        for(Processo p : lista) {
            System.out.println(p);
        }
    }
}
