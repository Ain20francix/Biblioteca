package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.CopieDAO;
import it.uniroma2.dicii.bd.model.dao.LibroDAO;
import it.uniroma2.dicii.bd.model.domain.Libro;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.view.BibliotecarioVIew;
import it.uniroma2.dicii.bd.view.ResponsabileView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;

public class ResponsabileController implements Controller{

    @Override
    public void start() {
        try {
            ConnectionFactory.changeRole(Role.RESPONSABILE);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            int choice;
            try {
                choice = ResponsabileView.showMenu();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

            switch(choice) {
                case 1 -> inserisciLibro();
                case 2 -> reportCopieNonRestituite();
                case 3 -> System.exit(0);
                default -> throw new RuntimeException("Invalid choice");
                //aggiungere report copie trasferite
                //migliorare report copie in prestito aggiungendo anche identificativo dell'utente e contatto
            }
        }
    }

    public void reportCopieNonRestituite(){

        try{
            new CopieDAO().reportCopieNonRestituite();
        }catch(DAOException e) {
            System.out.println("Stampa lista copie non restituite, non completata con successo: \n"+e.getMessage());
        }
    }

    public void inserisciLibro(){
        Libro l;
        String []parametri = {"ISBN del libro", "titolo del libro","casa editrice del libro","genere del libro","nome autore del libro","cognome autore del libro"};
        String []valori = new String[parametri.length];
        String []generi={"Biografia", "Autobiografia","Romanzo storico", "Giallo", "Thriller" , "Azione" , "Fantascienza", "Fantasy", "Horror" , "Romanzo di formazione" , "Romanzo Rosa", "Umoristico"};
        int arg=0;
        boolean flag=false;
        String temp="";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(arg<parametri.length) {
            flag = false;
            System.out.printf("Inserisci %s:", parametri[arg]);
            try {
                temp = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore di lettura input", e);
            }

            //Uscita nel caso si volesse interrompere l'operazione
            if(temp.equals("Exit")){return;}

            switch (arg) {
                case 0:
                    if(temp.length()!=17){
                        System.out.println("Valore non valido, riprovare!\nIl codice ISBN deve avere 17 caratteri, compreso il carattere '-'\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 1:
                    if(temp.length()>50 || temp.equals("")){
                        System.out.println("Valore non valido, riprovare!\nIl titolo del libro non deve essere più lungo di 50 caratteri e non può essere vuoto\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 2:
                    if(temp.length()>40 || temp.equals("")){
                        System.out.println("Valore non valido, riprovare!\nIl nome della casa editrice non deve essere più lungo di 40 caratteri e non può essere vuoto\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 3:
                    if(!Arrays.asList(generi).contains(temp)){
                        System.out.println("Valore non valido, riprovare!\nIl genere deve essere uno tra i seguenti:\n-Biografia\n-Autobiografia\n-Romanzo storico\n-Giallo\n-Thriller\n-Azione\n-Fantascienza\n-Fantasy\n-Horror\n-Romanzo di formazione\n-Romanzo Rosa\n-Umoristico\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 4:
                    if(temp.length()>30 || temp.equals("")){
                        System.out.println("Valore non valido, riprovare!\nIl nome dell'autore non può eccedere i 30 caratteri e non può essere vuoto");
                        arg--;
                        flag=true;
                    }
                    break;
                case 5:
                    if(temp.length()>30 || temp.equals("")){
                        System.out.println("Valore non valido, riprovare!\nIl cognome dell'autore non può eccedere i 30 caratteri e non può essere vuoto");
                        arg--;
                        flag=true;
                    }
                    break;
            }
            if(!flag){
                valori[arg] = temp;
            }
            arg++;
        }

        try {
            l = new LibroDAO().execute(valori);
            System.out.println("Copia correttamente inserita\n");
        } catch(DAOException e) {
            throw new RuntimeException(e);
        }
    }
}
