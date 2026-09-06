package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.Copia;
import it.uniroma2.dicii.bd.model.domain.Libro;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static it.uniroma2.dicii.bd.model.dao.BookingListProcedureDAO.printResultsTable;

public class LibroDAO implements GenericProcedureDAO<Libro> {

        @Override
        public Libro execute(Object... params) throws DAOException {

            //CREATE PROCEDURE `inserisciCopia` (in var_Etichetta CHAR(4),in var_CodiceLibro CHAR(17),in var_NumeroRipiano TINYINT,in var_NumeroScaffale TINYINT)
            try {
                Connection conn = ConnectionFactory.getConnection();
                CallableStatement cs = conn.prepareCall("{call inserisciLibro(?,?,?,?,?,?)}");
                //Parametri libro
                cs.setString(1, (String) params[0]);    //ISBN
                cs.setString(2, (String) params[1]);    //Titolo
                cs.setString(3, (String) params[2]);    //CaseEditrice
                cs.setString(4, (String) params[3]);    //Genere
                //Paraemtri autore
                cs.setString(5, (String) params[4]);    //NomeAutore
                cs.setString(6, (String) params[5]);    //CognomeAutore
                cs.execute();

            } catch (SQLException e) {
                throw new DAOException("Errore inserimento libro: " + e.getMessage());
            }

            return new Libro((String) params[0],(String) params[1],(String) params[2],(String) params[3]);
        }

        public void listaLibri() throws DAOException {
            try {
                Connection conn = ConnectionFactory.getConnection();
                CallableStatement cs = conn.prepareCall("{call listaLibri()}");
                boolean status = cs.execute();

                if (status) {
                    ResultSet rs = cs.getResultSet();
                    printResultsTable(rs,System.out);

                }

            } catch (SQLException e) {
                throw new DAOException("Errore lista libri: " + e.getMessage());
            }
        }
}
/*
* ISBN CHAR(17) PRIMARY KEY NOT NULL,
    Titolo CHAR(50) NOT NULL,
    CasaEditrice CHAR(40) NOT NULL,
    Genere ENUM ('Biografia', 'Autobiografia','Romanzo storico', ...)
* */