package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.CopieDAO;
import it.uniroma2.dicii.bd.model.dao.LibroDAO;
import it.uniroma2.dicii.bd.model.domain.Libro;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.view.ResponsabileView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDate;
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

            BibliotecarioController b = new BibliotecarioController();

            switch(choice) {
                case 1 -> inserisciLibro();
                case 2 -> reportCopieNonRestituite();
                case 3 -> reportCopieTrasferite();
                case 4 -> System.exit(0);
                default -> throw new RuntimeException("Opzione invalida");
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

    public void reportCopieTrasferite(){

        try{
            new CopieDAO().reportCopieTrasferite();
        }catch(DAOException e) {
            System.out.println("Stampa lista copie non restituite, non completata con successo: \n"+e.getMessage());
        }
    }

    public void inserisciLibro(){
        Libro l;
        String []parametri = {"ISBN del libro", "titolo del libro","casa editrice del libro","genere del libro","nome autore del libro","cognome autore del libro"};
        String []valori = new String[parametri.length];
        String []generi={"Arte e Fotografia", "Autobiografia", "Avventura", "Azione", "Bambini e Ragazzi", "Biografia", "Classici", "Cucina e Gastronomia", "Diritto", "Economia e Finanza", "Fantascienza", "Fantasy", "Filosofia", "Fumetti e Graphic Novel", "Giallo", "Hobbistica e Tempo libero", "Horror", "Informatica e Tecnologia", "Medicina e Salute", "Narrativa Contemporanea", "Poesia", "Psicologia", "Religione e Spiritualità", "Romanzo di formazione", "Romanzo Rosa", "Romanzo storico", "Saggistica", "Scienze", "Self-help e Crescita Personale", "Storia", "Teatro", "Thriller", "Umoristico", "Viaggi"};
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
                        System.out.println("Valore non valido, riprovare!\nIl genere deve essere uno tra i seguenti:\n-Arte e Fotografia\n" +
                                "-Autobiografia\n-Avventura\n-Azione\n-Bambini e Ragazzi\n-Biografia\n-Classici\n-Cucina e Gastronomia\n" +
                                "-Diritto\n-Economia e Finanza\n-Fantascienza\n-Fantasy\n-Filosofia\n-Fumetti e Graphic Novel\n" +
                                "-Giallo\n-Hobbistica e Tempo libero\n-Horror\n-Informatica e Tecnologia\n-Medicina e Salute\n" +
                                "-Narrativa Contemporanea\n-Poesia\n-Psicologia\n-Religione e Spiritualità\n-Romanzo di formazione\n" +
                                "-Romanzo Rosa\n-Romanzo storico\n-Saggistica\n-Scienze\n-Self-help e Crescita Personale\n-Storia\n-Teatro\n-Thriller\n-Umoristico\n-Viaggi\n");
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
            l = new LibroDAO().execute(valori[0],valori[1],valori[2],java.sql.Date.valueOf(LocalDate.now()),valori[3],valori[4],valori[5]);
            System.out.println("Copia correttamente inserita\n");
        } catch(DAOException e) {
            throw new RuntimeException(e);
        }
    }
}
