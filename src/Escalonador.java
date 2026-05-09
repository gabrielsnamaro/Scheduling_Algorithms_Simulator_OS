import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    protected LinkedList<Processo> processos;
    protected Map<Integer, Processo> espera;
    protected Map<Integer, Processo> retorno;
    protected int vazao;

    public Escalonador(LinkedList<Processo> processos) {
        this.processos = processos;
        espera = new HashMap<>(processos.size());
        retorno = new HashMap<>(processos.size());
    }

    protected void ordenar(LinkedList<Processo> processos, Comparator<Processo> comparador) {
        processos.sort(comparador);
    }

    protected LinkedList<Processo> organizarProximosProcessos() {
        LinkedList<Processo> listaProximos = new LinkedList<>(processos);
        ordenar(listaProximos, (p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada()));
        return listaProximos;
    }

    public abstract void escalonar();

}