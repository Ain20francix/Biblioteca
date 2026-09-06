package it.uniroma2.dicii.bd.model.domain;

public class Copia {
    private final String etichetta;
    private final String codiceLibro;
    private final String stato;
    private final Integer numeroRipiano;
    private final Integer numeroScaffale;

    public Copia(String etichetta, String codiceLibro, String stato, int numeroRipiano, int numeroScaffale) {
        this.etichetta = etichetta;
        this.codiceLibro = codiceLibro;
        this.stato = stato;
        this.numeroRipiano = numeroRipiano;
        this.numeroScaffale = numeroScaffale;
    }

    public String getEtichetta() {
        return etichetta;
    }


}