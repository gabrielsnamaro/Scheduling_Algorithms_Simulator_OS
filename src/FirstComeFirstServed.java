import java.util.LinkedList;
import java.util.Queue;

public class FirstComeFirstServed extends Escalonador {

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
        adicionarProcessosDaEspera(processosEmEspera, processosProntos, instanteAtual);

        while((proximosProcessos.size() + processosEmEspera.size() + processosProntos.size()) > 0) {
            Execucao execucaoAtual = new Execucao();

            execucaoAtual.setInstanteInicial(instanteAtual);

            if(!processosProntos.isEmpty()) {
                Processo atual = processosProntos.element();
                int tempoAvanco = atual.getBurstReal();

                execucaoAtual.setFilaDePronto(processosProntos);
                execucaoAtual.setProcesso(atual);

                atual = processosProntos.poll();
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

    private Queue<Processo> processosOrdenados() {
        LinkedList<Processo> resultado = new LinkedList<>(this.processos);
        resultado.sort((p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
    
        return resultado;
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
