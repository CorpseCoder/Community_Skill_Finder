package com.communityskillfinder;

import com.communityskillfinder.exception.DuplicatePartnershipRequestException;
import com.communityskillfinder.exception.InvalidPartnershipStateException;
import com.communityskillfinder.exception.UnauthorizedPartnershipActionException;
import com.communityskillfinder.service.UserService;
import com.communityskillfinder.service.PartnershipService;
import com.communityskillfinder.service.MatchingService;
import com.communityskillfinder.service.SkillService;
import com.communityskillfinder.model.User;
import com.communityskillfinder.model.Partnership;
import com.communityskillfinder.model.ReciprocalMatch;
import com.communityskillfinder.model.DirectMatch;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AppTest {
    static int passed = 0, failed = 0;
    static void check(String name, boolean cond) {
        if (cond) { passed++; System.out.println("  PASS: " + name); }
        else { failed++; System.out.println("  FAIL: " + name); }
    }

    public static void main(String[] args) throws Exception {
        UserService us = new UserService();
        PartnershipService ps = new PartnershipService();
        MatchingService ms = new MatchingService();
        SkillService ss = new SkillService();

        System.out.println("=== Test 1: Register users with hashed passwords ===");
        try {
            User alice = us.registerUser("Alice Test", "alicetest@example.com", "Bio A", "password123");
            check("Alice registered with ID", alice.getUserId() > 0);
            check("Alice has password hash", alice.getPasswordHash() != null && alice.getPasswordHash().startsWith("pbkdf2:sha256:"));
            System.out.println("    Alice password_hash: " + alice.getPasswordHash().substring(0, 40) + "...");
        } catch (Exception e) { check("Alice registered", false); System.out.println("    " + e.getMessage()); }

        try {
            User bob = us.registerUser("Bob Test", "bobtest@example.com", "Bio B", "password456");
            check("Bob registered with ID", bob.getUserId() > 0);
            check("Bob has password hash", bob.getPasswordHash() != null && bob.getPasswordHash().startsWith("pbkdf2:sha256:"));
        } catch (Exception e) { check("Bob registered", false); System.out.println("    " + e.getMessage()); }

        try {
            us.registerUser("Dup", "alicetest@example.com", "Bio", "pass");
            check("Duplicate email rejected", false);
        } catch (IllegalArgumentException e) {
            check("Duplicate email rejected", true);
        }

        try {
            us.registerUser("NoPass", "nopass@example.com", "Bio", "");
            check("Empty password rejected", false);
        } catch (IllegalArgumentException e) {
            check("Empty password rejected", true);
        }

        System.out.println("\n=== Test 2: Login by email ===");
        try {
            Optional<User> u = us.getUserByEmail("alicetest@example.com");
            check("Alice found by email", u.isPresent());
            check("Alice email matches", u.map(User::getEmail).orElse("").equals("alicetest@example.com"));
            check("Correct password authenticates", us.authenticate("alicetest@example.com", "password123").isPresent());
            check("Wrong password rejected", us.authenticate("alicetest@example.com", "wrong").isEmpty());
            check("Unknown email rejected", us.authenticate("nobody@example.com", "password123").isEmpty());
        } catch (Exception e) { check("Alice found by email", false); System.out.println("    " + e.getMessage()); }

        System.out.println("\n=== Test 3: Password hash stored in DB ===");
        try {
            Optional<User> u = us.getUserByEmail("alicetest@example.com");
            check("Password hash not null", u.map(User::getPasswordHash).orElse(null) != null);
            check("Password hash starts with pbkdf2", u.map(User::getPasswordHash).orElse("").startsWith("pbkdf2:sha256:"));
        } catch (Exception e) { check("Password hash in DB", false); System.out.println("    " + e.getMessage()); }

        System.out.println("\n=== Test 4: Teach/Learn skills ===");
        try {
            boolean t1 = us.addTeachSkill(5, 1, "EXPERT");    // Alice teaches Java
            boolean t2 = us.addTeachSkill(5, 2, "INTERMEDIATE"); // Alice teaches Python
            boolean l1 = us.addLearnSkill(5, 1, "HIGH");      // Alice learns Java (for Bob match)
            boolean l3 = us.addLearnSkill(5, 3, "HIGH");      // Alice learns SQL (for removal test)
            check("Alice teach Java", t1);
            check("Alice teach Python", t2);
            check("Alice learn Java", l1);
            check("Alice learn SQL", l3);

            boolean t3 = us.addTeachSkill(6, 1, "EXPERT");    // Bob teaches Java
            boolean l2 = us.addLearnSkill(6, 2, "HIGH");      // Bob learns Python
            check("Bob teach Java", t3);
            check("Bob learn Python", l2);
        } catch (Exception e) { check("Skills added", false); System.out.println("    " + e.getMessage()); }

        System.out.println("\n=== Test 5: Reciprocal matches ===");
        try {
            List<ReciprocalMatch> matches = ms.getReciprocalMatches(5);
            check("Alice has reciprocal match with Bob", matches.stream().anyMatch(m -> m.getPartnerUserId() == 6));
            if (matches.stream().anyMatch(m -> m.getPartnerUserId() == 6)) {
                ReciprocalMatch m = matches.stream().filter(x -> x.getPartnerUserId() == 6).findFirst().get();
                check("Match: Bob teaches Java (Alice wants)", m.getGivesSkillName().equals("Java"));
                check("Match: Bob wants Python (Alice teaches)", m.getGetsSkillName().equals("Python"));
            }
        } catch (Exception e) { check("Reciprocal matches", false); System.out.println("    " + e.getMessage()); }

        System.out.println("\n=== Test 6: Direct matches ===");
        try {
            List<DirectMatch> direct = ms.getDirectMatches(5);
            check("Alice has direct match (Bob teaches Java)", direct.stream().anyMatch(d -> d.getMentorId() == 6));
        } catch (Exception e) { check("Direct matches", false); System.out.println("    " + e.getMessage()); }

        System.out.println("\n=== Test 7: Partnership lifecycle ===");
        try {
            Partnership p = ps.requestPartnership(5, 6, 1);  // Alice mentors Bob on Java
            check("Partnership created", p.getPartnershipId() > 0);
            check("Partnership status PENDING", p.getStatus().equals("PENDING"));
            int pid = p.getPartnershipId();
            check("Partnership sender is Alice", p.getSenderId() == 5);
            check("Partnership recipient is Bob", p.getRecipientId() == 6);

            boolean duplicateRejected = false;
            try { ps.requestPartnership(5, 6, 1); } catch (DuplicatePartnershipRequestException ex) { duplicateRejected = true; }
            check("Duplicate active request rejected", duplicateRejected);

            // View partnerships
            List<Partnership> aliceParts = ps.getUserPartnerships(5);
            check("Alice sees partnership", aliceParts.stream().anyMatch(pp -> pp.getPartnershipId() == pid));

            boolean senderCannotAccept = false;
            try { ps.acceptPartnership(pid, 5); } catch (UnauthorizedPartnershipActionException ex) { senderCannotAccept = true; }
            check("Sender cannot accept own request", senderCannotAccept);

            boolean strangerCannotAccept = false;
            try { ps.acceptPartnership(pid, 999); } catch (UnauthorizedPartnershipActionException ex) { strangerCannotAccept = true; }
            check("Unrelated user cannot accept request", strangerCannotAccept);

            // Bob accepts
            boolean accepted = ps.acceptPartnership(pid, 6);
            check("Bob accepts partnership", accepted);

            // Verify status changed
            Optional<Partnership> updated = ps.getPartnershipById(pid);
            check("Partnership status ACCEPTED", updated.map(Partnership::getStatus).orElse("").equals("ACCEPTED"));

            boolean reAcceptRejected = false;
            try { ps.acceptPartnership(pid, 6); } catch (InvalidPartnershipStateException ex) { reAcceptRejected = true; }
            check("Re-accept after ACCEPTED rejected", reAcceptRejected);

            boolean rejectAfterAcceptRejected = false;
            try { ps.rejectPartnership(pid, 6); } catch (InvalidPartnershipStateException ex) { rejectAfterAcceptRejected = true; }
            check("Reject after ACCEPTED rejected", rejectAfterAcceptRejected);

            // Cancel
            boolean cancelled = ps.cancelPartnership(pid, 6);
            check("Partnership cancelled", cancelled);
        } catch (Exception e) { check("Partnership lifecycle", false); System.out.println("    " + e.getMessage()); e.printStackTrace(); }

        System.out.println("\n=== Test 8: Skill catalog ===");
        try {
            List<com.communityskillfinder.model.Skill> skills = ss.getAllSkills();
            check("Skills catalog not empty", !skills.isEmpty());
            com.communityskillfinder.model.Skill newSkill = ss.addSkill("Kubernetes", "DevOps");
            check("New skill added", newSkill.getSkillId() > 0);
            List<com.communityskillfinder.model.Skill> found = ss.searchSkills("Kubernetes");
            check("Skill search finds Kubernetes", found.stream().anyMatch(s -> s.getSkillName().equals("Kubernetes")));
        } catch (Exception e) { check("Skill catalog", false); System.out.println("    " + e.getMessage()); }

        System.out.println("\n=== Test 9: Remove skills + reject/complete partnerships + profile update ===");
        try {
            boolean removed = us.removeTeachSkill(5, 2);  // Alice removes Python
            check("Remove teach skill", removed);
            List<com.communityskillfinder.model.UserTeachSkill> after = us.getTeachSkills(5);
            check("Python no longer in teach list", after.stream().noneMatch(t -> t.getSkillId() == 2));
            us.addTeachSkill(5, 2, "INTERMEDIATE"); // restore

            boolean removedLearn = us.removeLearnSkill(5, 3); // Alice removes SQL
            check("Remove learn skill", removedLearn);
            us.addLearnSkill(5, 3, "HIGH"); // restore
        } catch (Exception e) { check("Remove skills", false); System.out.println("    " + e.getMessage()); }

        try {
            Partnership p1 = ps.requestPartnership(6, 5, 1); // Bob mentors Alice
            boolean rejected = ps.rejectPartnership(p1.getPartnershipId(), 5);
            check("Reject partnership", rejected);
            check("Rejected status", ps.getPartnershipById(p1.getPartnershipId()).map(Partnership::getStatus).orElse("").equals("REJECTED"));

            boolean acceptRejected = false;
            try { ps.acceptPartnership(p1.getPartnershipId(), 5); } catch (InvalidPartnershipStateException ex) { acceptRejected = true; }
            check("Accept after REJECTED rejected", acceptRejected);

            Partnership p2 = ps.requestPartnership(5, 6, 2);
            boolean completePendingRejected = false;
            try { ps.completePartnership(p2.getPartnershipId(), 5); } catch (InvalidPartnershipStateException ex) { completePendingRejected = true; }
            check("Complete while PENDING rejected", completePendingRejected);
            check("Recipient accepts before completion", ps.acceptPartnership(p2.getPartnershipId(), 6));
            boolean completed = ps.completePartnership(p2.getPartnershipId(), 5);
            check("Complete partnership", completed);
            check("Completed status", ps.getPartnershipById(p2.getPartnershipId()).map(Partnership::getStatus).orElse("").equals("COMPLETED"));

            boolean authFail = false;
            try { ps.rejectPartnership(p1.getPartnershipId(), 999); } catch (SecurityException se) { authFail = true; }
            check("Unauthorized user rejected", authFail);
        } catch (Exception e) { check("Reject/complete partnership", false); System.out.println("    " + e.getMessage()); }

        try {
            boolean updated = us.updateProfile(5, "Alice Updated", "alicetest@example.com", "New bio");
            check("Profile updated", updated);
            check("Profile name changed", us.getUserById(5).map(User::getName).orElse("").equals("Alice Updated"));
            check("Profile bio changed", us.getUserById(5).map(User::getBio).orElse("").equals("New bio"));
        } catch (Exception e) { check("Profile update", false); System.out.println("    " + e.getMessage()); }

        try {
            boolean invalidLevel = false;
            try { us.addTeachSkill(5, 1, "LEGENDARY"); } catch (IllegalArgumentException ie) { invalidLevel = true; }
            check("Invalid exp level rejected", invalidLevel);

            boolean invalidPrio = false;
            try { us.addLearnSkill(5, 1, "URGENT"); } catch (IllegalArgumentException ie) { invalidPrio = true; }
            check("Invalid priority rejected", invalidPrio);

            boolean invalidProfileEmail = false;
            try { us.updateProfile(5, "Alice", "bad-email", "bio"); } catch (IllegalArgumentException ie) { invalidProfileEmail = true; }
            check("Invalid profile email rejected", invalidProfileEmail);

            boolean duplicateProfileEmail = false;
            try { us.updateProfile(5, "Alice", "bobtest@example.com", "bio"); } catch (IllegalArgumentException ie) { duplicateProfileEmail = true; }
            check("Duplicate profile email rejected", duplicateProfileEmail);

            boolean notFound = false;
            try { ps.acceptPartnership(99999, 5); } catch (IllegalArgumentException ie) { notFound = true; }
            check("Nonexistent partnership rejected", notFound);
        } catch (Exception e) { check("Validation checks", false); System.out.println("    " + e.getMessage()); }

        System.out.println("\n=== RESULTS: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}
