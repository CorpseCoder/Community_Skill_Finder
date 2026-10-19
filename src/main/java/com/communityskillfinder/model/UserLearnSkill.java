package com.communityskillfinder.model;

public class UserLearnSkill {
    private int userId;
    private int skillId;
    private String skillName;
    private String category;
    private String priority; // LOW, MEDIUM, HIGH

    public UserLearnSkill() {}

    public UserLearnSkill(int userId, int skillId, String priority) {
        this.userId = userId;
        this.skillId = skillId;
        this.priority = priority;
    }

    public UserLearnSkill(int userId, int skillId, String skillName, String category, String priority) {
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.priority = priority;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [Priority: %s]", skillName != null ? skillName : "Skill #" + skillId, category != null ? category : "", priority);
    }
}
