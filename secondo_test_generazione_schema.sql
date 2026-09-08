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
    Dismissione BOOLEAN NOT NULL DEFAULT FALSE,
    DataImmissione DATE NOT NULL,
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
    Stato ENUM('Disponibile','Prestata') NOT NULL,
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
    Copia CHAR(4) NOT NULL,
    DataCessione DATE NOT NULL,
    Biblioteca CHAR(100) NOT NULL,
    DataRestituzione DATE DEFAULT NULL,
    Stato ENUM('Prestata a','Prestata da') NOT NULL,
    PRIMARY KEY(Copia,DataCessione,Biblioteca),
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
-- procedure inserisciUtente
-- -----------------------------------------------------


USE `biblioteca`;
DROP procedure IF EXISTS `biblioteca`.`inserisciUtente`;

DELIMITER $$
USE `biblioteca`$$
CREATE PROCEDURE `inserisciUtente` (in var_CF CHAR(16),in var_Nome CHAR(30),in var_Cognome CHAR(30),in var_Sesso ENUM ('Uomo','Donna','Non binario','Preferisco non specificare'),in var_DataNascita DATE,IN var_LuogoNascita CHAR(40),in var_Residenza CHAR(40),in var_MezzoPreferito ENUM ('Email','Cellulare','Telefono di casa'),in var_contatto CHAR(100))
BEGIN
    -- registrazione utente
    INSERT INTO `Utente` (`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`,`MezzoPreferito`) VALUES (var_CF, var_Nome, var_Cognome, var_Sesso, var_DataNascita, var_LuogoNascita, var_Residenza, var_MezzoPreferito);

    -- Registrazione contatto
    INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES (var_MezzoPreferito,var_contatto,var_CF);



END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure registraPrestitoUtente
-- -----------------------------------------------------


-- bisogna restituire al termine della procedure tramite dei parametri out la posizione della copia e poi mettere tale posizione a NULL

USE `biblioteca`;
DROP procedure IF EXISTS `biblioteca`.`registraPrestitoUtente`;

DELIMITER $$
USE `biblioteca`$$

CREATE PROCEDURE `registraPrestitoUtente` (in var_Copia CHAR(4),in var_DataPrestito DATE,in var_Utente CHAR(16),in var_DurataConsultazioneEspressa ENUM ('1','2','3'),out var_NumeroRipiano TINYINT,out var_NumeroScaffale TINYINT)
BEGIN
    INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES (var_Copia,var_DataPrestito,var_Utente,NULL,var_DurataConsultazioneEspressa);

    -- seleziono il ripiano e lo scaffale di dove si trova la copia per restituirla al bibliotecario

    SELECT NumeroRipiano,NumeroScaffale INTO var_NumeroRipiano,var_NumeroScaffale FROM `Copia` WHERE Etichetta=var_Copia;

    -- poi aggiorniamo lo stato della relativa copia e impostiamo la posizione a NULL

    UPDATE `Copia` SET `Stato`='Prestata',`NumeroRipiano`=NULL,`NumeroScaffale`=NULL WHERE `Etichetta`= var_Copia;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure restituzioneCopiaUtente
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `restituzioneCopiaUtente`;

DELIMITER $$

CREATE PROCEDURE `restituzioneCopiaUtente` (in var_Copia CHAR(4),in var_DataRestituzione DATE)
BEGIN
    UPDATE `PrestitoUtente` SET `DataRestituzione` = var_DataRestituzione WHERE `Copia` = var_Copia AND `DataRestituzione` IS NULL;

    -- poi aggiorno lo stato della copia per renderla disponibile
    UPDATE `Copia` SET `Stato` = 'Disponibile' WHERE `Etichetta` = var_Copia;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure inserisciCopia
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `inserisciCopia`;

DELIMITER $$

CREATE PROCEDURE `inserisciCopia` (in var_Etichetta CHAR(4),in var_CodiceLibro CHAR(17),in var_NumeroRipiano TINYINT,in var_NumeroScaffale TINYINT)
BEGIN

    INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES (var_Etichetta,var_CodiceLibro,'Disponibile',var_NumeroRipiano,var_NumeroScaffale);

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure inserisciLibro
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `inserisciLibro`;

