package com.communityskillfinder.service;

import com.communityskillfinder.dao.UserDAO;
import com.communityskillfinder.dao.UserLearnSkillDAO;
import com.communityskillfinder.dao.UserTeachSkillDAO;
import com.communityskillfinder.model.User;
import com.communityskillfinder.model.UserLearnSkill;
import com.communityskillfinder.model.UserTeachSkill;
import com.communityskillfinder.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final UserTeachSkillDAO teachSkillDAO = new UserTeachSkillDAO();
    private final UserLearnSkillDAO learnSkillDAO = new UserLearnSkillDAO();

    public User registerUser(String name, String email, String bio, String password) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        Optional<User> existing = userDAO.findByEmail(email.trim());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A user with email " + email + " already exists.");
        }

        String passwordHash = PasswordUtil.hash(password.trim());
        User user = new User(name.trim(), email.trim(), passwordHash, bio != null ? bio.trim() : "");
        return userDAO.createUser(user);
    }

    public Optional<User> getUserById(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email.trim());
    }

    public Optional<User> authenticate(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() || password == null) {
            return Optional.empty();
        }
        Optional<User> user = userDAO.findByEmail(email.trim());
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return PasswordUtil.verify(password, user.get().getPasswordHash()) ? user : Optional.empty();
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    public boolean updateProfile(int userId, String name, String email, String bio) throws SQLException {
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        if (name != null) {
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }
            user.setName(name.trim());
        }
        if (email != null) {
            String normalizedEmail = email.trim();
            if (!normalizedEmail.contains("@")) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            Optional<User> emailOwner = userDAO.findByEmail(normalizedEmail);
            if (emailOwner.isPresent() && emailOwner.get().getUserId() != userId) {
                throw new IllegalArgumentException("A user with email " + normalizedEmail + " already exists.");
            }
            user.setEmail(normalizedEmail);
        }
        if (bio != null) user.setBio(bio.trim());
        return userDAO.update(user);
    }

    public boolean deactivateUser(int userId) throws SQLException {
        return userDAO.delete(userId);
    }

    // Teaching skill management
    public boolean addTeachSkill(int userId, int skillId, String expLevel) throws SQLException {
        String level = expLevel != null ? expLevel.toUpperCase().trim() : "INTERMEDIATE";
        if (!List.of("BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT").contains(level)) {
            throw new IllegalArgumentException("Experience level must be BEGINNER, INTERMEDIATE, ADVANCED, or EXPERT.");
        }
        return teachSkillDAO.addTeachSkill(userId, skillId, level);
    }

    public List<UserTeachSkill> getTeachSkills(int userId) throws SQLException {
        return teachSkillDAO.findByUserId(userId);
    }

    public boolean removeTeachSkill(int userId, int skillId) throws SQLException {
        return teachSkillDAO.removeTeachSkill(userId, skillId);
    }

    // Learning skill management
    public boolean addLearnSkill(int userId, int skillId, String priority) throws SQLException {
        String prio = priority != null ? priority.toUpperCase().trim() : "MEDIUM";
        if (!List.of("LOW", "MEDIUM", "HIGH").contains(prio)) {
            throw new IllegalArgumentException("Priority must be LOW, MEDIUM, or HIGH.");
        }
        return learnSkillDAO.addLearnSkill(userId, skillId, prio);
    }

    public List<UserLearnSkill> getLearnSkills(int userId) throws SQLException {
        return learnSkillDAO.findByUserId(userId);
    }

    public boolean removeLearnSkill(int userId, int skillId) throws SQLException {
        return learnSkillDAO.removeLearnSkill(userId, skillId);
    }
}
