package com.communityskillfinder.model;

/**
 * Encapsulates a reciprocal match result:
 * User A teaches a skill User B wants to learn,
 * AND User B teaches a skill User A wants to learn.
 */
public class ReciprocalMatch {
    private int partnerUserId;
    private String partnerName;
    private String partnerEmail;
    private String givesSkillName; // What the partner teaches (which target user wants)
    private String getsSkillName;  // What the partner wants to learn (which target user teaches)

    public ReciprocalMatch() {}

    public ReciprocalMatch(int partnerUserId, String partnerName, String partnerEmail, String givesSkillName, String getsSkillName) {
        this.partnerUserId = partnerUserId;
        this.partnerName = partnerName;
        this.partnerEmail = partnerEmail;
        this.givesSkillName = givesSkillName;
        this.getsSkillName = getsSkillName;
    }

    public int getPartnerUserId() {
        return partnerUserId;
    }

    public void setPartnerUserId(int partnerUserId) {
        this.partnerUserId = partnerUserId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getGivesSkillName() {
        return givesSkillName;
    }

    public void setGivesSkillName(String givesSkillName) {
        this.givesSkillName = givesSkillName;
    }

    public String getGetsSkillName() {
        return getsSkillName;
    }

    public void setGetsSkillName(String getsSkillName) {
        this.getsSkillName = getsSkillName;
    }

    @Override
    public String toString() {
        return String.format("Partner: %s (%s) | Can Teach You: [%s] <---> Wants From You: [%s]",
                partnerName, partnerEmail, givesSkillName, getsSkillName);
    }
}
