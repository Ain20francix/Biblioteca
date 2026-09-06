package it.uniroma2.dicii.bd.model.dao;

import com.mysql.cj.protocol.Resultset;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.BookingFlight;
import it.uniroma2.dicii.bd.model.domain.BookingList;
import it.uniroma2.dicii.bd.model.domain.Copia;
import it.uniroma2.dicii.bd.model.domain.Passenger;
import it.uniroma2.dicii.bd.model.domain.StampaResultSet;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;

import static it.uniroma2.dicii.bd.model.dao.BookingListProcedureDAO.printResultsTable;

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
                printResultsTable(rs,System.out);

            }

        } catch (SQLException e) {
            throw new DAOException("Errore lista copie: " + e.getMessage());
        }
    }

    public void reportCopieNonRestituite() throws DAOException {
        try {

            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call reportCopieNonRestituite()}");
            System.out.println("prima execute");
            boolean status = cs.execute();
            System.out.println("dopo execute");

            if (status) {
                ResultSet rs = cs.getResultSet();
                printResultsTable(rs,System.out);

            }

        } catch (SQLException e) {
            throw new DAOException("Errore lista copie: " + e.getMessage());
        }
    }
}


