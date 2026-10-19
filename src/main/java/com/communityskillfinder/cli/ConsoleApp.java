package com.communityskillfinder.cli;

import com.communityskillfinder.model.*;
import com.communityskillfinder.service.MatchingService;
import com.communityskillfinder.service.PartnershipService;
import com.communityskillfinder.service.SkillService;
import com.communityskillfinder.service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    private final UserService userService = new UserService();
    private final SkillService skillService = new SkillService();
    private final MatchingService matchingService = new MatchingService();
    private final PartnershipService partnershipService = new PartnershipService();
    private final Scanner scanner = new Scanner(System.in);

    private User currentUser = null;

    public static void main(String[] args) {
        new ConsoleApp().run();
    }

    public void run() {
        printBanner();

        boolean running = true;
        while (running) {
            try {
                if (currentUser == null) {
                    running = showAuthMenu();
                } else {
                    running = showMainMenu();
                }
            } catch (Exception e) {
                System.out.println("\n[!] Error: " + e.getMessage());
            }
        }

        System.out.println("\nThank you for using Community Skill Finder. Goodbye!");
    }

    private void printBanner() {
        System.out.println("======================================================================");
        System.out.println("                   COMMUNITY SKILL FINDER CLI                         ");
        System.out.println("    A Peer-to-Peer Technical Learning Exchange Platform (Java+MySQL)  ");
        System.out.println("======================================================================");
    }

    private boolean showAuthMenu() throws SQLException {
        System.out.println("\n--- WELCOME / AUTHENTICATION ---");
        System.out.println("1. Login by Email");
        System.out.println("2. Register New User");
        System.out.println("3. List All Users (Demo / Quick Select)");
        System.out.println("4. Exit");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleRegister();
            case "3" -> handleListUsers();
            case "4" -> { return false; }
            default -> System.out.println("[!] Invalid option. Try again.");
        }
        return true;
    }

    private void handleLogin() throws SQLException {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            System.out.println("\n[+] Welcome back, " + currentUser.getName() + "!");
        } else {
            System.out.println("[!] No user found with email: " + email);
        }
    }

    private void handleRegister() throws SQLException {
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter a short bio: ");
        String bio = scanner.nextLine().trim();

        try {
            currentUser = userService.registerUser(name, email, bio);
            System.out.println("\n[+] Registration successful! Logged in as " + currentUser.getName() + " (ID: " + currentUser.getUserId() + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("[!] Registration failed: " + e.getMessage());
        }
    }

    private void handleListUsers() throws SQLException {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found in database.");
            return;
        }
        System.out.println("\n--- REGISTERED USERS ---");
        for (User u : users) {
            System.out.printf("[%d] %s (%s) - %s%n", u.getUserId(), u.getName(), u.getEmail(), u.getBio() != null ? u.getBio() : "");
        }
        System.out.print("\nEnter User ID to quickly log in (or press Enter to cancel): ");
        String line = scanner.nextLine().trim();
        if (!line.isEmpty()) {
            try {
                int uid = Integer.parseInt(line);
                Optional<User> uOpt = userService.getUserById(uid);
                if (uOpt.isPresent()) {
                    currentUser = uOpt.get();
                    System.out.println("\n[+] Switched active user to: " + currentUser.getName());
                } else {
                    System.out.println("[!] User ID not found.");
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private boolean showMainMenu() throws SQLException {
        System.out.println("\n======================================================================");
        System.out.println(" LOGGED IN AS: " + currentUser.getName() + " <" + currentUser.getEmail() + "> (ID: " + currentUser.getUserId() + ")");
        System.out.println("======================================================================");
        System.out.println("1. View My Profile & Skill Inventory");
        System.out.println("2. Manage Skills I Can TEACH (Add / Remove)");
        System.out.println("3. Manage Skills I Want to LEARN (Add / Remove)");
        System.out.println("4. FIND RECIPROCAL MATCHES (2-Way Exchange) [CORE]");
        System.out.println("5. Find Direct Matches (1-Way Learning)");
        System.out.println("6. Send Partnership Exchange Request");
        System.out.println("7. View & Manage My Partnerships (Pending / Active)");
        System.out.println("8. Explore / Add to Master Skills Catalog");
        System.out.println("9. Switch User / Logout");
        System.out.println("0. Exit Application");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> viewProfile();
            case "2" -> manageTeachSkills();
            case "3" -> manageLearnSkills();
            case "4" -> viewReciprocalMatches();
            case "5" -> viewDirectMatches();
            case "6" -> sendPartnershipRequest();
            case "7" -> managePartnerships();
            case "8" -> manageSkillCatalog();
            case "9" -> { currentUser = null; System.out.println("[*] Logged out."); }
            case "0" -> { return false; }
            default -> System.out.println("[!] Invalid option. Try again.");
        }
        return true;
    }

    private void viewProfile() throws SQLException {
        System.out.println("\n--- PROFILE DETAILS ---");
        System.out.println("Name:  " + currentUser.getName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Bio:   " + (currentUser.getBio() != null ? currentUser.getBio() : "None"));

        System.out.println("\n[Skills I Can Teach]:");
        List<UserTeachSkill> teachList = userService.getTeachSkills(currentUser.getUserId());
        if (teachList.isEmpty()) {
            System.out.println("  (None listed yet)");
        } else {
            for (UserTeachSkill ts : teachList) {
                System.out.println("  * " + ts.getSkillName() + " [" + ts.getCategory() + "] - Level: " + ts.getExpLevel());
            }
        }

        System.out.println("\n[Skills I Want to Learn]:");
        List<UserLearnSkill> learnList = userService.getLearnSkills(currentUser.getUserId());
        if (learnList.isEmpty()) {
            System.out.println("  (None listed yet)");
        } else {
            for (UserLearnSkill ls : learnList) {
                System.out.println("  * " + ls.getSkillName() + " [" + ls.getCategory() + "] - Priority: " + ls.getPriority());
            }
        }
    }

    private void manageTeachSkills() throws SQLException {
        System.out.println("\n--- MANAGE TEACHING SKILLS ---");
        System.out.println("1. Add / Update Teaching Skill");
        System.out.println("2. Remove Teaching Skill");
        System.out.print("Select: ");
        String opt = scanner.nextLine().trim();

        if ("1".equals(opt)) {
            displayCatalog();
            System.out.print("Enter Skill ID: ");
            int skillId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Experience Level (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT): ");
            String level = scanner.nextLine().trim();
            userService.addTeachSkill(currentUser.getUserId(), skillId, level);
            System.out.println("[+] Teaching skill added/updated!");
        } else if ("2".equals(opt)) {
            List<UserTeachSkill> list = userService.getTeachSkills(currentUser.getUserId());
            if (list.isEmpty()) {
                System.out.println("No teaching skills found.");
                return;
            }
            for (UserTeachSkill s : list) {
                System.out.printf("[%d] %s (%s)%n", s.getSkillId(), s.getSkillName(), s.getExpLevel());
            }
            System.out.print("Enter Skill ID to remove: ");
            int skillId = Integer.parseInt(scanner.nextLine().trim());
            userService.removeTeachSkill(currentUser.getUserId(), skillId);
            System.out.println("[+] Skill removed from your teaching inventory.");
        }
    }

    private void manageLearnSkills() throws SQLException {
        System.out.println("\n--- MANAGE LEARNING GOALS ---");
        System.out.println("1. Add / Update Learning Goal");
        System.out.println("2. Remove Learning Goal");
        System.out.print("Select: ");
        String opt = scanner.nextLine().trim();

        if ("1".equals(opt)) {
            displayCatalog();
            System.out.print("Enter Skill ID: ");
            int skillId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Priority (LOW, MEDIUM, HIGH): ");
            String priority = scanner.nextLine().trim();
            userService.addLearnSkill(currentUser.getUserId(), skillId, priority);
            System.out.println("[+] Learning goal added/updated!");
        } else if ("2".equals(opt)) {
            List<UserLearnSkill> list = userService.getLearnSkills(currentUser.getUserId());
            if (list.isEmpty()) {
                System.out.println("No learning goals found.");
                return;
            }
            for (UserLearnSkill s : list) {
                System.out.printf("[%d] %s (Priority: %s)%n", s.getSkillId(), s.getSkillName(), s.getPriority());
            }
            System.out.print("Enter Skill ID to remove: ");
            int skillId = Integer.parseInt(scanner.nextLine().trim());
            userService.removeLearnSkill(currentUser.getUserId(), skillId);
            System.out.println("[+] Skill removed from your learning goals.");
        }
    }

    private void viewReciprocalMatches() throws SQLException {
        System.out.println("\n======================================================================");
        System.out.println("               RECIPROCAL MATCHES (2-WAY SKILL BARTER)                ");
        System.out.println("======================================================================");
        List<ReciprocalMatch> matches = matchingService.getReciprocalMatches(currentUser.getUserId());
        if (matches.isEmpty()) {
            System.out.println("No bilateral reciprocal matches found currently.");
            System.out.println("Tip: Add more skills you can teach or want to learn to increase barter matches!");
            return;
        }

        int count = 1;
        for (ReciprocalMatch m : matches) {
            System.out.printf("%d. [Match Found] Partner: %s (ID: %d | %s)%n", count++, m.getPartnerName(), m.getPartnerUserId(), m.getPartnerEmail());
            System.out.printf("   --> They Teach:  [%s] (which you want to learn)%n", m.getGivesSkillName());
            System.out.printf("   <-- You Teach:   [%s] (which they want to learn)%n", m.getGetsSkillName());
            System.out.println("   ------------------------------------------------------------------");
        }
    }

    private void viewDirectMatches() throws SQLException {
        System.out.println("\n--- DIRECT MATCHES (1-WAY TEACHING) ---");
        List<DirectMatch> matches = matchingService.getDirectMatches(currentUser.getUserId());
        if (matches.isEmpty()) {
            System.out.println("No direct mentor matches found for your learning goals.");
            return;
        }

        int count = 1;
        for (DirectMatch m : matches) {
            System.out.printf("%d. Mentor: %s (ID: %d | %s)%n", count++, m.getMentorName(), m.getMentorId(), m.getMentorEmail());
            System.out.printf("   Teaches: [%s] (ID: %d) | Level: %s%n", m.getSkillName(), m.getSkillId(), m.getExpLevel());
            System.out.println("   ------------------------------------------------------------------");
        }
    }

    private void sendPartnershipRequest() throws SQLException {
        System.out.println("\n--- SEND PARTNERSHIP REQUEST ---");
        System.out.print("Are you offering to be the MENTOR (1) or requesting as MENTEE (2)? ");
        String role = scanner.nextLine().trim();

        System.out.print("Enter Partner User ID: ");
        int partnerId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Skill ID (or press Enter if general exchange): ");
        String sLine = scanner.nextLine().trim();
        Integer skillId = sLine.isEmpty() ? null : Integer.parseInt(sLine);

        int mentorId = "1".equals(role) ? currentUser.getUserId() : partnerId;
        int menteeId = "1".equals(role) ? partnerId : currentUser.getUserId();

        try {
            Partnership p = partnershipService.requestPartnership(mentorId, menteeId, skillId);
            System.out.println("[+] Partnership request sent! ID: #" + p.getPartnershipId() + " (Status: " + p.getStatus() + ")");
        } catch (Exception e) {
            System.out.println("[!] Failed to send request: " + e.getMessage());
        }
    }

    private void managePartnerships() throws SQLException {
        System.out.println("\n--- MY PARTNERSHIPS ---");
        List<Partnership> list = partnershipService.getUserPartnerships(currentUser.getUserId());
        if (list.isEmpty()) {
            System.out.println("No active or pending partnerships.");
            return;
        }

        for (Partnership p : list) {
            System.out.printf("[#%d] Mentor: %s (ID:%d) | Mentee: %s (ID:%d) | Skill: %s | Status: [%s]%n",
                    p.getPartnershipId(),
                    p.getMentorName(), p.getMentorId(),
                    p.getMenteeName(), p.getMenteeId(),
                    p.getSkillName() != null ? p.getSkillName() : "General",
                    p.getStatus());
        }

        System.out.println("\nActions: [A]ccept | [R]eject | [C]omplete | [D]elete/Cancel | [B]ack");
        System.out.print("Choose action: ");
        String action = scanner.nextLine().trim().toUpperCase();
        if ("B".equals(action) || action.isEmpty()) return;

        System.out.print("Enter Partnership ID: ");
        int pid = Integer.parseInt(scanner.nextLine().trim());

        switch (action) {
            case "A" -> {
                partnershipService.acceptPartnership(pid, currentUser.getUserId());
                System.out.println("[+] Partnership #" + pid + " ACCEPTED!");
            }
            case "R" -> {
                partnershipService.rejectPartnership(pid, currentUser.getUserId());
                System.out.println("[+] Partnership #" + pid + " REJECTED.");
            }
            case "C" -> {
                partnershipService.completePartnership(pid, currentUser.getUserId());
                System.out.println("[+] Partnership #" + pid + " marked as COMPLETED!");
            }
            case "D" -> {
                partnershipService.cancelPartnership(pid, currentUser.getUserId());
                System.out.println("[+] Partnership #" + pid + " cancelled/deleted.");
            }
            default -> System.out.println("[!] Unknown action.");
        }
    }

    private void manageSkillCatalog() throws SQLException {
        System.out.println("\n--- MASTER SKILLS CATALOG ---");
        System.out.println("1. List All Catalog Skills");
        System.out.println("2. Search Catalog by Keyword");
        System.out.println("3. Add New Skill to Catalog");
        System.out.print("Select: ");
        String opt = scanner.nextLine().trim();

        if ("1".equals(opt)) {
            displayCatalog();
        } else if ("2".equals(opt)) {
            System.out.print("Enter keyword to search: ");
            String q = scanner.nextLine().trim();
            List<Skill> results = skillService.searchSkills(q);
            if (results.isEmpty()) {
                System.out.println("No matching skills found.");
            } else {
                for (Skill s : results) {
                    System.out.printf("[%d] %s (Category: %s)%n", s.getSkillId(), s.getSkillName(), s.getCategory());
                }
            }
        } else if ("3".equals(opt)) {
            System.out.print("Enter Skill Name: ");
            String sname = scanner.nextLine().trim();
            System.out.print("Enter Category: ");
            String cat = scanner.nextLine().trim();
            Skill created = skillService.addSkill(sname, cat);
            System.out.println("[+] Skill added to catalog: " + created);
        }
    }

    private void displayCatalog() throws SQLException {
        List<Skill> skills = skillService.getAllSkills();
        System.out.println("\n--- SKILLS CATALOG ---");
        for (Skill s : skills) {
            System.out.printf("[%d] %s (%s)%n", s.getSkillId(), s.getSkillName(), s.getCategory());
        }
    }
}
