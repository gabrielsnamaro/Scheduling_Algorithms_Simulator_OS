public class InterrupcaoIO extends Exception {
    private int novoInstante;

    public InterrupcaoIO(String message, int novoInstante) {
        super(message);
        this.novoInstante = novoInstante;
    }

    public int getNovoInstante() {
        return novoInstante;
    }
}
