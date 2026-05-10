public class MetricaIndividual {
    private Processo processo;
    private int instanteDeInicio;
    private int instanteDeTermino;
    private int tempoEmIO;

    public MetricaIndividual(Processo processo) {
        this.processo = processo;
        tempoEmIO = 0;
    }

    public void setInstanteDeInicio(int instanteDeInicio) {
        this.instanteDeInicio = instanteDeInicio;
    }

    public void setInstanteDeTermino(int instanteDeTermino) {
        this.instanteDeTermino = instanteDeTermino;
    }

    public void adicionarTempoEmIO(int tempo) {
        tempoEmIO += tempo;
    }

    private int calcularEspera() {
        int esperaParaComecar = instanteDeInicio - processo.getInstanteChegada();
        int esperaAposIniciar = ((instanteDeTermino - instanteDeInicio) - processo.getBurstTotal()) - tempoEmIO;

        return esperaParaComecar + esperaAposIniciar;
    }

    public int getTermino() {
        return instanteDeTermino;
    }

    @Override
    public String toString() {
        if(instanteDeTermino == 0)
            throw new UnsupportedOperationException("Processo não foi finalizado. Métricas não foram completamente colhidas. ");

        return String.format(
            "* PID: %d\n* Iniciou em: %dms\n* Terminou em %dms\n* Tempo total realizando I/O: %dms\n* Tempo de espera: %dms\n",
            processo.getPid(), instanteDeInicio, instanteDeTermino, tempoEmIO, calcularEspera()
        );
    }
}