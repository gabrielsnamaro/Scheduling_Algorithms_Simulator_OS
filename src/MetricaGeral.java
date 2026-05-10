import java.util.List;

public class MetricaGeral {
    private List<MetricaIndividual> processosRegistrados;

    public void adicionarIndividual(MetricaIndividual metrica) {
        processosRegistrados.add(metrica);
    }

    /**
     * 
     * @return Vazão: processos finalizados por milissegundo.
     */
    public double vazao() {
        return processosRegistrados.size() / instanteFinal();
    }

    private int instanteFinal() {
        MetricaIndividual ultimo = processosRegistrados.get(0);

        for(int i = 1; i < processosRegistrados.size(); i++) {
            MetricaIndividual atual = processosRegistrados.get(i);

            if(ultimo.getTermino() < atual.getTermino())
                ultimo = atual;
        }

        return ultimo.getTermino();
    }
}