DELIMITER $$

CREATE PROCEDURE `inserisciLibro` (in var_ISBN CHAR(17),in var_Titolo CHAR(50),in var_CaseEditrice CHAR(40),in var_dataImmissione DATE,in var_Genere ENUM ('Biografia', 'Autobiografia','Romanzo storico', 'Giallo', 'Thriller' , 'Azione' , 'Fantascienza', 'Fantasy', 'Horror' , 'Romanzo di formazione' , 'Romanzo Rosa', 'Umoristico'),in var_NomeAutore CHAR(30),in var_CognomeAutore CHAR(30))
BEGIN

    -- inserimento dati libro

    INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES (var_ISBN,var_Titolo,var_CaseEditrice,var_dataImmissione,var_Genere);

    -- inserimento dati hascritto

    IF NOT EXISTS (SELECT 1 FROM `Autore` WHERE `Nome`=var_NomeAutore AND `Cognome`=var_CognomeAutore) THEN
        INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES (var_NomeAutore,var_CognomeAutore);
    END IF;

    INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES (var_ISBN,var_NomeAutore,var_CognomeAutore);

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure trasferimentoCopia
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `trasferimentoCopia`;

DELIMITER $$

CREATE PROCEDURE `trasferimentoCopia` (in var_Copia CHAR(4),in var_DataCessione DATE,in var_Biblioteca CHAR(100),in var_Stato ENUM('Prestata a','Prestata da'))
BEGIN

    DECLARE var_stato_copia CHAR(30);
    DECLARE var_Biblioteca_origine CHAR(100);

    -- controllo che la biblioteca di destinazione non sia la stessa che ha prestato la copia
    SELECT `Biblioteca` INTO var_Biblioteca_origine FROM `Trasferimenti` WHERE `Copia`=var_Copia AND `Stato`='Prestata da';


    IF var_Biblioteca = var_Biblioteca_origine THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: la biblioteca di destinazione risulta la medesima dalla quale proviene la copia che si sta cercando di trasferire.';
    END IF;

    -- controllo che la copia da trasferire esista e non sia stata dismessa
    IF NOT EXISTS (SELECT 1 FROM `Copia`,`Libro` WHERE `Etichetta` = var_Copia AND `Copia`.`CodiceLibro` AND `Libro`.`Dismissione`=FALSE) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: la copia non esiste.';
    END IF;

    -- aggiornamento stato copia
    IF var_Stato = 'Prestata a' THEN
            -- controlliamo che la copia che si vuole trasferire sia disponibile
            SELECT `Stato` INTO var_stato_copia FROM `Copia` WHERE `Etichetta` = var_Copia;
            IF var_stato_copia != 'Disponibile' THEN
                SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Errore Trigger: copia non disponibile per il trasferimento.';
            END IF;

            UPDATE `Copia` SET `Stato` = 'Prestata',`NumeroRipiano`= NULL,`NumeroScaffale`= NULL WHERE `Etichetta` = var_Copia;
    ELSEIF var_Stato != 'Prestata da' THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Errore Trigger: tipo di trasferimento non supportato.';
    END IF;

    -- registrazione trasferimento
    INSERT INTO `Trasferimenti`(`Copia`, `DataCessione`, `Biblioteca`, `DataRestituzione`, `Stato`) VALUES (var_Copia,var_DataCessione,var_Biblioteca,NULL,var_Stato);

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure reportCopieNonRestituite
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `reportCopieNonRestituite`;

DELIMITER $$

CREATE PROCEDURE `reportCopieNonRestituite` ()
BEGIN

    SELECT `PrestitoUtente`.`Copia`,Utente.CF,Utente.Nome,Utente.Cognome,Contatto.Tipo,Contatto.Valore FROM `PrestitoUtente`,Utente,Contatto WHERE PrestitoUtente.Utente=Utente.CF AND Utente.CF=Contatto.Utente AND PrestitoUtente.DataRestituzione IS NULL;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure reportCopieTrasferite
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `reportCopieTrasferite`;

