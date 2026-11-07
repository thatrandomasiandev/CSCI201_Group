# Testing Plan and Code
## Real-Time Multiplayer Quiz Game
**CSCI 201 — Fall 2026 · Team 12**  
**Due:** November 8, 2026  
**Companion code:** `testing/` (Maven + JUnit 5)

---

## 1. Purpose

This document describes the tests required to show that the quiz application behaves correctly. It covers authentication, room/lobby management, timed questions, answers and statistics, scoring, disconnects, and rankings. The accompanying code implements and executes the automated unit, white-box, black-box, and regression tests for the core server-side algorithms identified in the Detailed Design. Additional integration and UI tests may be added after this submission as the full Tomcat application is completed.

---

## 2. Testing Types

| Type | How we use it |
|------|----------------|
| **Unit testing** | JUnit tests for isolated classes (`Scoring`, `DisplayNames`, `RoomCodes`, `AnswerTracker`, `DeadlineGate`, `Ranking`) |
| **Black-box testing** | Cases defined only by inputs and expected outputs, without relying on internal structure |
| **White-box testing** | Cases chosen to exercise branches and guards (invalid `k`, frozen tracker, collision retry) |
| **Regression testing** | Named suite (`RegressionSuiteTest`) that re-checks bugs we do not want to reintroduce (answer-count inflation, late accepts, tie-break order) |

Manual / system tests for the browser UI and WebSocket flows are listed in §4 and will be automated or scripted as endpoints become available.

---

## 3. How to Run the Automated Tests

Requirements: Java 17+, Maven 3.8+.

```bash
cd testing
mvn test
```

All tests under `src/test/java` should pass. Source under test lives in `src/main/java/edu/usc/csci201/quiz/`.

---

## 4. Test Cases (Inputs and Expected Outputs)

IDs are stable for regression tracking. **Auto** = implemented in JUnit now. **Manual** = execute against the running app when available.

### 4.1 Authentication and access control

| ID | Type | Input | Expected output | Status |
|----|------|-------|-----------------|--------|
| AUTH-01 | Black-box | Register with unique username, valid email, password ≥ 8 chars | Account created; user can log in | Manual |
| AUTH-02 | Black-box | Register with an existing username | Error; no second account | Manual |
| AUTH-03 | Black-box | Login with correct username and password | Session established; Create/Join Room available | Manual |
| AUTH-04 | Black-box | Login with wrong password | Error; no session | Manual |
| AUTH-05 | Black-box | Guest opens Create Room or Join Room URL without login | Redirected or blocked; guest cannot enter a room | Manual |
| AUTH-06 | Black-box | Logout then call a protected room API | Unauthorized / redirected to login | Manual |

### 4.2 Room and lobby management

| ID | Type | Input | Expected output | Status |
|----|------|-------|-----------------|--------|
| ROOM-01 | Unit / black-box | Generate code with empty active set | 6-character `[A-Z0-9]` code | Auto (`RoomCodesTest`) |
| ROOM-02 | Unit / white-box | Generate code while 50 codes already active | New code not in the active set | Auto (`RoomCodesTest`) |
| ROOM-03 | Black-box | Authenticated host creates a room | Room created; host is in lobby; join code shown | Manual |
| ROOM-04 | Black-box | Join with valid code and unique display name | Player appears in lobby for all connected clients | Manual |
| ROOM-05 | Unit / black-box | Join name `"Alex"` when `"alex"` taken | Name rejected as unavailable | Auto (`DisplayNamesTest`) |
| ROOM-06 | Unit / black-box | Join name `"  Blake  "` when unused | Normalized to `"Blake"`; available | Auto (`DisplayNamesTest`) |
| ROOM-07 | Black-box | Join with invalid code | Error; remain outside room | Manual |
| ROOM-08 | Black-box | Join when lobby is locked | Error; join refused | Manual |
| ROOM-09 | Black-box | Join after game has started | Error; join refused | Manual |
| ROOM-10 | Black-box | Non-host attempts Start Game | Rejected; only host may start | Manual |
| ROOM-11 | Black-box | Host locks lobby, then unlocks | Lock flag updates for all clients | Manual |

### 4.3 Questions and timing

| ID | Type | Input | Expected output | Status |
|----|------|-------|-----------------|--------|
| TIME-01 | Unit / black-box | Receipt time ≤ deadline | Answer accepted by gate | Auto (`DeadlineGateTest`) |
| TIME-02 | Unit / black-box | Receipt time = deadline + 1 ms | Answer rejected by gate | Auto (`DeadlineGateTest`) |
| TIME-03 | Black-box | Host starts question with 20 s limit | All players receive same prompt, choices, and deadline | Manual |
| TIME-04 | Black-box | Client submits after deadline | Server rejects; no score change for that submit | Manual |
| TIME-05 | Black-box | Question set exhausted | Game ends; final leaderboard shown | Manual |
| TIME-06 | Black-box | Host does not press Next | Next question does not auto-start | Manual |

### 4.4 Answers and live statistics

