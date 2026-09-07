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
                case 4 -> inserisciCopia();
                case 5 -> trasferimentoCopia();
                case 6 -> restituzioneCopiaTrasferita();
                case 7 -> stampaListaCopie();
                case 8 -> stampaListaLibri();
                case 9 -> stampaListaUtenti();
                case 10 -> cambiaPosizione();
                case 11 -> System.exit(0);
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

        String []parametri = {"il codice fiscale", "il nome", "il cognome",
                "il sesso a scelta tra:\n1)Uomo\n2)Donna\n3)Non binario\n4)Preferisco non specificare\n", "la data di nascita", "la città di nascita", "l'indirizzo di residenza",
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
                }
                if(!flag){
                    valori[arg] = temp;
                }
            arg++;
        }

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

        System.out.println("Fine inserimento parametri\n");

        try {
            u = new UtenteDAO().execute(valori);
            System.out.println("Utente correttamente registrato\n");
        }  catch(DAOException e) {
            System.out.println("Operazione non riuscita:\n"+e.getMessage());
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
        System.out.println("Fine inserimento parametri\n");

        try {
            pu = new PrestitoUtenteDAO().execute(valori[0],java.sql.Date.valueOf(LocalDate.now()),valori[1],Integer.parseInt(valori[2]));
            System.out.println("Prestito Utente correttamente registrato\n");
        }catch(DAOException e){
            System.out.println("Operazione non riuscita\n"+e.getMessage());
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

        System.out.println("Fine inserimento parametri\n");

        try {
            new CopieDAO().cambiaPosizione(valori[0],Integer.parseInt(valori[1]),Integer.parseInt(valori[2]));
            System.out.println("Utente correttamente registrato\n");
        }  catch(DAOException e) {
            System.out.println("Operazione non riuscita:\n"+e.getMessage());
        }
    }
}