DELIMITER $$

CREATE PROCEDURE `reportCopieTrasferite` ()
BEGIN

    SELECT Trasferimenti.Copia,Trasferimenti.DataCessione,COALESCE(Trasferimenti.DataRestituzione,'Non restituita') as DataRestituzione,Trasferimenti.Stato,Biblioteca.Indirizzo,Biblioteca.Indirizzo FROM `Trasferimenti`,Biblioteca WHERE Trasferimenti.Biblioteca=Biblioteca.Indirizzo;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure restituzioneCopiaTrasferita
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `restituzioneCopiaTrasferita`;

DELIMITER $$

CREATE PROCEDURE `restituzioneCopiaTrasferita` (in var_Copia CHAR(4), in var_DataRestituzione DATE, in var_Stato ENUM('Prestata a','Prestata da'))
BEGIN

    -- controlliamo che la copia sia effettivamente stata prestata ad/da una biblitoeca esterna
    IF NOT EXISTS (SELECT 1 FROM `Trasferimenti` WHERE `Trasferimenti`.`Copia`=var_Copia AND `Trasferimenti`.`Stato`=var_Stato) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: non esiste un trasferimento relativo a tale copia.';
    END IF;

    -- troviamo lo stato della copia

    -- controlliamo che non ci sia un prestito utente in corso con tale copia
    IF EXISTS (SELECT 1 FROM `PrestitoUtente`, `Trasferimenti` WHERE `PrestitoUtente`.`Copia`=`Trasferimenti`.`Copia` AND `PrestitoUtente`.`Copia`=var_Copia AND `Trasferimenti`.`Stato`=var_Stato AND `PrestitoUtente`.`DataRestituzione` IS NULL) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: risultata un prestito utente in corso associato a tale copia.';
    END IF;

    -- aggiornamento stato copia trasferita come restituita
    UPDATE `Trasferimenti` SET `DataRestituzione`=var_DataRestituzione WHERE `Copia`=var_Copia AND `Stato`=var_Stato;

    -- aggiornamento stato copia
    IF var_Stato = 'Prestata a' THEN
        UPDATE `Copia` SET `Stato`='Disponibile' WHERE `Etichetta`=var_Copia; -- la copia viene segnata come prestabile
    ELSE
        UPDATE `Copia` SET `Stato`='Prestata' WHERE `Etichetta`=var_Copia; -- copia restituita alla biblioteca di appartenenza
    END IF;


END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure cercaCopia
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `cercaCopia`;

DELIMITER $$

CREATE PROCEDURE `cercaCopia` (in var_ISBN CHAR(17))
BEGIN

    -- controlliamo che esista una copia disponibile del libro richiesto

    SELECT `Etichetta`,COALESCE(`NumeroRipiano`,'Non Disponibile') AS NumeroRipiano,COALESCE(`NumeroScaffale`,'Non Disponibile') AS NumeroScaffale FROM `Copia`,`Libro` WHERE `Copia`.`CodiceLibro`=`Libro`.`ISBN` AND `Libro`.`Dismissione`=FALSE AND Stato='Disponibile' AND Copia.CodiceLibro=var_ISBN;


END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure cambiaPosizione
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `cambiaPosizione`;

DELIMITER $$

CREATE PROCEDURE `cambiaPosizione` (in var_Copia CHAR(4), in var_NumeroRipiano TINYINT,in var_NumeroScaffale TINYINT)
BEGIN

    -- controlliamo che la copia sia esistente, disponibile e non dismessa
    IF NOT EXISTS (SELECT 1 FROM `Copia`,`Libro` WHERE `Libro`.`ISBN`=`Copia`.`CodiceLibro` AND `Libro`.`Dismissione`=FALSE AND `Etichetta`=var_Copia AND `Stato`='Disponibile') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: non esiste tale copia.';
    END IF;

    -- aggiorniamo la posizione della copia nella biblioteca
    UPDATE `Copia` SET `NumeroRipiano`=var_NumeroRipiano,`NumeroScaffale`=var_NumeroScaffale WHERE `Etichetta`=var_Copia;


