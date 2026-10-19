package com.communityskillfinder.model;

/**
 * Encapsulates a 1-way direct match:
 * A mentor who teaches a skill that the target user wants to learn.
 */
public class DirectMatch {
    private int mentorId;
    private String mentorName;
    private String mentorEmail;
    private int skillId;
    private String skillName;
    private String expLevel;

    public DirectMatch() {}

    public DirectMatch(int mentorId, String mentorName, String mentorEmail, int skillId, String skillName, String expLevel) {
        this.mentorId = mentorId;
        this.mentorName = mentorName;
        this.mentorEmail = mentorEmail;
        this.skillId = skillId;
        this.skillName = skillName;
        this.expLevel = expLevel;
    }

    public int getMentorId() {
        return mentorId;
    }

    public void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
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

    public String getExpLevel() {
        return expLevel;
    }

    public void setExpLevel(String expLevel) {
        this.expLevel = expLevel;
    }

    @Override
    public String toString() {
        return String.format("Mentor: %s (%s) | Teaches: [%s] (Level: %s)",
                mentorName, mentorEmail, skillName, expLevel);
    }
}
