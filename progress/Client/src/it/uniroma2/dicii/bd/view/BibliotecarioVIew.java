package it.uniroma2.dicii.bd.view;

import java.io.IOException;
import java.util.Scanner;

public class BibliotecarioVIew {
    public static int showMenu() throws IOException {
        System.out.println("*********************************");
        System.out.println("*    LIBRARY MENU    *");
        System.out.println("*********************************\n");
        System.out.println("*** What should I do for you? ***\n");
        System.out.println("0) Registrazione Utente");
        System.out.println("1) Registrazione Prestito Utente");
        System.out.println("2) Restituzione Copia Prestito Utente");
        System.out.println("3) Report Copie in Prestito");
        System.out.println("4) Inserimento Copia");
        System.out.println("5) Inserimento Libro");
        System.out.println("6) Trasferimento Copia");
        System.out.println("7) Restituzione Copia Trasferita");
        System.out.println("8) Stampa lista copie");
        System.out.println("9) Stampa lista libri");
        System.out.println("10) Stampa lista utenti");
        System.out.println("11) Quit");

        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Please enter your choice: ");
            choice = input.nextInt();
            if (choice >= 1 && choice <= 11) {
                break;
            }
            System.out.println("Invalid option");
        }

        return choice;
    }
}
