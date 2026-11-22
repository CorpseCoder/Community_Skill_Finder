package com.communityskillfinder.service.action;

import com.communityskillfinder.dao.PartnershipDAO;
import com.communityskillfinder.exception.InvalidPartnershipStateException;
import com.communityskillfinder.exception.UnauthorizedPartnershipActionException;
import com.communityskillfinder.model.Partnership;

import java.sql.SQLException;

public abstract class AbstractPartnershipAction implements PartnershipAction {

    @Override
    public final boolean execute(PartnershipDAO partnershipDAO, Partnership partnership, int currentUserId) throws SQLException {
        if (!isAuthorized(partnership, currentUserId)) {
            throw new UnauthorizedPartnershipActionException(getActionName(), partnership.getPartnershipId(), currentUserId);
        }
        if (!isAllowedInCurrentState(partnership)) {
            throw new InvalidPartnershipStateException(getActionName(), partnership.getPartnershipId(), partnership.getStatus());
        }
        return perform(partnershipDAO, partnership, currentUserId);
    }

    protected abstract boolean isAuthorized(Partnership partnership, int currentUserId);

    protected abstract boolean isAllowedInCurrentState(Partnership partnership);

    protected abstract boolean perform(PartnershipDAO partnershipDAO, Partnership partnership, int currentUserId) throws SQLException;
}
