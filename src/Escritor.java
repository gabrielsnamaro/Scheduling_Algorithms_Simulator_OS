import java.io.FileWriter;
import java.io.IOException;

public class Escritor {
    private static final String NOME_ARQUIVO;
    private static FileWriter escritor;

    static {
        NOME_ARQUIVO = "execucoes.txt";
        limpar();
    }

    public static void limpar() {
        try {
            escritor = new FileWriter(NOME_ARQUIVO);
            escritor.close();
        } catch (IOException e) {
            System.out.println("Deu pau");
        } 
    }

    public static void registrar(String execucao) {
        try {
            escritor = new FileWriter(NOME_ARQUIVO, true);

            escritor.append(execucao);

            escritor.close();
        } catch (IOException e) {
            System.out.println("Deu pau");
        }
    }

}