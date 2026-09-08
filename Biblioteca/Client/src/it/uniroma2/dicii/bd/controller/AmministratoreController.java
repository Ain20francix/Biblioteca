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

            while(true) {
                int choice;
                try {
                    choice = AmministratoreView.showMenu();
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
                switch(choice) {
                    case 1 -> dismissione();
                    case 2 -> System.exit(0);
                    default -> throw new RuntimeException("Opzione invalida");

                }
            }
        }

        public void dismissione(){
            try{
                new CopieDAO().dismissione();
                System.out.println("Dismissione copie di libri non prestati da più di 10 anni, avvenuta con successo!");
            }catch(DAOException e) {
                System.out.println("Dismissione libri, non completata con successo: \n"+e.getMessage());
            }
        }

    }