END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure dismissione
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `dismissione`;

DELIMITER $$

CREATE PROCEDURE `dismissione` ()
BEGIN

    -- escludo i libri che magari sono appena stati immessi nel sistema

    UPDATE `Libro` SET `Dismissione` = TRUE WHERE `Dismissione` = FALSE AND `DataImmissione` <= DATE_SUB(CURDATE(), INTERVAL 10 YEAR) AND `ISBN` NOT IN (
          -- seleziono i libri che hanno avuto almeno un prestito negli ultimi 10 anni
          SELECT C.CodiceLibro FROM `PrestitoUtente` P, `Copia` C WHERE P.Copia = C.Etichetta AND P.DataPrestito >= DATE_SUB(CURDATE(), INTERVAL 10 YEAR));


END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure listaCopie
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `listaCopie`;

DELIMITER $$

CREATE PROCEDURE `listaCopie` ()
BEGIN

    SELECT * FROM `Copia`,`Libro` WHERE `Copia`.`CodiceLibro`=`Libro`.`ISBN` AND `Libro`.`Dismissione`=FALSE;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure listaUtenti
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `listaUtenti`;

DELIMITER $$

CREATE PROCEDURE `listaUtenti` ()
BEGIN

    SELECT * FROM `Utente`;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure listaLibri
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `listaLibri`;

DELIMITER $$

CREATE PROCEDURE `listaLibri` ()
BEGIN

    SELECT * FROM `Libro`;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure listaBiblioteche
-- -----------------------------------------------------

USE `biblioteca`;
DROP PROCEDURE IF EXISTS `listaBiblioteche`;

DELIMITER $$

CREATE PROCEDURE `listaBiblioteche` ()
BEGIN

    SELECT `Indirizzo`, `Nome` FROM `Biblioteca`;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Autore`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Mario','Rossi');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Giovanni','Verga');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Italo','Calvino');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Luigi','Pirandello');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Paola','Mastrocola');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Umberto','Eco');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Elsa','Morante');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Cesare','Pavese');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Natalia','Ginzburg');
INSERT INTO `Autore`(`Nome`, `Cognome`) VALUES ('Leonardo','Sciascia');

COMMIT;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Libro`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-81-7525-766-5','La fattoria di zio Tobia','Feltrinelli','2010-05-18','Umoristico');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-82-7525-766-5','Io non ho paura','Feltrinelli','2026-05-18','Thriller');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-84-7525-766-5','Esercito delle cose inutili','Feltrinelli','2025-05-18','Romanzo di formazione');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-04-55555-1','Il nome della rosa','Bompiani','2026-06-30','Romanzo Rosa');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-06-12345-2','Se questo è un uomo','Einaudi','2026-01-11','Romanzo storico');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-17-98765-3','Il barone rampante','Mondadori','2026-12-18','Fantasy');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-07-88888-4','Così parlò Bellavista','Feltrinelli','2026-09-25','Autobiografia');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-11-11111-5','La solitudine dei numeri primi','Mondadori','2020-04-18','Fantascienza');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-06-22222-6','Pastorale americana','Einaudi','2008-05-18','Romanzo di formazione');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-04-33333-7','Cent’anni di solitudine','Mondadori','2013-06-08','Giallo');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `DataImmissione`, `Genere`) VALUES ('978-88-07-44444-8','Norwegian Wood','Feltrinelli','2014-03-19','Azione');

COMMIT;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`HaScritto`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-81-7525-766-5','Italo','Calvino');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-82-7525-766-5','Paola','Mastrocola');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-84-7525-766-5','Paola','Mastrocola');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-04-55555-1','Mario','Rossi');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-06-12345-2','Natalia','Ginzburg');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-17-98765-3','Cesare','Pavese');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-07-88888-4','Giovanni','Verga');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-11-11111-5','Leonardo','Sciascia');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-06-22222-6','Leonardo','Sciascia');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-04-33333-7','Umberto','Eco');
INSERT INTO `HaScritto`(`CodiceLibro`, `NomeAutore`, `CognomeAutore`) VALUES ('978-88-07-44444-8','Luigi','Pirandello');

