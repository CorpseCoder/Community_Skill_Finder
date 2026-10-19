package com.communityskillfinder.service;

import com.communityskillfinder.dao.SkillDAO;
import com.communityskillfinder.model.Skill;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SkillService {
    private final SkillDAO skillDAO = new SkillDAO();

    public Skill addSkill(String skillName, String category) throws SQLException {
        if (skillName == null || skillName.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            category = "General";
        }
        Optional<Skill> existing = skillDAO.findByName(skillName.trim());
        if (existing.isPresent()) {
            return existing.get();
        }
        Skill skill = new Skill(skillName.trim(), category.trim());
        return skillDAO.createSkill(skill);
    }

    public List<Skill> getAllSkills() throws SQLException {
        return skillDAO.findAll();
    }

    public List<Skill> searchSkills(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return skillDAO.findAll();
        }
        return skillDAO.search(keyword.trim());
    }

    public Optional<Skill> getSkillById(int skillId) throws SQLException {
        return skillDAO.findById(skillId);
    }
}
