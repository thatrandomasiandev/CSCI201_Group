package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LobbyState")
class LobbyStateTest {

  private LobbyState lobby;

  @BeforeEach
  void setUp() {
    lobby = new LobbyState(1, "Host");
  }

  @Nested
  @DisplayName("Black-box join rules")
  class JoinRules {

    @Test
    void hostIsSeatedFirst() {
      assertEquals(1, lobby.players().size());
      assertTrue(lobby.players().get(0).host());
      assertEquals(0, lobby.players().get(0).joinOrder());
    }

    @Test
    void secondPlayerJoinsWithNextJoinOrder() {
      lobby.join(2, "Alex");
      assertEquals(2, lobby.players().size());
      assertEquals(1, lobby.players().get(1).joinOrder());
      assertEquals("Alex", lobby.players().get(1).displayName());
    }

    @Test
    void rejectsDuplicateNameIgnoringCase() {
      lobby.join(2, "Alex");
      assertThrows(IllegalStateException.class, () -> lobby.join(3, "alex"));
    }

    @Test
    void rejectsBlankName() {
      assertThrows(IllegalArgumentException.class, () -> lobby.join(2, "   "));
    }

    @Test
    void rejectsJoinWhenLocked() {
      lobby.setLocked(true);
      assertTrue(lobby.isLocked());
      assertThrows(IllegalStateException.class, () -> lobby.join(2, "Blake"));
    }

    @Test
    void unlockAllowsJoinAgain() {
      lobby.setLocked(true);
      lobby.setLocked(false);
      lobby.join(2, "Blake");
      assertEquals(2, lobby.players().size());
    }
  }

  @Nested
  @DisplayName("Host controls")
  class HostControls {

    @Test
    void nonHostCannotStart() {
      lobby.join(2, "Alex");
      assertThrows(IllegalStateException.class, () -> lobby.startGame(2));
      assertEquals(LobbyState.Phase.LOBBY, lobby.phase());
    }

    @Test
    void hostCanStart() {
      lobby.join(2, "Alex");
      lobby.startGame(1);
      assertEquals(LobbyState.Phase.IN_GAME, lobby.phase());
    }

    @Test
    void joinRejectedAfterStart() {
      lobby.startGame(1);
      assertThrows(IllegalStateException.class, () -> lobby.join(2, "Alex"));
    }

    @Test
    void hostCanRemoveNonHost() {
      lobby.join(2, "Alex");
      lobby.removePlayer(1, 2);
      assertEquals(1, lobby.players().size());
    }

    @Test
    void cannotRemoveHost() {
      assertThrows(IllegalStateException.class, () -> lobby.removePlayer(1, 1));
    }

    @Test
    void nonHostCannotRemove() {
      lobby.join(2, "Alex");
      assertThrows(IllegalStateException.class, () -> lobby.removePlayer(2, 2));
    }
  }
}
