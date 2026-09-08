package it.uniroma2.dicii.bd.model.domain;

public class Utente {

    private final String CF;
    private final String Nome;
    private final String Cognome;
    private final String Sesso;
    private final String DataNascita;
    private final String LuogoNascita;
    private final String Residenza;
    private final String MezzoPreferito;

    public Utente(String CF, String Nome, String Cognome, String Sesso, String DataNascita, String LuogoNascita, String Residenza, String MezzoPreferito) {
        this.CF = CF;
        this.Nome = Nome;
        this.Cognome = Cognome;
        this.Sesso = Sesso;
        this.DataNascita = DataNascita;
        this.LuogoNascita = LuogoNascita;
        this.Residenza = Residenza;
        this.MezzoPreferito = MezzoPreferito;
    }

    public String getCF() {
        return CF;
    }

}
