import java.util.LinkedList;

public class Escalonamento {
    private static class Execucao {
        private Processo processo;
        private int instanteInicial;
        private int instanteFinal;

        public Execucao(Processo processo, int instanteInicial, int instanteFinal) {
            this.processo = processo;
            this.instanteInicial = instanteInicial;
            this.instanteFinal = instanteFinal;
        }

        public int getDuracao() { return instanteFinal - instanteInicial; }
    }
    
    private LinkedList<Execucao> ordemDeExecucao;

    public Escalonamento() {
        ordemDeExecucao = new LinkedList<>();
    }

    public void adicionarExecucao(Processo processo, int instanteInicial, int instanteFinal) {
        ordemDeExecucao.add(new Execucao(processo, instanteInicial, instanteFinal));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Ordem de execução:\n");

        for(int i = 0; i < ordemDeExecucao.size(); i++) {
            Execucao atual = ordemDeExecucao.get(i);

            String indicadorFinalizado = atual.getDuracao() == atual.processo.getBurstTotal() 
                ? "(finalizou aqui)"
                : "";

            builder.append(String.format("* Instantes %d - %d: Processo PID %d %s\n",
               atual.instanteInicial, atual.instanteFinal, atual.processo.getPid(), indicadorFinalizado
            ));
        }

        return builder.toString();
    }
}