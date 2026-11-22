package com.communityskillfinder.service.action;

import com.communityskillfinder.dao.PartnershipDAO;
import com.communityskillfinder.model.Partnership;

import java.sql.SQLException;

public interface PartnershipAction {
    String getActionName();

    boolean execute(PartnershipDAO partnershipDAO, Partnership partnership, int currentUserId) throws SQLException;
}
