package com.communityskillfinder.dao;

import com.communityskillfinder.model.Partnership;
import com.communityskillfinder.model.ReciprocalMatch;
import com.communityskillfinder.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PartnershipDAO {

    public Partnership createPartnership(Partnership p) throws SQLException {
        String sql = "INSERT INTO partnerships (mentor_id, mentee_id, skill_id, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, p.getMentorId());
            stmt.setInt(2, p.getMenteeId());
            if (p.getSkillId() != null) {
                stmt.setInt(3, p.getSkillId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, p.getStatus() != null ? p.getStatus() : "PENDING");

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setPartnershipId(rs.getInt(1));
                }
            }
        }
        return p;
    }

    public Optional<Partnership> findById(int partnershipId) throws SQLException {
        String sql = "SELECT p.partnership_id, p.mentor_id, p.mentee_id, p.skill_id, p.status, p.created_at, " +
                     "u1.name AS mentor_name, u2.name AS mentee_name, s.skill_name " +
                     "FROM partnerships p " +
                     "JOIN users u1 ON p.mentor_id = u1.user_id " +
                     "JOIN users u2 ON p.mentee_id = u2.user_id " +
                     "LEFT JOIN skills s ON p.skill_id = s.skill_id " +
                     "WHERE p.partnership_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, partnershipId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPartnership(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Partnership> findByUser(int userId) throws SQLException {
        List<Partnership> list = new ArrayList<>();
        String sql = "SELECT p.partnership_id, p.mentor_id, p.mentee_id, p.skill_id, p.status, p.created_at, " +
                     "u1.name AS mentor_name, u2.name AS mentee_name, s.skill_name " +
                     "FROM partnerships p " +
                     "JOIN users u1 ON p.mentor_id = u1.user_id " +
                     "JOIN users u2 ON p.mentee_id = u2.user_id " +
                     "LEFT JOIN skills s ON p.skill_id = s.skill_id " +
                     "WHERE p.mentor_id = ? OR p.mentee_id = ? " +
                     "ORDER BY p.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPartnership(rs));
                }
            }
        }
        return list;
    }

    public boolean updateStatus(int partnershipId, String status) throws SQLException {
        String sql = "UPDATE partnerships SET status = ? WHERE partnership_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, partnershipId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int partnershipId) throws SQLException {
        String sql = "DELETE FROM partnerships WHERE partnership_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, partnershipId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Executes the reciprocal matching algorithm:
     * Finds partners (User B) such that:
     * User B teaches a skill User A wants to learn,
     * AND User B wants to learn a skill User A teaches.
     */
    public List<ReciprocalMatch> findReciprocalMatches(int currentUserId) throws SQLException {
        List<ReciprocalMatch> matches = new ArrayList<>();
        String sql = "SELECT " +
                     "    partner.user_id AS partner_user_id, " +
                     "    partner.name AS partner_name, " +
                     "    partner.email AS partner_email, " +
                     "    partner_teaches.skill_name AS gives_skill_name, " +
                     "    user_teaches.skill_name AS gets_skill_name " +
                     "FROM user_learn_skills my_learn " +
                     "JOIN skills partner_teaches ON my_learn.skill_id = partner_teaches.skill_id " +
                     "JOIN user_teach_skills partner_teach_rel ON partner_teach_rel.skill_id = partner_teaches.skill_id " +
                     "JOIN users partner ON partner.user_id = partner_teach_rel.user_id " +
                     "JOIN user_learn_skills partner_learn_rel ON partner_learn_rel.user_id = partner.user_id " +
                     "JOIN skills user_teaches ON user_teaches.skill_id = partner_learn_rel.skill_id " +
                     "JOIN user_teach_skills my_teach ON my_teach.skill_id = user_teaches.skill_id AND my_teach.user_id = my_learn.user_id " +
                     "WHERE my_learn.user_id = ? AND partner.user_id <> ? " +
                     "ORDER BY partner.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, currentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matches.add(new ReciprocalMatch(
                            rs.getInt("partner_user_id"),
                            rs.getString("partner_name"),
                            rs.getString("partner_email"),
                            rs.getString("gives_skill_name"),
                            rs.getString("gets_skill_name")
                    ));
                }
            }
        }
        return matches;
    }

    /**
     * Finds 1-way direct matches:
     * Users who teach any skill that the target user has listed in their learn goals.
     */
    public List<com.communityskillfinder.model.DirectMatch> findDirectMatches(int currentUserId) throws SQLException {
        List<com.communityskillfinder.model.DirectMatch> matches = new ArrayList<>();
        String sql = "SELECT DISTINCT " +
                     "    u.user_id AS mentor_id, " +
                     "    u.name AS mentor_name, " +
                     "    u.email AS mentor_email, " +
                     "    s.skill_id, " +
                     "    s.skill_name, " +
                     "    uts.exp_level " +
                     "FROM user_learn_skills uls " +
                     "JOIN user_teach_skills uts ON uls.skill_id = uts.skill_id " +
                     "JOIN users u ON uts.user_id = u.user_id " +
                     "JOIN skills s ON uts.skill_id = s.skill_id " +
                     "WHERE uls.user_id = ? AND u.user_id <> ? " +
                     "ORDER BY s.skill_name ASC, u.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, currentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matches.add(new com.communityskillfinder.model.DirectMatch(
                            rs.getInt("mentor_id"),
                            rs.getString("mentor_name"),
                            rs.getString("mentor_email"),
                            rs.getInt("skill_id"),
                            rs.getString("skill_name"),
                            rs.getString("exp_level")
                    ));
                }
            }
        }
        return matches;
    }


    private Partnership mapResultSetToPartnership(ResultSet rs) throws SQLException {
        Partnership p = new Partnership(
                rs.getInt("partnership_id"),
                rs.getInt("mentor_id"),
                rs.getInt("mentee_id"),
                (Integer) rs.getObject("skill_id"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
        p.setMentorName(rs.getString("mentor_name"));
        p.setMenteeName(rs.getString("mentee_name"));
        p.setSkillName(rs.getString("skill_name"));
        return p;
    }
}
