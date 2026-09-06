SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

/*Creazione database*/

DROP SCHEMA IF EXISTS `biblioteca` ;
CREATE SCHEMA biblioteca;

USE biblioteca;

/*Creazione tabelle*/
DROP TABLE IF EXISTS `biblioteca`.`Autore`;
CREATE TABLE Autore(
    Nome CHAR(30) NOT NULL,
    Cognome CHAR(30) NOT NULL,
    PRIMARY KEY(Nome,Cognome)
);

DROP TABLE IF EXISTS `biblioteca`.`Libro`;
CREATE TABLE Libro(
    ISBN CHAR(17) PRIMARY KEY NOT NULL,
    Titolo CHAR(50) NOT NULL,
    CasaEditrice CHAR(40) NOT NULL,
    Genere ENUM ('Biografia', 'Autobiografia','Romanzo storico', 'Giallo', 'Thriller' , 'Azione' , 'Fantascienza', 'Fantasy', 'Horror' , 'Romanzo di formazione' , 'Romanzo Rosa', 'Umoristico')
);

DROP TABLE IF EXISTS `biblioteca`.`HaScritto`;
CREATE TABLE HaScritto(
    CodiceLibro CHAR(17) NOT NULL,
    NomeAutore CHAR(30) NOT NULL,
    CognomeAutore CHAR(30) NOT NULL,
    PRIMARY KEY(CodiceLibro,NomeAutore,CognomeAutore),
    FOREIGN KEY (CodiceLibro) REFERENCES Libro(ISBN),
    FOREIGN KEY (NomeAutore,CognomeAutore) REFERENCES Autore(Nome,Cognome)
);

DROP TABLE IF EXISTS `biblioteca`.`Copia`;
CREATE TABLE Copia(
    Etichetta CHAR(4) PRIMARY KEY NOT NULL,
    CodiceLibro CHAR(17) NOT NULL,
    Stato ENUM('Disponibile','Prestata','Trasferita') NOT NULL,
    NumeroRipiano TINYINT UNSIGNED DEFAULT NULL,
    NumeroScaffale TINYINT UNSIGNED DEFAULT NULL,
    FOREIGN KEY (CodiceLibro) REFERENCES Libro(ISBN)
);

DROP TABLE IF EXISTS `biblioteca`.`Utente`;
CREATE TABLE Utente(
    CF CHAR(16) PRIMARY KEY NOT NULL,
    Nome varchar(30) NOT NULL,
    Cognome varchar(30) NOT NULL,
    Sesso ENUM ('Uomo','Donna','Non binario','Preferisco non specificare') NOT NULL,
    DataNascita DATE NOT NULL,
    LuogoNascita CHAR(40) NOT NULL,
    Residenza CHAR(40) NOT NULL,
    MezzoPreferito ENUM ('Email','Cellulare','Telefono di casa') NOT NULL
);

DROP TABLE IF EXISTS `biblioteca`.`Contatto`;
CREATE TABLE Contatto(
    Tipo ENUM ('Email','Cellulare','Telefono di casa') NOT NULL,
    Valore CHAR(100) NOT NULL,
    Utente CHAR(16) NOT NULL,
    PRIMARY KEY(Valore,Utente),
    FOREIGN KEY (Utente) REFERENCES Utente(CF)
    /*Bisogna settare SET sql_mode = 'STRICT_TRANS_TABLES'; per far si che valori diversi da quelli della ENUM possano essere rifiutati*/
);

DROP TABLE IF EXISTS `biblioteca`.`PrestitoUtente`;
CREATE TABLE PrestitoUtente(
    Copia CHAR(4) NOT NULL,
    DataPrestito DATE NOT NULL,
    Utente CHAR(16) NOT NULL,
    DataRestituzione DATE DEFAULT NULL,
    DurataConsultazioneEspressa ENUM('1','2','3') NOT NULL,
    PRIMARY KEY(Copia,DataPrestito,Utente),
    FOREIGN KEY (Copia) REFERENCES Copia(Etichetta),
    FOREIGN KEY (Utente) REFERENCES Utente(CF)
);

DROP TABLE IF EXISTS `biblioteca`.`Biblioteca`;
CREATE TABLE Biblioteca(
    Indirizzo CHAR(100) PRIMARY KEY NOT NULL,
    Nome CHAR(100) NOT NULL,
    OrarioApertura TIME DEFAULT NULL
);

DROP TABLE IF EXISTS `biblioteca`.`Trasferimenti`;
CREATE TABLE Trasferimenti(
    Copia CHAR(4) PRIMARY KEY NOT NULL,
    DataCessione DATE NOT NULL,
    Biblioteca CHAR(100) NOT NULL,
    DataRestituzione DATE DEFAULT NULL,
    Stato ENUM('Prestata a','Prestata da') NOT NULL,
    FOREIGN KEY (Copia) REFERENCES Copia(Etichetta),
    FOREIGN KEY (Biblioteca) REFERENCES Biblioteca(Indirizzo)
);

/*Creazione ruoli di chi si logga*/

DROP TABLE IF EXISTS `biblioteca`.`Ruoli` ;

CREATE TABLE Ruoli(
  username CHAR(100) PRIMARY KEY NOT NULL,
  password CHAR(100) NOT NULL,
  ruolo ENUM('amministratore', 'bibliotecario', 'responsabile') NOT NULL
);

-- -----------------------------------------------------
-- procedure login
-- -----------------------------------------------------

USE `biblioteca`;
DROP procedure IF EXISTS `biblioteca`.`login`;

DELIMITER $$
USE `biblioteca`$$
CREATE PROCEDURE `login` (in var_username CHAR(100), in var_pass CHAR(100), out var_role INT)
BEGIN
    declare var_user_role ENUM('amministratore', 'bibliotecario','responsabile');

    select `ruolo` from `Ruoli`
        where `username` = var_username
        and `password` = md5(var_pass)
        into var_user_role;

    -- See the corresponding enum in the client
        if var_user_role = 'amministratore' then
            set var_role = 1;
        elseif var_user_role = 'bibliotecario' then
            set var_role = 2;
        elseif var_user_role = 'responsabile' then
            set var_role = 3;
        else
            set var_role = 4;
        end if;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Ruoli`
-- -----------------------------------------------------
START TRANSACTION;
USE `biblioteca`;
INSERT INTO `Ruoli`(`username`, `password`, `ruolo`) VALUES ('utente1','e792cd9665119b1244e8afcf36fb5f48','amministratore');
INSERT INTO `Ruoli`(`username`, `password`, `ruolo`) VALUES ('utente2','18042a2d9336bf77016b1e21d915bed6','bibliotecario');
INSERT INTO `Ruoli`(`username`, `password`, `ruolo`) VALUES ('utente3','8928363f23ea4502106103c3ff78feef','responsabile');

COMMIT;

-- -----------------------------------------------------
-- Users and privileges
-- -----------------------------------------------------

SET SQL_MODE = '';
GRANT USAGE ON *.* TO login;
 DROP USER login;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'login' IDENTIFIED BY 'login';

GRANT EXECUTE ON procedure `biblioteca`.`login` TO 'login';
SET SQL_MODE = '';
GRANT USAGE ON *.* TO bibliotecario;
 DROP USER bibliotecario;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'bibliotecario' IDENTIFIED BY 'bibliotecario';

SET SQL_MODE = '';
GRANT USAGE ON *.* TO amministratore;
 DROP USER amministratore;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'amministratore' IDENTIFIED BY 'amministratore';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

/**/
