package com.communityskillfinder.service;

import com.communityskillfinder.dao.PartnershipDAO;
import com.communityskillfinder.model.DirectMatch;
import com.communityskillfinder.model.ReciprocalMatch;

import java.sql.SQLException;
import java.util.List;

public class MatchingService {
    private final PartnershipDAO partnershipDAO = new PartnershipDAO();

    /**
     * Retrieves bilateral reciprocal matches where User A and User B
     * can cross-teach each other skills.
     */
    public List<ReciprocalMatch> getReciprocalMatches(int userId) throws SQLException {
        return partnershipDAO.findReciprocalMatches(userId);
    }

    /**
     * Retrieves 1-way direct matches where other users teach skills
     * the given user wants to learn.
     */
    public List<DirectMatch> getDirectMatches(int userId) throws SQLException {
        return partnershipDAO.findDirectMatches(userId);
    }
}
