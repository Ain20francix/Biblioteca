package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.PrestitoUtente;
import it.uniroma2.dicii.bd.model.domain.Trasferimento;
import it.uniroma2.dicii.bd.model.domain.Utente;

import java.sql.*;
import java.time.LocalDate;

public class TrasferimentoDAO implements GenericProcedureDAO<Trasferimento>{

    @Override
    public Trasferimento execute(Object... params) throws DAOException {

        try {

            /*
            * CREATE PROCEDURE `trasferimentoCopia` (in var_Biblioteca CHAR(100),in var_Libro CHAR(17),in var_Copia CHAR(4),in var_NumeroRipiano TINYINT,in var_NumeroScaffale TINYINT,in var_DataCessione DATE,in var_Stato ENUM('Prestata a','Prestata da'))
             */
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call trasferimentoCopia(?,?,?,?,?,?,?)}"); //Copia,DataCessione,Biblioteca,Stato
            cs.setString(1, (String) params[0]);    //var_Biblioteca
            cs.setString(2, (String) params[1]);    //var_ISBN
            cs.setString(3, (String) params[2]);        //var_Copia
            cs.setInt(4, (int) Integer.parseInt((String) params[3]));    //var_NumeroRipiano
            cs.setInt(5, (int) Integer.parseInt((String) params[4]));    //var_NumeroScaffale
            cs.setDate(6, (Date) params[5]);    //var_DataCessione
            cs.setString(7, (String) params[6]);    //var_Stato


            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Trasferimento non correttamente inserito: " + e.getMessage());
        }
        //(String Copia,LocalDate DataCessione,String Biblioteca,LocalDate DataRestituzione,String Stato)
        return new Trasferimento((String) params[2],((Date)params[5]).toLocalDate(),(String) params[0],null,(String) params[6]);
    }

    public void restituzioneCopiaTrasferita(Object... params) throws DAOException {

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call restituzioneCopiaTrasferita(?,?,?)}");
            cs.setString(1, (String) params[0]);    //Copia
            cs.setDate(2, (Date) params[1]);        //DataRestituzione
            cs.setString(3,(String) params[2]);     //Stato
            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("restituzioneCopiaTrasferita non correttamente avvenuta: " + e.getMessage());
        }

        //String Copia,LocalDate DataPrestito,String Utente,int DurataConsultazioneEspressa

        //return new PrestitoUtente((String)params[0], ((Date)params[2]).toLocalDate(),(String)params[1],1);//valori messi a caso, sistemare
    }

}
