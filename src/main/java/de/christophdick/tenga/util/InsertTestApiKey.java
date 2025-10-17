package de.christophdick.tenga.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * Utility to insert the documented test API key into the database.
 * Run with: mvn exec:java -Dexec.mainClass="de.christophdick.tenga.util.InsertTestApiKey"
 */
public class InsertTestApiKey {

    public static void main(String[] args) {
        // Database connection from .mcp.json
        String url = "jdbc:postgresql://138.199.208.122:5433/postgres";
        String user = "postgres";
        String password = "PDOQUpYK45oZ3mUWp0GlQosGQGcdNCQOo0etkXgutvHUgx4LU6iZU9m3xQECRfy5";

        // Test API key: "test-key-123"
        // BCrypt hash (strength 10) - CORRECTED
        String keyHash = "$2a$10$ZbyK0iQa5DGdB7CeRzo98.7dB3gc.MVyThSPZA80NgHxX9MxrHeKm";

        // First check if key already exists
        String checkSql = "SELECT COUNT(*) FROM api_keys WHERE key_hash = ?";
        String insertSql = "INSERT INTO api_keys (key_hash, created_at) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // Check if key exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, keyHash);
                var rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("✓ Test API key already exists in database!");
                    System.out.println("  API Key: test-key-123");
                    return;
                }
            }

            // Insert new key
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, keyHash);
                insertStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

                int affected = insertStmt.executeUpdate();

                if (affected > 0) {
                    System.out.println("✓ Test API key inserted successfully!");
                    System.out.println("  API Key: test-key-123");
                    System.out.println("  You can now use this key for authentication.");
                } else {
                    System.out.println("⚠ API key was not inserted.");
                }
            }

        } catch (Exception e) {
            System.err.println("✗ Error inserting API key:");
            e.printStackTrace();
        }
    }
}
