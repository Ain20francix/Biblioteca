package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.CopieDAO;
import it.uniroma2.dicii.bd.model.dao.LibroDAO;
import it.uniroma2.dicii.bd.model.dao.UtenteDAO;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.view.AmministratoreView;
import it.uniroma2.dicii.bd.view.ResponsabileView;

import java.io.IOException;
import java.sql.SQLException;

public class AmministratoreController implements Controller {

    @Override
    public void start() {
            try {
                ConnectionFactory.changeRole(Role.AMMINISTRATORE);
            } catch(SQLException e) {
                throw new RuntimeException(e);
            }

            ResponsabileController r = new ResponsabileController();
            BibliotecarioController b = new BibliotecarioController();

            while(true) {
                int choice;
                try {
                    choice = AmministratoreView.showMenu();
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
                switch(choice) {
                    /*case 1 -> b.registraUtente();
                    case 2 -> b.registraPrestitoUtente();
                    case 3 -> b.restituzioneCopia();
                    case 4 -> r.reportCopieNonRestituite();
                    case 5 -> b.inserisciCopia();
                    case 6 -> r.inserisciLibro();
                    case 7 -> b.trasferimentoCopia();
                    case 8 -> b.restituzioneCopiaTrasferita();
                    case 9 -> stampaListaCopie();
                    case 10 -> stampaListaLibri();
                    case 11 -> stampaListaUtenti();*/
                    case 1 -> dismissione();
                    case 2 -> System.exit(0);
                    default -> throw new RuntimeException("Opzione invalida");

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

        public void dismissione(){
            try{
                new CopieDAO().dismissione();
            }catch(DAOException e) {
                System.out.println("Dismissione libri, non completata con usccesso: \n"+e.getMessage());
            }
        }

    }