| ID | Type | Input | Expected output | Status |
|----|------|-------|-----------------|--------|
| ANS-01 | Unit / white-box | First submit from player 1 choice `"A"` | Count = 1; stored answer `"A"` | Auto (`AnswerTrackerTest`) |
| ANS-02 | Unit / white-box | Player 1 changes `"A"` → `"B"` | Count still 1; stored answer `"B"` | Auto (`AnswerTrackerTest`) |
| ANS-03 | Unit | Players 1 and 2 each submit once | Count = 2 | Auto (`AnswerTrackerTest`) |
| ANS-04 | Unit | Submit after freeze | Rejected; prior answers unchanged | Auto (`AnswerTrackerTest`) |
| ANS-05 | Black-box | Choice not in listed options | Rejected by server | Manual |
| ANS-06 | Black-box | At deadline, freeze and reveal | Correct choice shown; correct-count published | Manual |

### 4.5 Scoring and session completion

| ID | Type | Input | Expected output | Status |
|----|------|-------|-----------------|--------|
| SCORE-01 | Unit / black-box | Incorrect, any response time | 0 points | Auto (`ScoringTest`) |
| SCORE-02 | Unit / black-box | Correct, 0 ms, K = 200 | 100 points | Auto (`ScoringTest`) |
| SCORE-03 | Unit / black-box | Correct, 5000 ms, K = 200 | 75 points | Auto (`ScoringTest`) |
| SCORE-04 | Unit / black-box | Correct, 100000 ms, K = 200 | 0 points (floor) | Auto (`ScoringTest`) |
| SCORE-05 | Unit / white-box | K ≤ 0 or negative response time | `IllegalArgumentException` | Auto (`ScoringTest`) |
| SCORE-06 | Unit / black-box | Scores 90 vs 50 | Higher score ranks first | Auto (`RankingTest`) |
| SCORE-07 | Unit / black-box | Equal scores, join orders 2, 0, 1 | Order by join order 0, then 1, then 2 | Auto (`RankingTest`) |
| SCORE-08 | Black-box | Full round with two players | Totals update once per player per question | Manual |

### 4.6 Disconnect and reconnect

| ID | Type | Input | Expected output | Status |
|----|------|-------|-----------------|--------|
| DISC-01 | Black-box | Non-host disconnect mid-question | Prior answers and score kept; others continue | Manual |
| DISC-02 | Black-box | Non-host reconnects | Receives current room state; can play again | Manual |
| DISC-03 | Black-box | Host disconnects; question ends | Session pauses until host returns or timeout | Manual |
| DISC-04 | Black-box | Host absent beyond timeout | Session ends | Manual |

### 4.7 Regression suite (automated)

| ID | Type | Behavior locked in | Status |
|----|------|--------------------|--------|
| REG-001 | Regression | Changing an answer must not inflate the live answered count | Auto |
| REG-002 | Regression | Late answers rejected; freeze keeps the last accepted answer | Auto |
| REG-003 | Regression | Tied scores ordered by join order, not submit order | Auto |
| REG-004 | Regression | Display names unique ignoring case | Auto |
| REG-005 | Regression | Incorrect answers never earn points even if fast | Auto |

---

## 5. Traceability to Technical Specifications

| Spec area | Covered by |
|-----------|------------|
| Room and lobby management | ROOM-*, DisplayNames, RoomCodes |
| Questions and timing | TIME-*, DeadlineGate |
| Answers and statistics | ANS-*, AnswerTracker |
| Scoring and completion | SCORE-*, Ranking, Scoring |
| Accounts / guest vs auth | AUTH-* |
| Disconnect behavior | DISC-* |
| Concurrency / single response | ANS-02, REG-001, AnswerTracker |

---

## 6. Code Inventory

| Path | Role |
|------|------|
| `testing/src/main/java/.../Scoring.java` | Time-adjusted points |
| `testing/src/main/java/.../DisplayNames.java` | Lobby name rules |
| `testing/src/main/java/.../RoomCodes.java` | Unique join codes |
| `testing/src/main/java/.../AnswerTracker.java` | Insert / replace / freeze / count |
| `testing/src/main/java/.../DeadlineGate.java` | Accept vs reject by deadline |
| `testing/src/main/java/.../Ranking.java` | Leaderboard ordering |
| `testing/src/test/java/.../*Test.java` | Unit, black-box, white-box cases |
| `testing/src/test/java/.../RegressionSuiteTest.java` | Regression suite |

These classes are the designed core algorithms from the Detailed Design and will be integrated into the Tomcat server as implementation proceeds.

---

## 7. Future Tests (post-submission)

- Integration tests for REST register/login/create/join against an embedded Tomcat or test server.
- WebSocket protocol tests for lobby broadcast, answer_count, and round_result messages.
- Load smoke test: 20 concurrent players in one room (performance targets from the Technical Specifications).
- UI smoke checklist for all nine pages.

---

## 8. Rubric Checklist

| Requirement | Location |
|-------------|----------|
| Test cases cover functionality with inputs and expected outputs | §4 |
| Multiple testing types (unit, black-box, white-box, regression) | §2, §4, code |
| Document + code submitted | This file + `testing/` |
| Professional formatting / grammar | This document |

---

*Team 12 — Testing Plan and Code for Real-Time Multiplayer Quiz Game*
