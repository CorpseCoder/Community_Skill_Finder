package com.communityskillfinder.exception;

public class DuplicatePartnershipRequestException extends IllegalStateException {
    private final int mentorId;
    private final int menteeId;
    private final Integer skillId;

    public DuplicatePartnershipRequestException(int mentorId, int menteeId, Integer skillId) {
        super("An active partnership request already exists between users "
                + mentorId + " and " + menteeId
                + (skillId != null ? " for skill #" + skillId : " for a general exchange") + ".");
        this.mentorId = mentorId;
        this.menteeId = menteeId;
        this.skillId = skillId;
    }

    public int getMentorId() {
        return mentorId;
    }

    public int getMenteeId() {
        return menteeId;
    }

    public Integer getSkillId() {
        return skillId;
    }
}
