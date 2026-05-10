import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Escalonador {
    protected static class Execucao {
        protected int instanteInicial;
        protected int instanteFinal;
        protected Processo processo;
        protected String filaDePronto;
        protected Boolean houveIO;
        protected Boolean terminou;
        protected Boolean cpuOciosa;

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
            String registro = "********** " + instanteInicial + "ms até " + instanteFinal + "ms **********\n";
            
            if(!cpuOciosa) {
                registro += "* Fila de pronto: " + filaDePronto + "\n* Processo executado: " + processo.estadoAtual() + "\n";

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

    protected LinkedList<Processo> processos;
    protected int vazao;
    protected Queue<Processo> espera;    

    public Escalonador(LinkedList<Processo> processos) {
        this.processos = processos;
        this.espera = new LinkedList<>();
    }

    protected void ordenar(LinkedList<Processo> processos, Comparator<Processo> comparador) {
        processos.sort(comparador);
    }

    protected void ordenar(Queue<Processo> processos, Comparator<Processo> comparador) {
        LinkedList<Processo> lista = new LinkedList<>(processos);
        lista.sort(comparador);
        transferirListaParaFila(processos, lista);
    }

    protected LinkedList<Processo> organizarProximosProcessos() {
        LinkedList<Processo> listaProximos = new LinkedList<>(processos);
        ordenar(listaProximos, (p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
        return listaProximos;
    }

    protected LinkedList<Processo> organizarProximosProcessos(Comparator<Processo> comparador) {
        LinkedList<Processo> listaProximos = new LinkedList<>(processos);
        ordenar(listaProximos, comparador);
        return listaProximos;
    }

    protected void transferirListaParaFila(Queue<Processo> fila, List<Processo> lista) {
        while(!fila.isEmpty())
            fila.poll();

        for(int i = 0; i < lista.size(); i++) {
            fila.add(lista.get(i));
        }
    }

    public abstract void escalonar();

}