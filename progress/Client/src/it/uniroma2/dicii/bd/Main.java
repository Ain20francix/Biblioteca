/**
 * Copyright (C) 2022 Alessandro Pellegrini
 *
 * This file is part of the material for the course Databases at
 * University of Rome Tor Vergata.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this source.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.uniroma2.dicii.bd;

import it.uniroma2.dicii.bd.controller.ApplicationController;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        ApplicationController applicationController = new ApplicationController();
        applicationController.start();
    }
}

/*
* è stato fatto
*
* GRANT SELECT ON mysql.proc TO 'utente'
*
* per ogni ruolo, quando si ricostruisce la connessioen verificare se è necessario.
* Ad ogni modo questo comando permette agli utenti solo di VEDERE le stored procedures e non di eseguirle
* per ESEGUIRLE servono i permessi di EXECUTE su ogni specifica stored procedure
* */