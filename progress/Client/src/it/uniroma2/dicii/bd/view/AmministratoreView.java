package it.uniroma2.dicii.bd.view;

import java.io.IOException;
import java.util.Scanner;

public class AmministratoreView {
    public static int showMenu() throws IOException {
        System.out.println("*********************************");
        System.out.println("*    LIBRARY MENU    *");
        System.out.println("*********************************\n");
        System.out.println("*** Scegliere una delle seguenti opzioni ***\n");
        System.out.println("1) Registrazione Utente");
        System.out.println("2) Registrazione Prestito Utente");
        System.out.println("3) Restituzione Copia Prestito Utente");
        System.out.println("4) Report Copie in Prestito");
        System.out.println("5) Inserimento Copia");
        System.out.println("6) Inserimento Libro");
        System.out.println("7) Trasferimento Copia");
        System.out.println("8) Restituzione Copia Trasferita");
        System.out.println("9) Stampa lista copie");
        System.out.println("10) Stampa lista libri");
        System.out.println("11) Stampa lista utenti");
        System.out.println("12) Esci");

        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Inserire l'opzione desiderata: ");
            choice = input.nextInt();
            if (choice >= 1 && choice <= 12) {
                break;
            }
            System.out.println("Opzione invalida");
        }

        return choice;
    }
}
