package it.uniroma2.dicii.bd.view;

import java.io.IOException;
import java.util.Scanner;

public class ResponsabileView {
    public static int showMenu() throws IOException {
        System.out.println("*********************************");
        System.out.println("*    LIBRARY MENU    *");
        System.out.println("*********************************\n");
        System.out.println("*** Scegliere un'opzione: ***\n");
        System.out.println("1) Inserimento Libro");
        System.out.println("2) Report Copie in Prestito");
        System.out.println("3) Report Copie Trasferite");
        System.out.println("4) Quit");

        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Please enter your choice: ");
            choice = input.nextInt();
            if (choice >= 1 && choice <= 4) {
                break;
            }
            System.out.println("Invalid option");
        }

        return choice;
    }
}
