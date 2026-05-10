import java.util.HashMap;
import java.util.Map;

public class MetricaGeral {
    private Map<Integer, MetricaIndividual> processosRegistrados;

    public MetricaGeral() {
        processosRegistrados = new HashMap<>();
    }

    public void adicionarIndividual(MetricaIndividual metrica) {
        processosRegistrados.put(metrica.getPid(), metrica);
    }

    /**
     * 
     * @return Vazão: processos finalizados por milissegundo.
     */
    public double vazao() {
        int[] instantes = inicioETermino();
        int execucaoTotal = instantes[1] - instantes[0];

        return processosRegistrados.size() / (double) execucaoTotal;
    }

    private int[] inicioETermino() {
        int[] retorno = new int[2];

        if (processosRegistrados.isEmpty()) {
            return new int[]{0, 0};
        }

        Map.Entry<Integer, MetricaIndividual> primeiraEntrada = processosRegistrados.entrySet().iterator().next();
        MetricaIndividual primeiroAComecar = primeiraEntrada.getValue();
        MetricaIndividual ultimoATerminar = primeiraEntrada.getValue();

        for (Map.Entry<Integer, MetricaIndividual> entrada : processosRegistrados.entrySet()) {
            MetricaIndividual atual = entrada.getValue();
            if (ultimoATerminar.getTermino() < atual.getTermino())
                ultimoATerminar = atual;
            if (primeiroAComecar.getInicio() > atual.getInicio())
                primeiroAComecar = atual;
        }

        retorno[0] = primeiroAComecar.getInicio();
        retorno[1] = ultimoATerminar.getTermino();
        return retorno;
    }

    public MetricaIndividual gerarMetrica(Processo processo) {
        MetricaIndividual retorno;
        int pid = processo.getPid();

        if(processosRegistrados.containsKey(pid)) {
            retorno = processosRegistrados.get(pid);
        } else {
            retorno = new MetricaIndividual(processo);
            processosRegistrados.put(pid, retorno);
        }

        return retorno;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("************ MÉTRICAS DE EXECUÇÃO ************\n* Vazão: " + vazao() + " processos por milissegundo\n*\n");

        for(Map.Entry<Integer, MetricaIndividual> individual : processosRegistrados.entrySet()) {
            builder.append(individual.getValue().toString());
        }

        return builder.toString();
    }
}
