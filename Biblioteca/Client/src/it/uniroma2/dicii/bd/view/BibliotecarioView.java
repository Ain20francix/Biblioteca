package it.uniroma2.dicii.bd.view;

import java.io.IOException;
import java.util.Scanner;

public class BibliotecarioView {
    public static int showMenu() throws IOException {
        System.out.println("*********************************");
        System.out.println("*    LIBRARY MENU    *");
        System.out.println("*********************************\n");
        System.out.println("*** Scegliere una delle seguenti operazioni ***\n");
        System.out.println("1) Registrazione Utente");
        System.out.println("2) Registrazione Prestito Utente");
        System.out.println("3) Restituzione Copia Prestito Utente");
        System.out.println("4) Inserimento Copia");
        System.out.println("5) Trasferimento Copia");
        System.out.println("6) Restituzione Copia Trasferita");
        System.out.println("7) Stampa lista utenti");
        System.out.println("8) Cambia posizione copia");
        System.out.println("9) Cerca copia");
        System.out.println("10) Esci");

        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Inserire l'opzione desiderata: ");
            choice = input.nextInt();
            if (choice >= 1 && choice <= 10) {
                break;
            }
            System.out.println("Opzione invalida");
        }

        return choice;
    }
}
