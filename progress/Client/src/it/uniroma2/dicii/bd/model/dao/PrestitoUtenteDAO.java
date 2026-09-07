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

            System.out.println("*** La copia si trova sul ripiano "+cs.getInt(5)+", scaffale "+cs.getInt(6)+" ***");
        } catch (SQLException e) {
            throw new DAOException("Prestito non correttamente registrato: " + e.getMessage());
        }

        return new PrestitoUtente((String)params[0], ((Date)params[1]).toLocalDate(),(String)params[2],(int)params[3]);
    }

    public void restituzioneCopiaUtente(Object... params) throws DAOException {

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call restituzioneCopiaUtente(?,?)}");
            cs.setString(1, (String) params[0]);    //Copia
            cs.setDate(2, (Date) params[1]);        //DataRestituzione

            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Restituzione copia non correttamente avvenuta: " + e.getMessage());
        }

    }
}
