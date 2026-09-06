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
-- procedure inserisciUtente
-- -----------------------------------------------------


USE `biblioteca`;
DROP procedure IF EXISTS `biblioteca`.`inserisciUtente`;

DELIMITER $$
USE `biblioteca`$$
CREATE PROCEDURE `inserisciUtente` (in var_CF CHAR(16),in var_Nome CHAR(30),in var_Cognome CHAR(30),in var_Sesso ENUM ('Uomo','Donna','Non binario','Preferisco non specificare'),in var_DataNascita DATE,IN var_LuogoNascita CHAR(40),in var_Residenza CHAR(40),in var_MezzoPreferito ENUM ('Email','Cellulare','Telefono di casa'))
BEGIN
    insert into `Utente` (`CF`, `Nome`, `Cognome`, `Sesso`, `DataNascita`, `LuogoNascita`, `Residenza`,`MezzoPreferito`)
                values (var_CF, var_Nome, var_Cognome, var_Sesso, var_DataNascita, var_LuogoNascita, var_Residenza, var_MezzoPreferito);
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

CREATE PROCEDURE `inserisciLibro` (in var_ISBN CHAR(17),in var_Titolo CHAR(50),in var_CaseEditrice CHAR(40),in var_Genere ENUM ('Biografia', 'Autobiografia','Romanzo storico', 'Giallo', 'Thriller' , 'Azione' , 'Fantascienza', 'Fantasy', 'Horror' , 'Romanzo di formazione' , 'Romanzo Rosa', 'Umoristico'),in var_NomeAutore CHAR(30),in var_CognomeAutore CHAR(30))
BEGIN

    -- inserimento dati libro

    INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES (var_ISBN,var_Titolo,var_CaseEditrice,var_Genere);

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

    -- controllo che la copia da trasferire esista
    IF NOT EXISTS (SELECT 1 FROM `Copia` WHERE `Etichetta` = var_Copia) THEN
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

    SELECT `Etichetta` FROM `Copia`, `PrestitoUtente` WHERE `Copia`.`Etichetta`=`PrestitoUtente`.`Copia` AND `PrestitoUtente`.`DataRestituzione` IS NULL;

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

    SELECT * FROM `Copia`;

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
-- Data for table `biblioteca`.`Autore`
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Libro`
-- -----------------------------------------------------

INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-81-7525-766-5','La fattoria di zio Tobia','Feltrinelli','Umoristico');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-82-7525-766-5','Io non ho paura','Feltrinelli','Thriller');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-84-7525-766-5','Esercito delle cose inutili','Feltrinelli','Romanzo di formazione');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-04-55555-1','Il nome della rosa','Bompiani','Romanzo Rosa');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-06-12345-2','Se questo è un uomo','Einaudi','Romanzo storico');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-17-98765-3','Il barone rampante','Mondadori','Fantasy');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-07-88888-4','Così parlò Bellavista','Feltrinelli','Autobiografia');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-11-11111-5','La solitudine dei numeri primi','Mondadori','Fantascienza');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-06-22222-6','Pastorale americana','Einaudi','Romanzo di formazione');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-04-33333-7','Cent’anni di solitudine','Mondadori','Giallo');
INSERT INTO `Libro`(`ISBN`, `Titolo`, `CasaEditrice`, `Genere`) VALUES ('978-88-07-44444-8','Norwegian Wood','Feltrinelli','Azione');

-- -----------------------------------------------------
-- Data for table `biblioteca`.`HaScritto`
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Copia`
-- -----------------------------------------------------

INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0001','978-81-7525-766-5','Disponibile','3','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0002','978-81-7525-766-5','Disponibile','3','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0003','978-81-7525-766-5','Disponibile','3','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0004','978-88-04-55555-1','Prestata','1','2');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0005','978-88-06-12345-2','Prestata','2','1');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0006','978-88-17-98765-3','Disponibile','4','3');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0007','978-88-07-88888-4','Disponibile','1','5');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0008','978-88-11-11111-5','Prestata','3','2');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0009','978-88-06-22222-6','Disponibile','5','1');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0010','978-88-04-33333-7','Disponibile','2','4');
INSERT INTO `Copia`(`Etichetta`, `CodiceLibro`, `Stato`, `NumeroRipiano`, `NumeroScaffale`) VALUES ('0011','978-88-07-44444-8','Prestata','3','6');

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Utente`
-- -----------------------------------------------------

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


-- -----------------------------------------------------
-- Data for table `biblioteca`.`Contatto`
-- -----------------------------------------------------

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

-- -----------------------------------------------------
-- Data for table `biblioteca`.`PrestitoUtente`
-- -----------------------------------------------------

INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES ('0004','2026-05-19','NRMSFN01A41Z133Y',NULL,'1');
INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES ('0005','2026-07-14','SMTSRA00E65Z100L','2026-08-28','3');
INSERT INTO `PrestitoUtente`(`Copia`, `DataPrestito`, `Utente`, `DataRestituzione`, `DurataConsultazioneEspressa`) VALUES ('0011','2026-07-14','AGSRWQ78G56D211H',NULL,'2');

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Biblioteca`
-- -----------------------------------------------------

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

-- -----------------------------------------------------
-- Data for table `biblioteca`.`Trasferimenti`
-- -----------------------------------------------------
INSERT INTO `Trasferimenti`(`Copia`, `DataCessione`, `Biblioteca`, `DataRestituzione`, `Stato`) VALUES ('0008','2024-08-16','Via dei Tre Pupazzi',NULL,'Prestata a');
INSERT INTO `Trasferimenti`(`Copia`, `DataCessione`, `Biblioteca`, `DataRestituzione`, `Stato`) VALUES ('0011','2026-12-02','Viale Angelico',NULL,'Prestata da');
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
-- Trigger per trasferimentoCopia
-- -----------------------------------------------------

/*USE `biblioteca`;
DROP TRIGGER IF EXISTS `biblioteca`.`before_trasferimentoCopia`;

DELIMITER $$

CREATE TRIGGER `biblioteca`.`before_trasferimentoCopia` BEFORE INSERT ON `Trasferimenti` FOR EACH ROW
BEGIN
    -- controlliamo che la copia che si vuole tarsferire esista e sia disponibile
    DECLARE var_stato CHAR(30);

    SELECT `Stato` INTO var_stato FROM `Copia` WHERE `Etichetta` = NEW.Copia;

    -- si considera il fatto che dopo che viene inserita la nuova copia, prima di poter essere registrato il trasferimento
    -- il bibliotecario puo assentarsi, la copia puo essere prestata e dopo viene registrato il trasferimento quindi
    -- il suo stato puo essere sia disponibile sia prestata

    IF var_stato != 'Disponibile' THEN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: copia non disponibile per il trasferimento.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM `Copia` WHERE `Etichetta` = NEW.Copia AND `Stato`= 'Disponibile') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: la copia non esiste.';
    END IF;

    -- controlliamo che la biblioteca esista
    IF NOT EXISTS (SELECT 1 FROM `Biblioteca` WHERE `Indirizzo` = NEW.Biblioteca) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Trigger: indirizzo della biblioteca inserita inesistente.';
    END IF;

END$$

DELIMITER ;*/

-- -----------------------------------------------------
-- Trigger per regole aziendali
-- -----------------------------------------------------

-- da implementare il trigger per evitare che un utente possa prendere in prestito lo stesso giorno più di 3 copie dello stesso libro


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

SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'amministratore'@'localhost'; -- CANCELLARE ASSOLUTAMENTE QUESTA RIGA
 DROP USER 'amministratore'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'amministratore'@'localhost' IDENTIFIED BY 'amministratore';

-- permessi bibliotecario
GRANT SELECT, INSERT, UPDATE, DELETE ON biblioteca.* TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`inserisciUtente` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`registraPrestitoUtente` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`restituzioneCopiaUtente` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`inserisciCopia` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`trasferimentoCopia` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaCopie` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaUtenti` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`listaLibri` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`inserisciLibro` TO 'bibliotecario'@'localhost';
GRANT EXECUTE ON procedure `biblioteca`.`reportCopieNonRestituite` TO 'bibliotecario'@'localhost';

-- permessi amministratore
GRANT SELECT, INSERT, UPDATE, DELETE ON biblioteca.* TO 'amministratore'@'localhost';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

/**/
