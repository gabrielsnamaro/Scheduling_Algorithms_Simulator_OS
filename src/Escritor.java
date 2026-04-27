import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Escritor {
    private static final String NOME_ARQUIVO;
    private static FileWriter escritor;

    static {
        NOME_ARQUIVO = "execucoes.txt";
        limpar();
    }

    private static void limpar() {
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