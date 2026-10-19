package com.communityskillfinder.model;

import java.sql.Timestamp;

public class Partnership {
    private int partnershipId;
    private int mentorId;
    private int menteeId;
    private Integer skillId;
    private String status; // PENDING, ACCEPTED, REJECTED, COMPLETED
    private Timestamp createdAt;

    // Optional metadata for display
    private String mentorName;
    private String menteeName;
    private String skillName;

    public Partnership() {}

    public Partnership(int mentorId, int menteeId, Integer skillId, String status) {
        this.mentorId = mentorId;
        this.menteeId = menteeId;
        this.skillId = skillId;
        this.status = status;
    }

    public Partnership(int partnershipId, int mentorId, int menteeId, Integer skillId, String status, Timestamp createdAt) {
        this.partnershipId = partnershipId;
        this.mentorId = mentorId;
        this.menteeId = menteeId;
        this.skillId = skillId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getPartnershipId() {
        return partnershipId;
    }

    public void setPartnershipId(int partnershipId) {
        this.partnershipId = partnershipId;
    }

    public int getMentorId() {
        return mentorId;
    }

    public void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    public int getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(int menteeId) {
        this.menteeId = menteeId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMenteeName() {
        return menteeName;
    }

    public void setMenteeName(String menteeName) {
        this.menteeName = menteeName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @Override
    public String toString() {
        return String.format("[#%d] Mentor: %s | Mentee: %s | Skill: %s | Status: %s (%s)",
                partnershipId,
                mentorName != null ? mentorName : "User " + mentorId,
                menteeName != null ? menteeName : "User " + menteeId,
                skillName != null ? skillName : (skillId != null ? "Skill " + skillId : "N/A"),
                status,
                createdAt);
    }
}
