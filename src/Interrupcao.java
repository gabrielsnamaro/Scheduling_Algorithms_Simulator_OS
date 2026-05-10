public class Interrupcao extends Exception {
    private int novoInstante;

    public Interrupcao(String mensagem, int novoInstante) {
        super(mensagem);
        this.novoInstante = novoInstante;
    }

    public int getNovoInstante() {
        return novoInstante;
    }
}