COMMIT;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Copia`
-- -----------------------------------------------------

START TRANSACTION;

INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0001','978-81-7525-766-5','Disponibile','3','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0002','978-81-7525-766-5','Disponibile','3','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0003','978-81-7525-766-5','Disponibile','3','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0004','978-88-04-55555-1','Prestata',NULL,NULL);
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0005','978-88-06-12345-2','Prestata',NULL,NULL);
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0006','978-88-17-98765-3','Disponibile','4','3');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0007','978-88-07-88888-4','Disponibile','1','5');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0008','978-88-11-11111-5','Prestata',NULL,NULL);
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0009','978-88-06-22222-6','Disponibile','5','1');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0010','978-88-04-33333-7','Disponibile','2','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0011','978-88-07-44444-8','Prestata',NULL,NULL);
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0012','978-88-07-44444-8','Disponibile','3','6');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0013','978-88-07-44444-8','Disponibile','3','6');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0014','978-88-07-44444-8','Disponibile','3','6');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0015','978-88-07-44444-8','Disponibile','3','6');

COMMIT;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Utente`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('FGGNML04J47A788H','Giuliana','Cristella','Donna','1998-05-15','Roma','Via Paperino','Email');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('HSSAHT99D66G432L','Kai','Charon','Non binario','1994-02-11','Roma','Via Castagneto','Email');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('AGSRWQ78G56D211H','Alessandro','Antonelli','Uomo','2006-01-22','Milano','Via Paperino','Cellulare');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('RSSMRA85M01F205Z','Mario','Rossi','Uomo','1985-08-01','Palermo','Via Libertà 12','Cellulare');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('BNCGLI90C42Z100W','Giulia','Bianchi','Donna','1990-03-02','Torino','Corso Francia 45','Telefono di casa');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('VRDLCU92P14F839K','Luca','Verdi','Non binario','1992-09-14','Napoli','Via Toledo 88','Email');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('NRMSFN01A41Z133Y','Stefania','Neri','Donna','2001-01-01','Bologna','Via Zamboni 5','Telefono di casa');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('CLTMRK88T25Z110X','Mark','Giaquine','Preferisco non specificare','1988-12-25','Firenze','Via Cavour 22','Cellulare');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('FRRMRC95L50H501J','Marco','Ferrari','Uomo','1995-07-10','Roma','Via Nazionale 10','Telefono di casa');
INSERT INTO `Utente`(`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`, `MezzoPreferito`) VALUES ('SMTSRA00E65Z100L','Sara','Valenza','Non binario','2000-05-25','Venezia','Cannaregio 1234','Cellulare');

COMMIT;


-- -----------------------------------------------------
-- Data for table `biblioteca`.`Contatto`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Email','giuliana98@gmail.com','FGGNML04J47A788H');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Email','kai_secret@libero.it','HSSAHT99D66G432L');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Cellulare','3326498745','AGSRWQ78G56D211H');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Cellulare','3425689444','BNCGLI90C42Z100W');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Telefono di casa','0648554879','BNCGLI90C42Z100W');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Email','lucasgreen@yahoo.com','VRDLCU92P14F839K');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Telefono di casa','0652889136','NRMSFN01A41Z133Y');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Cellulare','3365951424','CLTMRK88T25Z110X');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Telefono di casa','0678126598','FRRMRC95L50H501J');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Cellulare','3281556774','SMTSRA00E65Z100L');
INSERT INTO `Contatto`(`Tipo`, `Valore`, `Utente`) VALUES ('Cellulare','3263636554','RSSMRA85M01F205Z');

COMMIT;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`PrestitoUtente`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES ('0004','2026-05-19','NRMSFN01A41Z133Y',NULL,'1');
INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES ('0005','2026-07-14','SMTSRA00E65Z100L','2026-08-28','3');
INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES ('0011','2015-07-14','AGSRWQ78G56D211H',NULL,'2');
INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES ('0001','2010-07-14','AGSRWQ78G56D211H','2010-08-12','2');

