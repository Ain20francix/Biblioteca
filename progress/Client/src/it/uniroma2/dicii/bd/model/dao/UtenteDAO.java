package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.BookingFlight;
import it.uniroma2.dicii.bd.model.domain.Copia;
import it.uniroma2.dicii.bd.model.domain.Passenger;
import it.uniroma2.dicii.bd.model.domain.Utente;

import java.sql.*;

import it.uniroma2.dicii.bd.model.domain.StampaResultSet;

public class UtenteDAO implements GenericProcedureDAO<Utente> {

    @Override
    public Utente execute(Object... params) throws DAOException {

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call inserisciUtente(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, (String) params[0]); //CF
            cs.setString(2, (String) params[1]); //Nome
            cs.setString(3, (String) params[2]); //Cognome
            cs.setString(4, (String) params[3]); //Sesso
            cs.setString(5, (String) params[4]); //DataNascita
            cs.setString(6, (String) params[5]); //LuogoNascita
            cs.setString(7, (String) params[6]); //IndirizzoResidenza
            cs.setString(8, (String) params[7]); //MezzoPreferito
            cs.setString(9, (String) params[8]); //Contatto
            cs.execute();
        } catch (SQLException e) {
            throw new DAOException("Utente non registrato: " + e.getMessage());
        }
        return new Utente((String)params[0],(String)params[1],(String)params[2],(String)params[3],(String)params[4],(String)params[5],(String)params[6],(String)params[7]);
    }

    public void listaUtenti() throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call listaUtenti()}");
            boolean status = cs.execute();

            if (status) {
                ResultSet rs = cs.getResultSet();
                StampaResultSet.printResultsTable(rs,System.out);

            }

        } catch (SQLException e) {
            throw new DAOException("Errore lista copie: " + e.getMessage());
        }
    }
}
