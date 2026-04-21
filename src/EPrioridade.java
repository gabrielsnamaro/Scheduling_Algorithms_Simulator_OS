public enum EPrioridade {
    ALTA(1),
    BAIXA(2);

    private int valor;

    private EPrioridade(int valor) {
        this.valor = valor;
    };

    public int getValor() {
        return valor;
    }

    public static EPrioridade doValor(int valor) {
        EPrioridade p = null;

        for(EPrioridade prioridade : EPrioridade.values()) {
            if(prioridade.getValor() == valor)
                p = prioridade;
        }

        return p;
    }
}
