package com.communityskillfinder.model;

public class UserTeachSkill {
    private int userId;
    private int skillId;
    private String skillName;
    private String category;
    private String expLevel; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    public UserTeachSkill() {}

    public UserTeachSkill(int userId, int skillId, String expLevel) {
        this.userId = userId;
        this.skillId = skillId;
        this.expLevel = expLevel;
    }

    public UserTeachSkill(int userId, int skillId, String skillName, String category, String expLevel) {
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.expLevel = expLevel;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpLevel() {
        return expLevel;
    }

    public void setExpLevel(String expLevel) {
        this.expLevel = expLevel;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [Experience: %s]", skillName != null ? skillName : "Skill #" + skillId, category != null ? category : "", expLevel);
    }
}
