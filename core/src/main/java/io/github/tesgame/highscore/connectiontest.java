package io.github.tesgame.highscore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
