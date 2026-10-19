package com.communityskillfinder.dao;

import com.communityskillfinder.model.Skill;
import com.communityskillfinder.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkillDAO {

    public Skill createSkill(Skill skill) throws SQLException {
        String sql = "INSERT INTO skills (skill_name, category) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, skill.getSkillName());
            stmt.setString(2, skill.getCategory());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    skill.setSkillId(rs.getInt(1));
                }
            }
        }
        return skill;
    }

    public Optional<Skill> findById(int skillId) throws SQLException {
        String sql = "SELECT skill_id, skill_name, category FROM skills WHERE skill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, skillId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Skill> findByName(String skillName) throws SQLException {
        String sql = "SELECT skill_id, skill_name, category FROM skills WHERE LOWER(skill_name) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, skillName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Skill> findAll() throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT skill_id, skill_name, category FROM skills ORDER BY category ASC, skill_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                skills.add(mapResultSetToSkill(rs));
            }
        }
        return skills;
    }

    public List<Skill> search(String query) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT skill_id, skill_name, category FROM skills " +
                     "WHERE LOWER(skill_name) LIKE LOWER(?) OR LOWER(category) LIKE LOWER(?) " +
                     "ORDER BY skill_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    skills.add(mapResultSetToSkill(rs));
                }
            }
        }
        return skills;
    }

    public boolean update(Skill skill) throws SQLException {
        String sql = "UPDATE skills SET skill_name = ?, category = ? WHERE skill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, skill.getSkillName());
            stmt.setString(2, skill.getCategory());
            stmt.setInt(3, skill.getSkillId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int skillId) throws SQLException {
        String sql = "DELETE FROM skills WHERE skill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, skillId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Skill mapResultSetToSkill(ResultSet rs) throws SQLException {
        return new Skill(
                rs.getInt("skill_id"),
                rs.getString("skill_name"),
                rs.getString("category")
        );
    }
}
