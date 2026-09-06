package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.*;
import it.uniroma2.dicii.bd.model.domain.*;
import it.uniroma2.dicii.bd.view.BibliotecarioVIew;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;

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
                choice = BibliotecarioVIew.showMenu();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

            switch(choice) {
                case 1 -> registraUtente();
                case 2 -> registraPrestitoUtente();
                case 3 -> restituzioneCopia();
                case 4 -> reportCopieNonRestituite();
                case 5 -> inserisciCopia();
                case 6 -> inserisciLibro();
                case 7 -> trasferimentoCopia();
                case 8 -> restituzioneCopiaTrasferita();
                case 9 -> stampaListaCopie();
                case 10 -> stampaListaLibri();
                case 11 -> stampaListaUtenti();
                case 12 -> System.exit(0);
                default -> throw new RuntimeException("Invalid choice");

            }
        }
    }

    public void stampaListaLibri(){
        try{
            new LibroDAO().listaLibri();
        }catch(DAOException e) {
            System.out.println("Stampa lista copie non completata con successo\n");
        }
    }

    public void stampaListaCopie(){
        try{
            new CopieDAO().listaCopie();
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

        //Bisogna aggiungere il contatto dell'utente obbligatorio al momento della registrazione

        String []parametri = {"CF", "Nome", "Cognome", "Sesso", "DataNascita", "LuogoNascita", "Residenza", "MezzoPreferito"};
        String[] valori = new String[parametri.length];
        Utente u=null;
        int arg=0;
        boolean flag=false;
        String temp="";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(arg<8){
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
                    case 1:
                        if(temp.length()>30){
                            System.out.println("Valore non valido, riprovare!Inserire meno di 30 caratteri\n");
                            arg--;
                            flag=true;
                        }
                        break;
                    case 2:
                        if(temp.length()>30){
                            System.out.println("Valore non valido, riprovare!Inserire meno di 30 caratteri\n");
                            arg--;
                            flag=true;
                        }
                        break;
                    case 3:
                        if(!(temp.equals("Uomo") || temp.equals("Donna") || temp.equals("Non binario") || temp.equals("Preferisco non specificare"))){
                            System.out.println("Valore non valido, riprovare!\nInserire uno tra i seguenti valori {Uomo,Donna,Non binario,Preferisco non specificare}\n");
                            arg--;
                            flag=true;
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
                    case 5:
                        if (temp.length() > 40) {
                            System.out.println("Valore non valido, riprovare!\nInserire meno di 40 caratteri\n");
                            arg--;
                            flag=true;
                        }
                        break;
                    case 6:
                        if (temp.length() >40) {
                            System.out.println("Valore non valido, riprovare!\nInserire meno di 40 caratteri\n");
                            arg--;
                            flag=true;
                        }
                        break;
                    case 7:
                        if(!(temp.equals("Email") || temp.equals("Cellulare") || temp.equals("Telefono di casa"))){
                            System.out.println("Valore non valido, riprovare!\nInserire uno tra i seguenti valori {Email,Cellulare,Telefono di casa}\n");
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
        System.out.println("Fine inserimento parametri\n");

        try {
            u = new UtenteDAO().execute(valori);
            System.out.println("Copia correttamente inserita\n");
        }  catch(DAOException e) {
            System.out.println("Operazione non riuscita\n");
        }
    }

    public void registraPrestitoUtente(){

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
                    if(temp.equals(1) || temp.equals(2) || temp.equals(3)){
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
        System.out.println("Fine inserimento parametri\n");

        try {
            pu = new PrestitoUtenteDAO().execute(valori[0],java.sql.Date.valueOf(LocalDate.now()),valori[1],Integer.parseInt(valori[2]));
            System.out.println("Prestito Utente correttamente registrato\n");
        }catch(DAOException e){
            System.out.println("Operazione non riuscita\n");
            throw new RuntimeException(e);
        }
    }

    public void restituzioneCopia(){

        //bisogna agggiornare la data restituzione del prestito e rendere la copia nuovamente disponibile
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

            //(Copia,DataRestituzione)
            try {
                new PrestitoUtenteDAO().restituzioneCopiaUtente(valori[0],java.sql.Date.valueOf(LocalDate.now()));
                System.out.println("RRestituzione copia correttamente avvenuta\n");
            }catch(DAOException e){
                System.out.println("Operazione non riuscita\n");
                throw new RuntimeException(e);
            }
    }

    public void reportCopieNonRestituite(){

        try{
            new CopieDAO().reportCopieNonRestituite();
        }catch(DAOException e) {
            System.out.println("Stampa lista copie non restituite, non completata con successo: \n"+e.getMessage());
        }
    }

    public String inserisciCopia(){
        Copia c;
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

    public void trasferimentoCopia(){
        Trasferimento t;
        String []parametri = {"l'etichetta della copia", "l'indirizzo della biblioteca"};
        String[] valori = new String[parametri.length];
        int arg=0;
        int choice=0;
        String []param_choice={"Prestata a","Prestata da"};
        /*
        * Prestata a:  Trasferimento di una copia ad una biblioteca esterna
        * Prestata da: Trasferimento di una copia da una biblioteca esterna
        * */
        boolean flag=false;
        String temp="";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("*** Inserisci una delle seguenti opzioni ***\n");
        System.out.println("1) Trasferimento di una copia ad una biblioteca esterna");
        System.out.println("2) Trasferimento di una copia da una biblioteca esterna");

        //Selezione scelta trasferimento
        while(!(temp.equals("1") || temp.equals("2"))){
            if(!temp.equals("")){
                System.out.println("Valore non valido, riprovare!");
            }
            //Uscita nel caso si volesse interrompere l'operazione
            if(temp.equals("Exit")){return;}

            try {
                System.out.print("-> ");
                temp = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore di lettura input", e);
            }
        }

        choice=Integer.parseInt(temp); //registrazione scelta del bibliotecario

        //Caso registrazione dati copia proveniente da una biblioteca esterna
        if(temp.equals("2")){
            System.out.println("Inserisci i dati della copia ricevuta!");
            valori[arg]=(String)inserisciCopia();//inserimento dati della copia trasferita
            arg++;
        }

        //Registrazione trasferimento
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
                    if (temp.length() != 4) {
                        System.out.println("Valore non valido, riprovare!\nL'etichetta della copia deve essere un codice alfanumerico di 4 caratteri\n");
                        arg--;
                        flag = true;
                    }
                    break;
                case 1:
                    if (temp.length() > 100 || temp.length() == 0) {
                        System.out.println("Valore non valido, riprovare!\nL'indirizzo della biblioteca non può essere vuoto ed al più deve essere di 100 caratteri\n");
                        arg--;
                        flag = true;
                    }
                    break;
            }
                if(!flag){
                    valori[arg]=temp;
                }
                arg++;
            }

        try {
            //(in var_Copia CHAR(4),in var_DataCessione DATE,in var_Biblioteca CHAR(100),in var_Stato ENUM('Prestata a','Prestata da'))
            t = new TrasferimentoDAO().execute(valori[0],java.sql.Date.valueOf(LocalDate.now()),valori[1],param_choice[choice-1]); //Copia,DataCessione,Biblioteca,Stato
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
        String []parametri = {"Copia","indicare una delle seguenti opzioni:\n1)Copia trasferita ad una biblioteca\n2)Copia trasferita da una biblioteca"};
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
}
