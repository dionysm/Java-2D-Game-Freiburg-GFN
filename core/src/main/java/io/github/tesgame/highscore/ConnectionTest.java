package io.github.tesgame.highscore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// OLD MYSQL
/*
public class connectiontest {

    public static void main(String[] args) {
        System.out.println("Starte Test");

        String url = "jdbc:mysql://localhost:3306/highscores";
        String user = "root";
        String password = "";

        try {
            System.out.println("Versuche Verbindung zu DB...");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Verbindung erfolgreich!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Verbindung fehlgeschlagen!");
            e.printStackTrace();
        }

        System.out.println("Test abgeschlossen.");
    }
}
*/
// NEW WITH SQLITE
public class ConnectionTest {

    public static void main(String[] args) {
        System.out.println("Starte Test");

        // SQLite-Datenbank-URL
        String url = "jdbc:sqlite:highscores.db"; // SQLite-Dateipfad
        // Kein Benutzername und Passwort nötig, da file based db

        try {
            System.out.println("Versuche Verbindung zu DB...");
            // Verbindung zur SQLite-Datenbank herstellen
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Verbindung erfolgreich!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Verbindung fehlgeschlagen!");
            e.printStackTrace();
        }

        System.out.println("Test abgeschlossen.");
    }
}
