package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.PrestitoUtente;
import it.uniroma2.dicii.bd.model.domain.Utente;

import java.sql.*;
import java.time.LocalDate;

public class PrestitoUtenteDAO implements GenericProcedureDAO<PrestitoUtente>{

    @Override
    public PrestitoUtente execute(Object... params) throws DAOException {

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call registraPrestitoUtente(?,?,?,?,?,?)}"); //le ultime 2 variabili sono out per ripiano  e scaffale
            cs.setString(1, (String) params[0]);    //Copia
            cs.setDate(2, (Date) params[1]);        //DataPrestito
            cs.setString(3, (String) params[2]);    //Utente
            cs.setInt(4, (int) params[3]);          //DataConsultazioneEspressa
            cs.registerOutParameter(5,Types.TINYINT);
            cs.registerOutParameter(6,Types.TINYINT);
            cs.execute();

            System.out.println("La copia si trova sul ripiano "+cs.getInt(5)+", scaffale "+cs.getInt(6));

            System.out.println("fine procedure registra-prestito_utente");
        } catch (SQLException e) {
            throw new DAOException("Prestito non correttamente inserito: " + e.getMessage());
        }

        return new PrestitoUtente((String)params[0], ((Date)params[1]).toLocalDate(),(String)params[2],(int)params[3]);
    }

    public PrestitoUtente restituzioneCopiaUtente(Object... params) throws DAOException {

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call restituzioneCopiaUtente(?,?,?)}");
            cs.setString(1, (String) params[0]);    //Copia
            cs.setString(2, (String) params[1]);    //Utente
            cs.setDate(3, (Date) params[2]);        //DataRestituzione

            cs.execute();


            System.out.println("fine procedure registra-prestito_utente");
        } catch (SQLException e) {
            throw new DAOException("Prestito non correttamente inserito: " + e.getMessage());
        }

        //String Copia,LocalDate DataPrestito,String Utente,int DurataConsultazioneEspressa

        return new PrestitoUtente((String)params[0], ((Date)params[2]).toLocalDate(),(String)params[1],1);//valori messi a caso, sistemare
    }
}
