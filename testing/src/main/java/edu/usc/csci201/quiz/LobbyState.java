package edu.usc.csci201.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory lobby for one room: join rules, lock, host controls, and game-start gate.
 */
public final class LobbyState {

  public enum Phase {
    LOBBY,
    IN_GAME,
    ENDED
  }

  public record PlayerSeat(int playerId, String displayName, int joinOrder, boolean host) {}

  private final int hostPlayerId;
  private final Map<Integer, PlayerSeat> seats = new LinkedHashMap<>();
  private final Map<String, Integer> nameKeys = new LinkedHashMap<>();
  private boolean locked;
  private Phase phase = Phase.LOBBY;
  private int nextJoinOrder;

  public LobbyState(int hostPlayerId, String hostDisplayName) {
    this.hostPlayerId = hostPlayerId;
    joinInternal(hostPlayerId, hostDisplayName, true);
  }

  public synchronized void setLocked(boolean locked) {
    requireLobby();
    this.locked = locked;
  }

  public synchronized boolean isLocked() {
    return locked;
  }

  public synchronized Phase phase() {
    return phase;
  }

  public synchronized List<PlayerSeat> players() {
    return List.copyOf(seats.values());
  }

  public synchronized void join(int playerId, String displayName) {
    requireLobby();
    if (locked) {
      throw new IllegalStateException("lobby is locked");
    }
    if (seats.containsKey(playerId)) {
      throw new IllegalStateException("player already in lobby");
    }
    String key = DisplayNames.uniquenessKey(displayName);
    if (key.isEmpty()) {
      throw new IllegalArgumentException("display name is blank");
    }
    if (nameKeys.containsKey(key)) {
      throw new IllegalStateException("display name already taken");
    }
    joinInternal(playerId, DisplayNames.normalize(displayName), false);
  }

  public synchronized void removePlayer(int actingHostId, int targetPlayerId) {
    requireLobby();
    requireHost(actingHostId);
    if (targetPlayerId == hostPlayerId) {
      throw new IllegalStateException("cannot remove host");
    }
    PlayerSeat removed = seats.remove(targetPlayerId);
    if (removed == null) {
      throw new IllegalStateException("player not in lobby");
    }
    nameKeys.remove(DisplayNames.uniquenessKey(removed.displayName()));
  }

  public synchronized void startGame(int actingHostId) {
    requireLobby();
    requireHost(actingHostId);
    if (seats.size() < 1) {
      throw new IllegalStateException("no players");
    }
    phase = Phase.IN_GAME;
  }

  public synchronized void endGame() {
    phase = Phase.ENDED;
  }

  private void joinInternal(int playerId, String displayName, boolean host) {
    String key = DisplayNames.uniquenessKey(displayName);
    PlayerSeat seat = new PlayerSeat(playerId, DisplayNames.normalize(displayName), nextJoinOrder++, host);
    seats.put(playerId, seat);
    nameKeys.put(key, playerId);
  }

  private void requireLobby() {
    if (phase != Phase.LOBBY) {
      throw new IllegalStateException("lobby is not open for this action");
    }
  }

  private void requireHost(int actingHostId) {
    if (actingHostId != hostPlayerId) {
      throw new IllegalStateException("only the host may perform this action");
    }
  }

  public synchronized List<String> displayNamesInJoinOrder() {
    List<String> names = new ArrayList<>();
    for (PlayerSeat seat : seats.values()) {
      names.add(seat.displayName());
    }
    return Collections.unmodifiableList(names);
  }
}
