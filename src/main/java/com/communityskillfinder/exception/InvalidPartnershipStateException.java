package com.communityskillfinder.exception;

public class InvalidPartnershipStateException extends IllegalStateException {
    private final String action;
    private final int partnershipId;
    private final String currentStatus;

    public InvalidPartnershipStateException(String action, int partnershipId, String currentStatus) {
        super("Cannot " + action + " partnership #" + partnershipId + " while its status is " + currentStatus + ".");
        this.action = action;
        this.partnershipId = partnershipId;
        this.currentStatus = currentStatus;
    }

    public String getAction() {
        return action;
    }

    public int getPartnershipId() {
        return partnershipId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}
