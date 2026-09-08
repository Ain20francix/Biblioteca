package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.Copia;
import it.uniroma2.dicii.bd.model.domain.StampaResultSet;

import java.sql.*;

public class CopieDAO implements GenericProcedureDAO<Copia> {

    @Override
    public Copia execute(Object... params) throws DAOException {

        //CREATE PROCEDURE `inserisciCopia` (in var_Etichetta CHAR(4),in var_CodiceLibro CHAR(17),in var_NumeroRipiano TINYINT,in var_NumeroScaffale TINYINT)
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call inserisciCopia(?,?,?,?)}");
            cs.setString(1, (String) params[0]);
            cs.setString(2, (String) params[1]);
            cs.setInt(3, (int) Integer.parseInt((String) params[2]));
            cs.setInt(4, (int) Integer.parseInt((String) params[3]));
            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Errore inserimento copia: " + e.getMessage());
        }

        return new Copia((String) params[0], (String) params[1], "Disponibile", (int) Integer.parseInt((String) params[2]), (int) Integer.parseInt((String) params[2]));
    }

    public void listaCopie() throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call listaCopie()}");
            boolean status = cs.execute();

            if (status) {
                ResultSet rs = cs.getResultSet();
                StampaResultSet.printResultsTable(rs,System.out);

            }

        } catch (SQLException e) {
            throw new DAOException("Errore lista copie: " + e.getMessage());
        }
    }

    public void reportCopieNonRestituite() throws DAOException {
        try {

            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call reportCopieNonRestituite()}");
            boolean status = cs.execute();

            if (status) {
                ResultSet rs = cs.getResultSet();
                StampaResultSet.printResultsTable(rs,System.out);

            }

        } catch (SQLException e) {
            throw new DAOException("Errore report copie non restituite: " + e.getMessage());
        }
    }

    public void reportCopieTrasferite() throws DAOException {
        try {

            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call reportCopieTrasferite()}");
            boolean status = cs.execute();

            if (status) {
                ResultSet rs = cs.getResultSet();
                StampaResultSet.printResultsTable(rs,System.out);

            }

        } catch (SQLException e) {
            throw new DAOException("Errore report copie trasferite: " + e.getMessage());
        }
    }

    public void cambiaPosizione(Object... params) throws DAOException {
        try {

            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call cambiaPosizione(?,?,?)}");
            cs.setString(1, (String)params[0]);
            cs.setInt(2, (int)params[1]);
            cs.setInt(3, (int)params[2]);
            boolean status = cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Errore posizione copia invariata: " + e.getMessage());
        }
    }

    public void cercaCopia(Object... params) throws DAOException {
        try {

            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call cercaCopia(?)}");
            cs.setString(1, (String)params[0]);
            boolean status = cs.execute();

            if (status) {
                ResultSet rs = cs.getResultSet();
                StampaResultSet.printResultsTable(rs,System.out);
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nella ricerca di una copia: " + e.getMessage());
        }
    }

    public void dismissione() throws DAOException {
        try {

            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call dismissione()}");
            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Errore dismissione fallita: " + e.getMessage());
        }
    }
}


