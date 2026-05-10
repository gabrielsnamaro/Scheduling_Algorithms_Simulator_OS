public class MetricaIndividual {
    private Processo processo;
    private int instanteDeInicio;
    private int instanteDeTermino;
    private int tempoEmIO;
    private boolean iniciou;

    public MetricaIndividual(Processo processo) {
        this.processo = processo;
        tempoEmIO = 0;
        iniciou = false;
    }

    public void iniciar(int instanteDeInicio) {
        if(!iniciou) {
            this.instanteDeInicio = instanteDeInicio;
            iniciou = true;
        }
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

    public int getPid() {
        return processo.getPid();
    }

    public int getInicio() {
        return instanteDeInicio;
    }

    @Override
    public String toString() {
        if(instanteDeTermino == 0)
            throw new UnsupportedOperationException("Processo não foi finalizado. Métricas não foram completamente colhidas. ");

        return String.format(
            "* PID: %d\n\t-> Chegou em: %dms\n\t-> Iniciou em: %dms\n\t-> Terminou em %dms\n\t-> Tempo total realizando I/O: %dms\n\t-> Tempo de espera: %dms\n",
            processo.getPid(), processo.getInstanteChegada(), instanteDeInicio, instanteDeTermino, tempoEmIO, calcularEspera()
        );
    }
}