# Community Skill Finder

A database-driven **Command-Line Application (CLI)** designed to facilitate peer-to-peer technical education. The platform pairs users with complementary teaching abilities and learning goals through a **reciprocal-first matching algorithm**, establishing direct, cost-free technical skill exchange.

---

## 🏛️ System Architecture

The project follows a clean **4-Tier Modular Architecture**:
$$\text{Interactive Console CLI} \longrightarrow \text{Java Service Layer} \longrightarrow \text{JDBC DAO Layer} \longrightarrow \text{MySQL Database (3NF)}$$

- **Presentation Layer**: [`ConsoleApp.java`](src/main/java/com/communityskillfinder/cli/ConsoleApp.java) – Menu-driven interactive terminal navigation.
- **Service Layer**:
  - [`UserService.java`](src/main/java/com/communityskillfinder/service/UserService.java) – User onboarding, profile, teaching/learning skill inventories.
  - [`SkillService.java`](src/main/java/com/communityskillfinder/service/SkillService.java) – Technical taxonomy catalog and keyword searching.
  - [`MatchingService.java`](src/main/java/com/communityskillfinder/service/MatchingService.java) – Reciprocal-first bilateral barter matching & 1-way direct matching.
  - [`PartnershipService.java`](src/main/java/com/communityskillfinder/service/PartnershipService.java) – Exchange requests and lifecycle state machine.
- **DAO Layer**: PreparedStatements preventing SQL injection:
  - [`UserDAO.java`](src/main/java/com/communityskillfinder/dao/UserDAO.java)
  - [`SkillDAO.java`](src/main/java/com/communityskillfinder/dao/SkillDAO.java)
  - [`UserTeachSkillDAO.java`](src/main/java/com/communityskillfinder/dao/UserTeachSkillDAO.java)
  - [`UserLearnSkillDAO.java`](src/main/java/com/communityskillfinder/dao/UserLearnSkillDAO.java)
  - [`PartnershipDAO.java`](src/main/java/com/communityskillfinder/dao/PartnershipDAO.java)
- **Entities & DTOs**:
  - [`User.java`](src/main/java/com/communityskillfinder/model/User.java)
  - [`Skill.java`](src/main/java/com/communityskillfinder/model/Skill.java)
  - [`UserTeachSkill.java`](src/main/java/com/communityskillfinder/model/UserTeachSkill.java)
  - [`UserLearnSkill.java`](src/main/java/com/communityskillfinder/model/UserLearnSkill.java)
  - [`Partnership.java`](src/main/java/com/communityskillfinder/model/Partnership.java)
  - [`ReciprocalMatch.java`](src/main/java/com/communityskillfinder/model/ReciprocalMatch.java)
  - [`DirectMatch.java`](src/main/java/com/communityskillfinder/model/DirectMatch.java)

---

## 🗄️ Database Setup (MySQL 8.0+)

1. Start your local MySQL server.
2. Run the SQL schema and seed data file:
   ```bash
   mysql -u root -p < schema.sql
   ```
3. Configure your database credentials in [`src/main/resources/application.properties`](src/main/resources/application.properties) or set environment variables:
   ```properties
   db.url=jdbc:mysql://localhost:3306/community_skill_finder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=your_password
   ```

---

## 🚀 Compiling and Running

### Option A: Standard Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.communityskillfinder.cli.ConsoleApp"
```

### Option B: Direct `javac` & `java`
```bash
# Compile all source files into bin directory
javac -d bin src/main/java/com/communityskillfinder/*/*.java src/main/java/com/communityskillfinder/*/*/*.java src/main/java/com/communityskillfinder/model/*.java

# Run CLI (include mysql-connector-j jar in the classpath)
java -cp "bin;path/to/mysql-connector-j-8.3.0.jar;src/main/resources" com.communityskillfinder.cli.ConsoleApp
```

---

## 🔄 Reciprocal Matching Algorithm Explained

A bilateral match occurs when:
1. **User A** can teach a skill that **User B** wants to learn.
2. **User B** can teach a skill that **User A** wants to learn.

The system executes this via a nested SQL join in [`PartnershipDAO.java`](src/main/java/com/communityskillfinder/dao/PartnershipDAO.java), enabling instant zero-cost skill bartering between motivated learners.
