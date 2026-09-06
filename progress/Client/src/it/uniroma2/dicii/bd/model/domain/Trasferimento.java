package it.uniroma2.dicii.bd.model.domain;

import java.time.LocalDate;

public class Trasferimento {

    private final String Copia;
    private final LocalDate DataCessione;
    private final String Biblioteca;
    private final LocalDate DataRestituzione;
    private final String Stato;

    public Trasferimento(String Copia,LocalDate DataCessione,String Biblioteca,LocalDate DataRestituzione,String Stato) {
        this.Copia = Copia;
        this.DataCessione = DataCessione;
        this.Biblioteca = Biblioteca;
        this.DataRestituzione = null;
        this.Stato = Stato;
    }
}

/*
        *   Copia
            DataCessione
            Biblioteca
            DataRestituzione
            Stato
            *
            *
            *
            *
            Copia CHAR(4) PRIMARY KEY NOT NULL,
            DataCessione DATE NOT NULL,
            Biblioteca CHAR(100) NOT NULL,
            DataRestituzione DATE DEFAULT NULL,
            Stato ENUM('Prestata a','Prestata da') NOT NULL,
            FOREIGN KEY (Copia) REFERENCES Copia(Etichetta),
            FOREIGN KEY (Biblioteca) REFERENCES Biblioteca(Indirizzo)
            *
        *
        * */
