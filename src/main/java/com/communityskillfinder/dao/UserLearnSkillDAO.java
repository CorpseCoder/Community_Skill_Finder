package com.communityskillfinder.dao;

import com.communityskillfinder.model.UserLearnSkill;
import com.communityskillfinder.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserLearnSkillDAO {

    public boolean addLearnSkill(int userId, int skillId, String priority) throws SQLException {
        String sql = "INSERT INTO user_learn_skills (user_id, skill_id, priority) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE priority = VALUES(priority)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            stmt.setString(3, priority);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<UserLearnSkill> findByUserId(int userId) throws SQLException {
        List<UserLearnSkill> list = new ArrayList<>();
        String sql = "SELECT uls.user_id, uls.skill_id, s.skill_name, s.category, uls.priority " +
                     "FROM user_learn_skills uls " +
                     "JOIN skills s ON uls.skill_id = s.skill_id " +
                     "WHERE uls.user_id = ? " +
                     "ORDER BY CASE uls.priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 ELSE 4 END";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new UserLearnSkill(
                            rs.getInt("user_id"),
                            rs.getInt("skill_id"),
                            rs.getString("skill_name"),
                            rs.getString("category"),
                            rs.getString("priority")
                    ));
                }
            }
        }
        return list;
    }

    public boolean updatePriority(int userId, int skillId, String priority) throws SQLException {
        String sql = "UPDATE user_learn_skills SET priority = ? WHERE user_id = ? AND skill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, priority);
            stmt.setInt(2, userId);
            stmt.setInt(3, skillId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeLearnSkill(int userId, int skillId) throws SQLException {
        String sql = "DELETE FROM user_learn_skills WHERE user_id = ? AND skill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            return stmt.executeUpdate() > 0;
        }
    }
}
