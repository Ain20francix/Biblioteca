CREATE DATABASE Biblioteca;

USE Biblioteca;

CREATE TABLE Utente(
    CF varchar(16) PRIMARY KEY NOT NULL,
    Nome varchar(45) DEFAULT NULL,
    Cognome varchar(45) DEFAULT NULL,
    MezzoPreferito ENUM ('Email','Cellulare','Telefono di casa')
);

CREATE TABLE Contatto(
    Valore varchar(45) NOT NULL,
    Tipo ENUM ('Email','Cellulare','Telefono di casa') NOT NULL,
    PRIMARY KEY(Valore,Tipo),
    Utente varchar(16) NOT NULL,
    FOREIGN KEY (Utente) REFERENCES Utente(CF)
    /*Bisogna settare SET sql_mode = 'STRICT_TRANS_TABLES'; per far si che valori diversi da quelli della ENUM possano essere rifiutati*/
);

CREATE TABLE Autore(
    Nome varchar(45) NOT NULL,
    Cognome varchar(45) NOT NULL,
    PRIMARY KEY(Nome,Cognome)
);

CREATE TABLE Libro(
    ISBN VARCHAR(13) PRIMARY KEY NOT NULL,
    Titolo varchar(45) NOT NULL,
    CasaEditrice varchar(45) NOT NULL
);

CREATE TABLE HaScritto(
    CodiceLibro VARCHAR(13),
    NomeAutore varchar(45),
    CognomeAutore varchar(45),
    PRIMARY KEY(CodiceLibro,NomeAutore,CognomeAutore),
    FOREIGN KEY (CodiceLibro) REFERENCES Libro(ISBN),
    FOREIGN KEY (NomeAutore,CognomeAutore) REFERENCES Autore(Nome,Cognome)
);

CREATE TABLE Ripiano(
    Numero int NOT NULL,
    Genere varchar(45) NOT NULL,
    PRIMARY KEY(Numero,Genere)
);

CREATE TABLE Copia(
    Etichetta varchar(45) PRIMARY KEY NOT NULL,
    Stato ENUM('Disponibile','Prestata') NOT NULL,
    Libro VARCHAR(13),
    Ripiano int,
    Scaffale varchar(45),
    FOREIGN KEY (Libro) REFERENCES Libro(ISBN),
    FOREIGN KEY (Ripiano,Scaffale) REFERENCES Ripiano(Numero,Genere)
);

CREATE TABLE BibliotecaEsterna(
    Indirizzo varchar(45) PRIMARY KEY NOT NULL,
    Telefono varchar(45) NOT NULL,
    Nome varchar(45) NOT NULL,
    OrarioApertura TIME DEFAULT NULL,
    OrarioChiusura TIME DEFAULT NULL
);

CREATE TABLE Trasferimenti(
    Copia varchar(45) PRIMARY KEY,
    DataCessione DATE NOT NULL,
    DataRestituzione DATE DEFAULT NULL,
    Stato ENUM('Prestata a','Prestata da'),
    BibliotecaEsterna varchar(45),
    FOREIGN KEY (Copia) REFERENCES Copia(Etichetta),
    FOREIGN KEY (BibliotecaEsterna) REFERENCES BibliotecaEsterna(Indirizzo)
);

CREATE TABLE PrestitoUtente(
    Copia varchar(45),
    DataPrestito DATE,
    Utente varchar(16),
    DataRestituzione DATE DEFAULT NULL,
    DurataConsultazioneEspressa ENUM('1','2','3'),
    PRIMARY KEY(Copia,DataPrestito,Utente),
    FOREIGN KEY (Copia) REFERENCES Copia(Etichetta),
    FOREIGN KEY (Utente) REFERENCES Utente(CF)
);
