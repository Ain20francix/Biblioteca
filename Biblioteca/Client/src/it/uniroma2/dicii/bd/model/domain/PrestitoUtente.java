package it.uniroma2.dicii.bd.model.domain;
import java.time.LocalDate;

public class PrestitoUtente {

    private final String Copia;
    private final LocalDate DataPrestito;
    private final String Utente;
    private final LocalDate DataRestituzione;
    private final int DurataConsultazioneEspressa;

    public PrestitoUtente(String Copia,LocalDate DataPrestito,String Utente,int DurataConsultazioneEspressa) {
        this.Copia = Copia;
        this.DataPrestito = DataPrestito;
        this.Utente = Utente;
        this.DataRestituzione = null;
        this.DurataConsultazioneEspressa = DurataConsultazioneEspressa;
    }

}
