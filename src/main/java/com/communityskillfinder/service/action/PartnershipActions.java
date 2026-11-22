package com.communityskillfinder.service.action;

import com.communityskillfinder.dao.PartnershipDAO;
import com.communityskillfinder.model.Partnership;

import java.sql.SQLException;

public final class PartnershipActions {

    private PartnershipActions() {}

    public static final class Accept extends AbstractPartnershipAction {
        @Override
        public String getActionName() {
            return "accept";
        }

        @Override
        protected boolean isAuthorized(Partnership partnership, int currentUserId) {
            return partnership.isRecipient(currentUserId);
        }

        @Override
        protected boolean isAllowedInCurrentState(Partnership partnership) {
            return "PENDING".equals(partnership.getStatus());
        }

        @Override
        protected boolean perform(PartnershipDAO partnershipDAO, Partnership partnership, int currentUserId) throws SQLException {
            return partnershipDAO.updateStatus(partnership.getPartnershipId(), "ACCEPTED");
        }
    }

    public static final class Reject extends AbstractPartnershipAction {
        @Override
        public String getActionName() {
            return "reject";
        }

        @Override
        protected boolean isAuthorized(Partnership partnership, int currentUserId) {
            return partnership.isParticipant(currentUserId);
        }

        @Override
        protected boolean isAllowedInCurrentState(Partnership partnership) {
            return "PENDING".equals(partnership.getStatus());
        }

        @Override
        protected boolean perform(PartnershipDAO partnershipDAO, Partnership partnership, int currentUserId) throws SQLException {
            return partnershipDAO.updateStatus(partnership.getPartnershipId(), "REJECTED");
        }
    }

    public static final class Complete extends AbstractPartnershipAction {
        @Override
        public String getActionName() {
            return "complete";
        }

        @Override
        protected boolean isAuthorized(Partnership partnership, int currentUserId) {
            return partnership.isParticipant(currentUserId);
        }

        @Override
        protected boolean isAllowedInCurrentState(Partnership partnership) {
            return "ACCEPTED".equals(partnership.getStatus());
        }

        @Override
        protected boolean perform(PartnershipDAO partnershipDAO, Partnership partnership, int currentUserId) throws SQLException {
            return partnershipDAO.updateStatus(partnership.getPartnershipId(), "COMPLETED");
        }
    }

    public static final class Cancel extends AbstractPartnershipAction {
        @Override
        public String getActionName() {
            return "cancel";
        }

        @Override
        protected boolean isAuthorized(Partnership partnership, int currentUserId) {
            return partnership.isParticipant(currentUserId);
        }

        @Override
        protected boolean isAllowedInCurrentState(Partnership partnership) {
            return "PENDING".equals(partnership.getStatus()) || "ACCEPTED".equals(partnership.getStatus());
        }

        @Override
        protected boolean perform(PartnershipDAO partnershipDAO, Partnership partnership, int currentUserId) throws SQLException {
            return partnershipDAO.delete(partnership.getPartnershipId());
        }
    }
}
