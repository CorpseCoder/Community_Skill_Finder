package com.communityskillfinder.dao;

import com.communityskillfinder.model.UserTeachSkill;
import com.communityskillfinder.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTeachSkillDAO {

    public boolean addTeachSkill(int userId, int skillId, String expLevel) throws SQLException {
        String sql = "INSERT INTO user_teach_skills (user_id, skill_id, exp_level) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE exp_level = VALUES(exp_level)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            stmt.setString(3, expLevel);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<UserTeachSkill> findByUserId(int userId) throws SQLException {
        List<UserTeachSkill> list = new ArrayList<>();
        String sql = "SELECT uts.user_id, uts.skill_id, s.skill_name, s.category, uts.exp_level " +
                     "FROM user_teach_skills uts " +
                     "JOIN skills s ON uts.skill_id = s.skill_id " +
                     "WHERE uts.user_id = ? " +
                     "ORDER BY s.skill_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new UserTeachSkill(
                            rs.getInt("user_id"),
                            rs.getInt("skill_id"),
                            rs.getString("skill_name"),
                            rs.getString("category"),
                            rs.getString("exp_level")
                    ));
                }
            }
        }
        return list;
    }

    public boolean updateExpLevel(int userId, int skillId, String expLevel) throws SQLException {
        String sql = "UPDATE user_teach_skills SET exp_level = ? WHERE user_id = ? AND skill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, expLevel);
            stmt.setInt(2, userId);
            stmt.setInt(3, skillId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeTeachSkill(int userId, int skillId) throws SQLException {
        String sql = "DELETE FROM user_teach_skills WHERE user_id = ? AND skill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            return stmt.executeUpdate() > 0;
        }
    }
}
