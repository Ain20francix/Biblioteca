package it.uniroma2.dicii.bd.model.domain;

import java.time.LocalDate;

public class Libro {
    private final String ISBN;
    private final String Titolo;
    private final String CasaEditrice;
    private final boolean Dismissione;
    private final String Genere;
    private final LocalDate DataImmissione;


    public Libro(String ISBN, String Titolo, String CasaEditrice, boolean Dismissione, LocalDate DataImmissione, String Genere) {
        this.ISBN = ISBN;
        this.Titolo = Titolo;
        this.CasaEditrice = CasaEditrice;
        this.Dismissione=Dismissione;
        this.DataImmissione=DataImmissione;
        this.Genere = Genere;
    }
}

/*
* ISBN CHAR(17) PRIMARY KEY NOT NULL,
    ISBN CHAR(17) PRIMARY KEY NOT NULL,
    Titolo CHAR(50) NOT NULL,
    CasaEditrice CHAR(40) NOT NULL,
    Dismissione BOOLEAN NOT NULL DEFAULT FALSE,
    DataImmissione DATE NOT NULL,
    Genere ('Biografia', 'Autobiografia','Romanzo storico', ...)
* */