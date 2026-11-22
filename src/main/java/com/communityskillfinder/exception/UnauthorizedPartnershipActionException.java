package com.communityskillfinder.exception;

public class UnauthorizedPartnershipActionException extends SecurityException {
    private final String action;
    private final int partnershipId;
    private final int userId;

    public UnauthorizedPartnershipActionException(String action, int partnershipId, int userId) {
        super("User " + userId + " is not authorized to " + action + " partnership #" + partnershipId + ".");
        this.action = action;
        this.partnershipId = partnershipId;
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public int getPartnershipId() {
        return partnershipId;
    }

    public int getUserId() {
        return userId;
    }
}