COMMIT;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Biblioteca`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via Giuseppe','Feltrinelli','9');
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via Castani','Mondadori','8');
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via Zazza','Giunti',NULL);
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via del Babuino','Mondadori',NULL);
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Largo del Pallaro','Mondadori',11);
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via di Propaganda','Feltrinelli',NULL);
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via Titta Scarpetta','Giunti','9');
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via dei Tre Pupazzi','Biblioteca Vallicelliana','9');
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Viale Angelico','Biblioteca Angelica','8');
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via Matteotti','Giunti',NULL);
INSERT INTO `Biblioteca`(`Indirizzo`, `Nome`, `OrarioApertura`) VALUES ('Via Roma','Feltrinelli','9');

COMMIT;

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Trasferimenti`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `Trasferimenti`(`Copia`, `DataCessione`, `Biblioteca`, `DataRestituzione`, `Stato`) VALUES ('0008','2024-08-16','Via dei Tre Pupazzi',NULL,'Prestata a');
INSERT INTO `Trasferimenti`(`Copia`, `DataCessione`, `Biblioteca`, `DataRestituzione`, `Stato`) VALUES ('0011','2015-01-02','Viale Angelico',NULL,'Prestata da');

COMMIT;

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
-- Trigger per registraPrestitoUtente
-- -----------------------------------------------------

USE `biblioteca`;
DROP TRIGGER IF EXISTS `biblioteca`.`before_registraPrestitoUtente`;

DELIMITER $$

CREATE TRIGGER `biblioteca`.`before_registraPrestitoUtente` BEFORE INSERT ON `PrestitoUtente` FOR EACH ROW
BEGIN

    -- controlliamo che la copia esista
    IF NOT EXISTS (SELECT 1 FROM `Copia` WHERE `Etichetta` = NEW.Copia) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: la copia non esiste.';
    END IF;
    -- controlliamo che la copia sia disponibile
    IF NOT EXISTS (SELECT 1 FROM `Copia` WHERE `Etichetta` = NEW.Copia AND `Stato`= 'Disponibile') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: la copia risulta non disponibile.';
    END IF;

    -- controlliamo che l'utente esista
    IF NOT EXISTS (SELECT 1 FROM `Utente` WHERE `CF` = NEW.Utente) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: Il codice fiscale utente inserito, non esiste.';
    END IF;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- Trigger per restituzioneCopiaUtente
-- -----------------------------------------------------

USE `biblioteca`;
DROP TRIGGER IF EXISTS `biblioteca`.`before_restituzioneCopiaUtente`;

DELIMITER $$

CREATE TRIGGER `biblioteca`.`before_restituzioneCopiaUtente` BEFORE UPDATE ON `PrestitoUtente` FOR EACH ROW
BEGIN
    -- controlliamo che la copia esista e sia disponibile
    IF NOT EXISTS (SELECT 1 FROM `PrestitoUtente` WHERE `Copia` = NEW.Copia AND `Utente`= NEW.Utente AND DataRestituzione IS NULL) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: prestito inesistente.';
    END IF;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- Trigger per inserisciCopia
-- -----------------------------------------------------

USE `biblioteca`;
DROP TRIGGER IF EXISTS `biblioteca`.`before_inserisciCopia`;

DELIMITER $$

CREATE TRIGGER `biblioteca`.`before_inserisciCopia` BEFORE INSERT ON `Copia` FOR EACH ROW
BEGIN
    -- controlliamo che la copia che si vuole inserire non esista ancora
    IF EXISTS (SELECT 1 FROM `Copia` WHERE `Etichetta` = NEW.Etichetta) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: copia esistente.';
    END IF;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- Trigger per cambiaPosizione
-- -----------------------------------------------------

/*USE `biblioteca`;
DROP TRIGGER IF EXISTS `biblioteca`.`before_cambiaPosizione`;

DELIMITER $$

CREATE TRIGGER `biblioteca`.`before_cambiaPosizione` BEFORE UPDATE ON `Copia` FOR EACH ROW
BEGIN

    DECLARE var_Stato ENUM('Disponibile','Prestata');

    SELECT `Stato` INTO var_Stato FROM `Copia` WHERE `Etichetta`=NEW.`Etichetta`;

    -- controlliamo che la copia di cui si voglia cambiare la posizione non sia in prestito/trasferita
    IF var_Stato != 'Disponibile' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: la copia risulta prestata/trasferita';
    END IF;

END$$

DELIMITER ;*/

