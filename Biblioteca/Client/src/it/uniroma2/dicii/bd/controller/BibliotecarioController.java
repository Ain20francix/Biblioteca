package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.*;
import it.uniroma2.dicii.bd.model.domain.*;
import it.uniroma2.dicii.bd.view.BibliotecarioView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;


import it.uniroma2.dicii.bd.model.domain.StampaResultSet;


public class BibliotecarioController  implements Controller{

    @Override
    public void start() {
        try {
            ConnectionFactory.changeRole(Role.BIBLIOTECARIO);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            int choice;
            try {
                choice = BibliotecarioView.showMenu();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

            switch(choice) {
                case 1 -> registraUtente();
                case 2 -> registraPrestitoUtente();
                case 3 -> restituzioneCopia();
                case 4 -> inserisciCopia();
                case 5 -> trasferimentoCopia();
                case 6 -> restituzioneCopiaTrasferita();
                case 7 -> stampaListaUtenti();
                case 8 -> cambiaPosizione();
                case 9 -> cercaCopia();
                case 10 -> System.exit(0);
                default -> throw new RuntimeException("Opzione invalida");

            }
        }
    }

    public void stampaListaBiblioteche(){
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call listaBiblioteche()}");
            boolean status = cs.execute();

            if (status) {
                ResultSet rs = cs.getResultSet();
                StampaResultSet.printResultsTable(rs,System.out);

            }
        } catch (SQLException e) {
            System.out.println("Errore nella stampa "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void stampaListaLibri(){
        try{
            new LibroDAO().listaLibri();
        }catch(DAOException e) {
            System.out.println("Stampa lista copie non completata con successo\n");
        }
    }

    public void stampaListaUtenti(){
        try{
            new UtenteDAO().listaUtenti();
        }catch(DAOException e) {
            System.out.println("Stampa lista utenti non completata con successo\n");
        }

    }

    public void registraUtente() {
        String []parametri = {"il codice fiscale", "il nome", "il cognome",
                "un'opzione a scelta tra:\n1)Uomo\n2)Donna\n3)Non binario\n4)Preferisco non specificare\n", "la data di nascita", "la città di nascita", "l'indirizzo di residenza",
                "il mezzo di contatto preferito a scelta tra:\n1)Cellulare\n2)Telefono di casa\n3)Email\n","il valore del contatto specificato\n"};
        String[] valori = new String[parametri.length];
        Utente u=null;
        int arg=0;
        boolean flag=false;
        String temp="";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(arg<7){
            flag=false;
            System.out.printf("Inserisci %s:",parametri[arg]);
            try {
                temp = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore di lettura input", e);
            }

            //Uscita nel caso si volesse interrompere l'operazione
            if(temp.equals("Exit")){return;}

                switch(arg){
                    case 0:
                        if(temp.length()!=16){
                            System.out.println("Valore non valido, riprovare! Il codice fiscale deve avere 16 caratteri\n");
                            arg--;
                            flag=true;
                        }
                        break;
                    case 1, 2:
                        if(temp.length()>30){
                            System.out.println("Valore non valido, riprovare!Inserire meno di 30 caratteri\n");
                            arg--;
                            flag=true;
                        }
                        break;
                    case 3:
                        if(!(temp.equals("1") || temp.equals("2") || temp.equals("3") || temp.equals("4"))){
                            System.out.println("Valore non valido, riprovare!\nInserire una tra le opzioni proposte\n");
                            arg--;
                            flag=true;
                        }else{
                            if(temp.equals("1")){
                                valori[arg] = "Uomo";
                            }else if(temp.equals("2")){
                                valori[arg] = "Donna";
                            }else if(temp.equals("3")){
                                valori[arg] = "Non binario";
                            }else{
                                valori[arg] = "Preferisco non specificare";
                            }
                        }
                        break;
                    case 4:
                        if (temp.length() != 10 || temp.trim().isEmpty()) {
                            System.out.println("Valore non valido, riprovare!\nInserire la data nel formato YYYY-MM-DD\n");
                            arg--;
                            flag=true;
                        }else{
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

                            try {
                                // Se il parsing va a buon fine, la data e il formato sono esatti
                                LocalDate.parse(temp, formatter);
                            } catch (DateTimeParseException e) {
                                System.out.println("Valore non valido, riprovare!\nInserire la data nel formato YYYY-MM-DD\n");
                                arg--;
                                flag=true;
                            }
                        }
                        break;
                    case 5, 6:
                        if (temp.length() > 40) {
                            System.out.println("Valore non valido, riprovare!\nInserire meno di 40 caratteri\n");
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

        //Inserimento contatto
        arg=0;
        while(arg<1) {
            flag = false;
            System.out.printf("Inserisci %s:", parametri[arg + 7]);
            try {
                temp = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore di lettura input", e);
            }

            if (temp.equals("Exit")) {
                return;
            }

            if (!(temp.equals("1") || temp.equals("2") || temp.equals("3"))){
                System.out.println("Valore non valido, riprovare!\nInserire una delle opzioni consigliate\n");
                arg--;
                flag=true;
            }else{

                if(temp.equals("1")){
                    valori[arg+7] = "Cellulare";
                }else if(temp.equals("2")){
                    valori[arg+7] = "Telefono di casa";
                }else{
                    valori[arg+7] = "Email";
                }

                System.out.printf("Inserisci il valore corrispondente:");
                try {
                    temp = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException("Errore di lettura input", e);
                }

                if(valori[arg+7].equals("Cellulare") && temp.length()!=10){
                    System.out.println("Numero di cellulare non valido, riprovare!\n");
                    arg--;
                    flag=true;
                }else if(valori[arg+7].equals("Telefono di casa") && temp.length()!=10){
                    System.out.println("Telefono di casa non valido, riprovare!\n");
                    arg--;
                    flag=true;
                }else if((valori[arg+7].equals("Email") &&
                        !(temp.toUpperCase().contains("@GMAIL.COM")
                        || temp.toUpperCase().contains("@LIBERO.IT")
                        || temp.toUpperCase().contains("@OUTLOOK.IT")
                                || temp.toUpperCase().contains("@ICLOUD.COM")
                                || temp.toUpperCase().contains("@HOTMAIL.COM")))){
                    System.out.println("Email inserita non valida, riprovare!\n");
                    arg--;
                    flag=true;
                }

                if(temp.equals("") || temp.length()==0){
                    System.out.println("Valore non valido, riprovare!\n");
                    arg--;
                    flag=true;
                }else{
                    valori[arg+8] = temp;
                }
            }
            arg++;
        }

        try {
            u = new UtenteDAO().execute(valori);
            System.out.println("Utente correttamente registrato\n");
        }  catch(DAOException e) {
            System.out.println("Operazione non riuscita:\n"+e.getMessage());
        }
    }

    public void registraPrestitoUtente(){

        //Siccome va inserito il codice fiscale dell'utente viene prima visualizzata una lista degli utenti
        stampaListaUtenti();

        String []parametri = {"Copia", "Utente", "DurataConsultazioneEspressa"};
        String[] valori = new String[parametri.length];
        PrestitoUtente pu;
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
                    if(temp.length()!=4){
                        System.out.println("Valore non valido, riprovare!\nL'etichetta deve essere formata da 4 caratteri alfanumerici\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 1:
                    if(temp.length()!=16){
                        System.out.println("Valore non valido, riprovare! Il codice fiscale deve avere 16 caratteri\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 2:
                    if(!(temp.equals("1") || temp.equals("2") || temp.equals("3"))){
                        System.out.println("Valore non valido, riprovare! La durata della consultazione può essere 1,2,3 mesi\n");
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
            pu = new PrestitoUtenteDAO().execute(valori[0],java.sql.Date.valueOf(LocalDate.now()),valori[1],Integer.parseInt(valori[2]));
            System.out.println("Prestito Utente correttamente registrato\n");
        }catch(DAOException e){
            System.out.println("Operazione non riuscita\n"+e.getMessage());
        }
    }

    public void restituzioneCopia(){
        String []parametri = {"Copia"};
        String[] valori = new String[parametri.length];
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
                    if(temp.length()!=4){
                        System.out.println("Valore non valido, riprovare!\nL'etichetta della copia deve essere di 4 caratteri\n");
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
            new PrestitoUtenteDAO().restituzioneCopiaUtente(valori[0],java.sql.Date.valueOf(LocalDate.now()));
            System.out.println("Restituzione copia correttamente avvenuta\n");
        }catch(DAOException e){
            System.out.println("Operazione non riuscita\n");
            throw new RuntimeException(e);
        }
    }

    public String inserisciCopia(){

        //Nella realtà si potrebbe leggere il codice ISBN della copia fisica, in questo scenario semplificato si stampa una lista dei libri solo per copiare l'ISBN
        //ma nella realtà questo non avverrebbe, chiaramenteutente2

        stampaListaLibri();

        Copia c=null;
        String []parametri = {"etichetta della copia", "codice del libro","numero ripiano","numero scaffale"};
        String[] valori = new String[parametri.length];
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
            if(temp.equals("Exit")){return "";}

            switch (arg) {
                case 0:
                    if(temp.length()!=4){
                        System.out.println("Valore non valido, riprovare!\nL'etichetta della copia deve essere di 4 caratteri\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 1:
                    if(temp.length()!=17){
                        System.out.println("Valore non valido, riprovare!\nIl codice del libro deve essere composto da 17 caratteri, compresi i caratteri '-'\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 2:
                    if(Integer.parseInt(temp)<0){
                        System.out.println("Valore non valido, riprovare!\nIl numero del ripiano deve essere positivo\n");
                        arg--;
                        flag=true;
                    }
                    break;
                case 3:
                    if(Integer.parseInt(temp)<0){
                        System.out.println("Valore non valido, riprovare!\nIl numero dello scaffale deve essere positivo\n");
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
            c = new CopieDAO().execute(valori);
            System.out.println("Copia correttamente inserita\n");
        } catch(DAOException e) {
            throw new RuntimeException(e);
        }

        return c.getEtichetta();
    }

    public void trasferimentoCopia(){
        Trasferimento t;
        String[] parametri = {"l'indirizzo della biblioteca", "l'ISBN del libro", "l'etichetta della copia", "il numero del ripiano", "il numero dello scaffale"};
        String[] valori = new String[parametri.length];
        int arg = 0;
        int choice = 0;
        String[] param_choice = {"Prestata a", "Prestata da"};
        /*
         * Prestata a:  Trasferimento di una copia ad una biblioteca esterna
         * Prestata da: Trasferimento di una copia da una biblioteca esterna
         * */
        boolean flag = false;
        String temp = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("*** Inserisci una delle seguenti opzioni ***\n");
        System.out.println("1) Trasferimento di una copia ad una biblioteca esterna");
        System.out.println("2) Trasferimento di una copia da una biblioteca esterna");

// Selezione scelta trasferimento
        while (!(temp.equals("1") || temp.equals("2"))) {
            if (!temp.equals("")) {
                System.out.println("Valore non valido, riprovare!");
            }
            // Uscita nel caso si volesse interrompere l'operazione
            if (temp.equals("Exit")) { return; }

            try {
                System.out.print("-> ");
                temp = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore di lettura input", e);
            }
        }

        choice = Integer.parseInt(temp);

        while (arg < parametri.length) {

            // Se l'opzione è 1 (Prestata a) NON chiediamo ISBN (arg=1), Ripiano (arg=3) e Scaffale (arg=4)
            if (choice == 1 && (arg == 1 || arg == 3 || arg == 4)) {
                valori[arg] = "0";
                arg++;
                continue;
            }

            flag = false;

            // Si stampa una lista delle biblioteche solo per facilitare l'inserimento
            if (arg == 0) {
                stampaListaBiblioteche();
            }else if(arg==1){
                stampaListaLibri();
            }

            System.out.printf("Inserisci %s: ", parametri[arg]);

            try {
                temp = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore di lettura input", e);
            }

            switch (arg) {
                case 0:
                    // indirizzo biblioteca
                    if (temp.length() > 100 || temp.length() == 0) {
                        System.out.println("Valore non valido, riprovare!\nL'indirizzo della biblioteca non può essere vuoto ed al più deve essere di 100 caratteri\n");
                        flag = true;
                    }
                    break;
                case 1:
                    // ISBN libro
                    if (temp.length() != 17) { // Ho corretto 18 in 17 per coerenza con il tuo messaggio
                        System.out.println("Valore non valido, riprovare!\nIl codice del libro deve essere composto da 17 caratteri, compresi i caratteri '-'\n");
                        flag = true;
                    }
                    break;
                case 2:
                    // copia
                    if (temp.length() != 4) {
                        System.out.println("Valore non valido, riprovare!\nL'etichetta della copia deve essere un codice alfanumerico di 4 caratteri\n");
                        flag = true;
                    }
                    break;
                case 3:
                    // ripiano
                    try {
                        if (Integer.parseInt(temp) < 0) {
                            System.out.println("Valore non valido, riprovare!\nIl numero del ripiano deve essere positivo\n");
                            flag = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Valore non valido, inserisci un numero intero.\n");
                        flag = true;
                    }
                    break;
                case 4:
                    // scaffale
                    try {
                        if (Integer.parseInt(temp) < 0) {
                            System.out.println("Valore non valido, riprovare!\nIl numero dello scaffale deve essere positivo\n");
                            flag = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Valore non valido, inserisci un numero intero.\n");
                        flag = true;
                    }
                    break;
            }

            if (!flag) {
                valori[arg] = temp;
                arg++;
            }
        }

        try {
            if (choice == 2) {
                // trasferimento DA una biblioteca esterna (riceviamo)
                //{"l'indirizzo della biblioteca", "l'ISBN del libro", "l'etichetta della copia", "il numero del ripiano", "il numero dello scaffale"};
                //CREATE PROCEDURE `trasferimentoCopia` (in var_Biblioteca CHAR(100),in var_Libro CHAR(17),in var_Copia CHAR(4),in var_NumeroRipiano TINYINT,in var_NumeroScaffale TINYINT,in var_DataCessione DATE,in var_Stato ENUM('Prestata a','Prestata da'))
                t = new TrasferimentoDAO().execute(valori[0], valori[1], valori[2], valori[3], valori[4], java.sql.Date.valueOf(LocalDate.now()), param_choice[choice - 1]);
            } else {
                // trasferimento AD una biblioteca esterna (inviamo)
                t = new TrasferimentoDAO().execute(valori[0], valori[1], valori[2], valori[3], valori[4], java.sql.Date.valueOf(LocalDate.now()), param_choice[choice - 1]);
            }
            System.out.println("Trasferimento correttamente registrato\n");
        } catch(DAOException e) {
            throw new RuntimeException(e);
        }

/*
* OSS.
* Una copia trasferita da una biblioteca esterna (in quanto viene resa disponibile una volta memorizzata nel database) può essere a sua volta trasferita ad un'altra biblioteca
*quando più avanti nel tempo la biblioteca esterna precedentemente citata mi restituirà la copia, semplicemente (dopo averla resa nuovamente disponibile)  per verificare se
* questa appartiene ad un trasferimento o meno da parte di una biblitoeca esterna sarà sufficiente fare una JOIN con Tarsferimenti con DataRestituzione=NULL, in modo
* da poter verificare se ci sono copie che devono ancora essere restituite alle biblioteche
*
* I trasferimenti vengono gestiti telefonicamente, però in caso si possono trovare in questo modo
*
* SELECT * FROM `Copia`, Trasferimenti WHERE Copia.Etichetta=Trasferimenti.Copia AND Trasferimenti.Stato='Prestata da';
* */


    }

    public void restituzioneCopiaTrasferita(){

        //bisogna agggiornare la data restituzione del prestito e rendere la copia nuovamente disponibile
        String []parametri = {"Copia","indicare una delle seguenti opzioni:\n1)Restituzione di una copia che era stata trasferita ad una biblioteca\n2)Restituzione di una copia trasferita da una biblioteca"};
        String[] valori = new String[parametri.length];
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
                    if(temp.length()!=4){
                        System.out.println("Valore non valido, riprovare!\nL'etichetta della copia deve essere di 4 caratteri\n");
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

        try{
            if(temp.equals("1")){
                new TrasferimentoDAO().restituzioneCopiaTrasferita(valori[0],java.sql.Date.valueOf(LocalDate.now()),"Prestata a");
            }else{
                new TrasferimentoDAO().restituzioneCopiaTrasferita(valori[0],java.sql.Date.valueOf(LocalDate.now()),"Prestata da");
            }
        }catch(DAOException e) {
            System.out.println("Restituzione copia trasferita non completata con successo\n"+e.getMessage());
        }
    }

    public void cambiaPosizione(){

        String []parametri = {"l'etichetta della copia","il numero del ripiano","il numero dello scaffale"};
        String[] valori = new String[parametri.length];
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

            if (temp.equals("Exit")) {
                return;
            }

            switch(arg){
                case 0:
                    if(temp.length()!=4){
                        System.out.println("Valore non valido, riprovare!\nL'etichetta della copia deve essere di 4 caratteri\n");
                        arg--;
                        flag=true;
                    }
                    break;
                default:
                    try {
                        int n=Integer.parseInt(temp);
                        if(n<0){
                            System.out.printf("Inserire un numero intero positivo\n");
                            arg--;
                            flag=false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.printf("Inserire un numero intero positivo\n");
                        arg--;
                        flag=false;
                    }
                    break;
            }
            if(!flag){
                valori[arg]=temp;
            }
            arg++;
        }

        try {
            new CopieDAO().cambiaPosizione(valori[0],Integer.parseInt(valori[1]),Integer.parseInt(valori[2]));
            System.out.println("Posizione copia correttamente aggiornata\n");
        }  catch(DAOException e) {
            System.out.println("Operazione non riuscita:\n"+e.getMessage());
        }
    }

    public void cercaCopia(){

        //Viene stampata una lista dei libri per aiutare il bibliotecario nella ricerca
        stampaListaLibri();

        boolean flag=false;
        String temp="";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(!flag){
            flag=true;
            System.out.printf("Inserisci l'ISBN del libro di cui cercare una copia:");
            try {
                temp = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore di lettura input", e);
            }

            if(temp.length()!=17){
                System.out.println("Valore non valido, riprovare!\nIl codice ISBN deve avere 17 caratteri, compreso il carattere '-'\n");
                flag=false;
            }
        }

        try {
            new CopieDAO().cercaCopia(temp);
        }catch(DAOException e){
            System.out.println("Operazione non riuscita\n");
            throw new RuntimeException(e);
        }
    }
}
