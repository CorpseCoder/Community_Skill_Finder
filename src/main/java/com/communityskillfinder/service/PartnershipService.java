package com.communityskillfinder.service;

import com.communityskillfinder.dao.PartnershipDAO;
import com.communityskillfinder.exception.DuplicatePartnershipRequestException;
import com.communityskillfinder.exception.PartnershipNotFoundException;
import com.communityskillfinder.model.Partnership;
import com.communityskillfinder.service.action.PartnershipAction;
import com.communityskillfinder.service.action.PartnershipActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PartnershipService {
    private final PartnershipDAO partnershipDAO = new PartnershipDAO();

    public Partnership requestPartnership(int senderId, int mentorId, int menteeId, Integer skillId) throws SQLException {
        if (mentorId == menteeId) {
            throw new IllegalArgumentException("Mentor and mentee cannot be the same user.");
        }
        if (senderId != mentorId && senderId != menteeId) {
            throw new IllegalArgumentException("Sender must be the mentor or the mentee.");
        }
        if (partnershipDAO.hasActivePartnership(mentorId, menteeId, skillId)) {
            throw new DuplicatePartnershipRequestException(mentorId, menteeId, skillId);
        }
        Partnership p = new Partnership(senderId, mentorId, menteeId, skillId, "PENDING");
        return partnershipDAO.createPartnership(p);
    }

    public Partnership requestPartnership(int mentorId, int menteeId, Integer skillId) throws SQLException {
        return requestPartnership(mentorId, mentorId, menteeId, skillId);
    }

    public List<Partnership> getUserPartnerships(int userId) throws SQLException {
        return partnershipDAO.findByUser(userId);
    }

    public Optional<Partnership> getPartnershipById(int partnershipId) throws SQLException {
        return partnershipDAO.findById(partnershipId);
    }

    public boolean acceptPartnership(int partnershipId, int currentUserId) throws SQLException {
        return execute(new PartnershipActions.Accept(), partnershipId, currentUserId);
    }

    public boolean rejectPartnership(int partnershipId, int currentUserId) throws SQLException {
        return execute(new PartnershipActions.Reject(), partnershipId, currentUserId);
    }

    public boolean completePartnership(int partnershipId, int currentUserId) throws SQLException {
        return execute(new PartnershipActions.Complete(), partnershipId, currentUserId);
    }

    public boolean cancelPartnership(int partnershipId, int currentUserId) throws SQLException {
        return execute(new PartnershipActions.Cancel(), partnershipId, currentUserId);
    }

    private boolean execute(PartnershipAction action, int partnershipId, int currentUserId) throws SQLException {
        Partnership p = partnershipDAO.findById(partnershipId)
                .orElseThrow(() -> new PartnershipNotFoundException(partnershipId));
        return action.execute(partnershipDAO, p, currentUserId);
    }
}
