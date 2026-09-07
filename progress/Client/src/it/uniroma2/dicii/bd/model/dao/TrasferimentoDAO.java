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
            //t = new TrasferimentoDAO().execute(valori[0],java.sql.Date.valueOf(LocalDate.now()),valori[1],valori[2]); //Copia,DataCessione,Biblioteca,*NO*DataRestituzione*NO*,Stato
            //(in var_Copia CHAR(4),in var_DataCessione DATE,in var_Biblioteca CHAR(100),in var_Stato ENUM('Prestata a','Prestata da'))
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call trasferimentoCopia(?,?,?,?)}"); //Copia,DataCessione,Biblioteca,Stato
            cs.setString(1, (String) params[0]);    //Copia
            cs.setDate(2, (Date) params[1]);        //DataCessione
            cs.setString(3, (String) params[2]);    //Biblioteca
            cs.setString(4, (String) params[3]);    //Stato
            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Trasferimento non correttamente inserito: " + e.getMessage());
        }

        return new Trasferimento((String) params[0],((Date)params[1]).toLocalDate(),(String) params[2],null,(String) params[3]);
    }

    public void restituzioneCopiaTrasferita(Object... params) throws DAOException {

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call restituzioneCopiaTrasferita(?,?,?)}");
            cs.setString(1, (String) params[0]);    //Copia
            cs.setDate(2, (Date) params[1]);        //DataRestituzione
            cs.setString(3,(String) params[2]);     //Stato
            System.out.println("prima di execute");
            cs.execute();

            System.out.println("fine procedure restituzioneCopiaTrasferita");
        } catch (SQLException e) {
            throw new DAOException("restituzioneCopiaTrasferita non correttamente avvenuta: " + e.getMessage());
        }

        //String Copia,LocalDate DataPrestito,String Utente,int DurataConsultazioneEspressa

        //return new PrestitoUtente((String)params[0], ((Date)params[2]).toLocalDate(),(String)params[1],1);//valori messi a caso, sistemare
    }

}
