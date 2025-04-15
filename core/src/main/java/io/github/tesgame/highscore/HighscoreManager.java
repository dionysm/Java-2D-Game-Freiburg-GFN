package io.github.tesgame.highscore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HighscoreManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/highscores";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public void saveHighscore(String Name, int Score) {
        String sql = "INSERT INTO highscores (name, score) VALUES (?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Name);
            stmt.setInt(2, Score);
            stmt.executeUpdate();
            System.out.println("Highscore wurde erfolgreich gespeichert!");

        } catch (SQLException e) {
            System.out.println("Fehler beim speichern der Highscore!");
            e.printStackTrace();
        }
    }

    public boolean qualifiesForTop10(int score) {
        String sql = "SELECT score FROM highscores ORDER BY score DESC LIMIT 10";
        List<Integer> topScores = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
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

        if (topScores.size() < 10) {
            return true;
        }

        return score > topScores.get(topScores.size() - 1);
    }


}