-- -----------------------------------------------------
-- Trigger per regole aziendali
-- -----------------------------------------------------

USE `biblioteca`;
DROP TRIGGER IF EXISTS `biblioteca`.`massimoCopiePrestabili`;

DELIMITER $$

CREATE TRIGGER `biblioteca`.`massimoCopiePrestabili` BEFORE INSERT ON `PrestitoUtente` FOR EACH ROW
BEGIN

    DECLARE var_CodiceLibro CHAR(17);
    DECLARE var_numeroCopie TINYINT;

    -- preleviamo il codice libro associato alla copia che sta cercando di essere prestata

    SELECT `CodiceLibro` INTO var_CodiceLibro FROM `Copia` WHERE `Etichetta`=NEW.Copia;

    -- controlliamo che l'utente non stia cercando di prendere nello stesso giorno una quarta copia di uno stesso libro'

    SELECT COUNT(*) INTO var_numeroCopie FROM `PrestitoUtente`,Copia,Libro WHERE PrestitoUtente.Copia=Copia.Etichetta AND Copia.CodiceLibro=Libro.ISBN AND Copia.CodiceLibro=var_CodiceLibro AND PrestitoUtente.DataPrestito=NEW.DataPrestito GROUP BY PrestitoUtente.DataPrestito;

    IF var_numeroCopie=3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: si sta cercando di effettuare un prestito di una quarta copia dello stesso libro.';
    END IF;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- Users and privileges
-- -----------------------------------------------------

SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'login'@'localhost';
 DROP USER 'login'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'login'@'localhost' IDENTIFIED BY 'login';

GRANT EXECUTE ON procedure `biblioteca`.`login` TO 'login'@'localhost';
SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'bibliotecario'@'localhost';
 DROP USER 'bibliotecario'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'bibliotecario'@'localhost' IDENTIFIED BY 'bibliotecario';

GRANT EXECUTE ON procedure `biblioteca`.`login` TO 'bibliotecario'@'localhost';
SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'responsabile'@'localhost';
 DROP USER 'responsabile'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'responsabile'@'localhost' IDENTIFIED BY 'responsabile';
GRANT EXECUTE ON procedure `biblioteca`.`login` TO 'responsabile'@'localhost';


SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'amministratore'@'localhost'; -- CANCELLARE ASSOLUTAMENTE QUESTA RIGA
 DROP USER 'amministratore'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'amministratore'@'localhost' IDENTIFIED BY 'amministratore';

-- permessi bibliotecario
GRANT EXECUTE ON procedure `biblioteca`.`inserisciUtente` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`registraPrestitoUtente` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`restituzioneCopiaUtente` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`inserisciCopia` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`trasferimentoCopia` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaCopie` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaUtenti` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaLibri` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`restituzioneCopiaTrasferita` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`cambiaPosizione` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`cercaCopia` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaBiblioteche` TO 'bibliotecario'@'localhost';

-- permessi responsabile
GRANT EXECUTE ON procedure `biblioteca`.`inserisciLibro` TO 'responsabile'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`reportCopieNonRestituite` TO 'responsabile'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`reportCopieTrasferite` TO 'responsabile'@'localhost';

-- permessi amministratore
GRANT EXECUTE ON procedure `biblioteca`.`inserisciUtente` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`registraPrestitoUtente` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`restituzioneCopiaUtente` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`inserisciCopia` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`trasferimentoCopia` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaCopie` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaUtenti` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaLibri` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`restituzioneCopiaTrasferita` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`inserisciLibro` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`reportCopieNonRestituite` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaBiblioteche` TO 'amministratore'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`dismissione` TO 'amministratore'@'localhost';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;


-- -----------------------------------------------------
-- Eventi temporizzati
-- -----------------------------------------------------
