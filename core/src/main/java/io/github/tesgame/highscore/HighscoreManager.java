package io.github.tesgame.highscore;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HighscoreManager {

    private static final String DB_URL = "jdbc:sqlite:highscores.db";

    public HighscoreManager() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS highscores (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "score INTEGER NOT NULL," +
            "timestamp TEXT NOT NULL" +
            ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Highscore-Tabelle:");
            e.printStackTrace();
        }
    }

    public void saveHighscore(String name, int score) {
        String sql = "INSERT INTO highscores (name, score, timestamp) VALUES (?, ?, ?)";

        // Zeitstempel
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, score);
            stmt.setString(3, timestamp);
            stmt.executeUpdate();
            System.out.println("Highscore wurde erfolgreich gespeichert!");

        } catch (SQLException e) {
            System.out.println("Fehler beim Speichern der Highscore!");
            e.printStackTrace();
        }
    }

    public boolean qualifiesForTop10(int score) {
        String sql = "SELECT score FROM highscores ORDER BY score DESC LIMIT 10";
        List<Integer> topScores = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                topScores.add(rs.getInt("score"));
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der Top 10:");
            e.printStackTrace();
            return false;
        }

        return topScores.size() < 10 || score > topScores.get(topScores.size() - 1);
    }

    //Zugriff auf Highscore aus Menü

    public List<String> getTop10Highscores() {
        String sql = "SELECT name, score, timestamp FROM highscores ORDER BY score DESC LIMIT 10";
        List<String> highscores = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                String time = rs.getString("timestamp");
                highscores.add(name + " - " + score + " Punkte (" + time + ")");
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der Highscores:");
            e.printStackTrace();
        }

        return highscores;
    }
}
