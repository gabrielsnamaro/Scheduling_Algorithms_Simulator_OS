public class InterrupcaoEncerramento extends Exception {
    private int novoInstante;

    public InterrupcaoEncerramento(String message, int novoInstante) {
        super(message);
        this.novoInstante = novoInstante;
    }

    public int getNovoInstante() {
        return novoInstante;
    }
}
