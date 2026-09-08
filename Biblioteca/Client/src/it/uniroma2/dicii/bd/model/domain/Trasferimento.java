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

