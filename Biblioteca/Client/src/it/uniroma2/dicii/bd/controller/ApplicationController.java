package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.model.domain.Credentials;

public class ApplicationController implements Controller {
    Credentials cred;

    @Override
    public void start() {
        LoginController loginController = new LoginController();
        loginController.start();
        cred = loginController.getCred();

        if(cred.getRole() == null) {
            throw new RuntimeException("Credenziali invalide");
        }

        switch(cred.getRole()) {
            case BIBLIOTECARIO  -> new BibliotecarioController().start();
            case RESPONSABILE   -> new ResponsabileController().start();
            case AMMINISTRATORE -> new AmministratoreController().start();
            default -> throw new RuntimeException("Credenziali invalide");
        }
    }
}
