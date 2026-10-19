package com.communityskillfinder.service;

import com.communityskillfinder.dao.PartnershipDAO;
import com.communityskillfinder.model.Partnership;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PartnershipService {
    private final PartnershipDAO partnershipDAO = new PartnershipDAO();

    public Partnership requestPartnership(int mentorId, int menteeId, Integer skillId) throws SQLException {
        if (mentorId == menteeId) {
            throw new IllegalArgumentException("Mentor and mentee cannot be the same user.");
        }
        Partnership p = new Partnership(mentorId, menteeId, skillId, "PENDING");
        return partnershipDAO.createPartnership(p);
    }

    public List<Partnership> getUserPartnerships(int userId) throws SQLException {
        return partnershipDAO.findByUser(userId);
    }

    public Optional<Partnership> getPartnershipById(int partnershipId) throws SQLException {
        return partnershipDAO.findById(partnershipId);
    }

    public boolean acceptPartnership(int partnershipId, int currentUserId) throws SQLException {
        Optional<Partnership> opt = partnershipDAO.findById(partnershipId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Partnership #" + partnershipId + " not found.");
        }
        Partnership p = opt.get();
        // Either mentor or mentee involved can respond
        if (p.getMentorId() != currentUserId && p.getMenteeId() != currentUserId) {
            throw new SecurityException("You are not authorized to accept this partnership.");
        }
        return partnershipDAO.updateStatus(partnershipId, "ACCEPTED");
    }

    public boolean rejectPartnership(int partnershipId, int currentUserId) throws SQLException {
        Optional<Partnership> opt = partnershipDAO.findById(partnershipId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Partnership #" + partnershipId + " not found.");
        }
        Partnership p = opt.get();
        if (p.getMentorId() != currentUserId && p.getMenteeId() != currentUserId) {
            throw new SecurityException("You are not authorized to modify this partnership.");
        }
        return partnershipDAO.updateStatus(partnershipId, "REJECTED");
    }

    public boolean completePartnership(int partnershipId, int currentUserId) throws SQLException {
        Optional<Partnership> opt = partnershipDAO.findById(partnershipId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Partnership #" + partnershipId + " not found.");
        }
        Partnership p = opt.get();
        if (p.getMentorId() != currentUserId && p.getMenteeId() != currentUserId) {
            throw new SecurityException("You are not authorized to modify this partnership.");
        }
        return partnershipDAO.updateStatus(partnershipId, "COMPLETED");
    }

    public boolean cancelPartnership(int partnershipId, int currentUserId) throws SQLException {
        Optional<Partnership> opt = partnershipDAO.findById(partnershipId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Partnership #" + partnershipId + " not found.");
        }
        Partnership p = opt.get();
        if (p.getMentorId() != currentUserId && p.getMenteeId() != currentUserId) {
            throw new SecurityException("You are not authorized to cancel this partnership.");
        }
        return partnershipDAO.delete(partnershipId);
    }
}
