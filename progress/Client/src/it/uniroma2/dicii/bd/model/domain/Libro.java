package it.uniroma2.dicii.bd.model.domain;

public class Libro {
    private final String ISBN;
    private final String Titolo;
    private final String CasaEditrice;
    private final String Genere;

    public Libro(String ISBN,String Titolo, String CasaEditrice, String Genere) {
        this.ISBN = ISBN;
        this.Titolo = Titolo;
        this.CasaEditrice = CasaEditrice;
        this.Genere = Genere;
    }
}

/*
* ISBN CHAR(17) PRIMARY KEY NOT NULL,
    Titolo CHAR(50) NOT NULL,
    CasaEditrice CHAR(40) NOT NULL,
    Genere ENUM ('Biografia', 'Autobiografia','Romanzo storico', ...)
* */