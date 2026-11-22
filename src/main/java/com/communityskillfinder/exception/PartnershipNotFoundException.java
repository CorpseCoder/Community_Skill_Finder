package com.communityskillfinder.exception;

public class PartnershipNotFoundException extends IllegalArgumentException {
    private final int partnershipId;

    public PartnershipNotFoundException(int partnershipId) {
        super("Partnership #" + partnershipId + " not found.");
        this.partnershipId = partnershipId;
    }

    public int getPartnershipId() {
        return partnershipId;
    }
}
