package labs.lab8.lab83;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


enum ActionType {
    JOIN_GAME,
    LEAVE_GAME,
    ATTACK
}


class Player {
    private final String id;
    private int score;

    public Player(String id) {
        this.id = id;
        this.score = 0;
    }

    // TODO: Implement addScore function
    public synchronized void addScore(int delta) {
        score += delta;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", score=" + score +
                '}';
    }
}


class PlayerAction {
    private final String playerId;
    private final ActionType action;

    public PlayerAction(String playerId, ActionType action) {
        this.playerId = playerId;
        this.action = action;
    }

    public String getPlayerId() {
        return playerId;
    }

    public ActionType getActionType() {
        return action;
    }

    public int getProcessingTime() {
        switch (action) {
            case JOIN_GAME:
                return 20;
            case LEAVE_GAME:
                return 30;
            case ATTACK:
                return 5;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "PlayerAction{" +
                "playerId='" + playerId + '\'' +
                ", action=" + action +
                '}';
    }
}

class RoomAction {
    final String roomId;
    final PlayerAction action;

    RoomAction(String roomId, PlayerAction action) {
        this.roomId = roomId;
        this.action = action;
    }
}


class GameRoom {

    public final String roomId;
    public final Map<String, Player> players = new ConcurrentHashMap<>();

    private final BlockingQueue<PlayerAction> actionQueue =
            new LinkedBlockingQueue<>();

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public volatile boolean running = true;
    private long totalTime = 0L;

    public GameRoom(String roomId) {
        this.roomId = roomId;
        startProcessor();
    }

    // TODO: Implement startProcessor
    private void startProcessor() {
        executor.submit(() -> {
            while (running || !actionQueue.isEmpty()) {
                try {
                    PlayerAction action = actionQueue.poll(100, TimeUnit.MILLISECONDS);
                    totalTime+=action.getProcessingTime();

                    if (action != null) {
                        processAction(action);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void submitAction(PlayerAction action) {
        System.out.println("[" + roomId + "] RECEIVED: " + action);
        actionQueue.offer(action);
    }

    private void processAction(PlayerAction action) {
        try {
            Thread.sleep(action.getProcessingTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        switch (action.getActionType()) {
            case JOIN_GAME:
                players.putIfAbsent(
                        action.getPlayerId(),
                        new Player(action.getPlayerId())
                );
                System.out.println("[" + roomId + "] JOIN: "
                        + action.getPlayerId());
                break;

            case LEAVE_GAME:
                if (players.remove(action.getPlayerId()) != null) {
                    System.out.println("[" + roomId + "] LEAVE: "
                            + action.getPlayerId());
                } else {
                    System.out.println("[" + roomId
                            + "] LEAVE IGNORED (not in room): "
                            + action.getPlayerId());
                }
                break;

            case ATTACK:
                Player p = players.get(action.getPlayerId());
                if (p == null) {
                    System.out.println("[" + roomId
                            + "] ATTACK IGNORED (not in room): "
                            + action.getPlayerId());
                } else {
                    p.addScore(10);
                    System.out.println("[" + roomId + "] ATTACK: " + p);
                }
                break;
        }
        System.out.println("Action processing time : " + action.getProcessingTime() + " ms");
    }

    public void shutdown() {
        // TODO: Add missing logic
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + roomId + "] FINAL PLAYERS:");
        players.values().forEach(p ->
                System.out.println("  " + p));
    }
}

class GameServer {

    private final BlockingQueue<RoomAction> inputQueue =
            new LinkedBlockingQueue<>();

    private final ConcurrentHashMap<String, GameRoom> rooms =
            new ConcurrentHashMap<>();

    private final ExecutorService dispatcher =
            Executors.newSingleThreadExecutor();

    public volatile boolean running = true;

    public AtomicLong singleThreadProcessingTime = new AtomicLong(0);

    public GameServer() {
        startDispatcher();
    }

    // TODO: Implement startDispatcher()
    private void startDispatcher() {
        dispatcher.submit(() -> {
            while (running || !inputQueue.isEmpty()) {
                try {
                    RoomAction roomAction = inputQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (roomAction != null) {
                        GameRoom room = rooms.computeIfAbsent(roomAction.roomId, GameRoom::new);
                        room.submitAction(roomAction.action);
                        System.out.println("Room action time: " + roomAction.action.getProcessingTime() + " ms");
                        singleThreadProcessingTime.getAndAdd(roomAction.action.getProcessingTime());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        });
    }

    public void submit(String roomId, PlayerAction action) {
        inputQueue.offer(new RoomAction(roomId, action));
    }

    // TODO: Implement GameServer shutdown() method
    public void shutdown() {
        running = false;
        dispatcher.shutdown();
        try {
            dispatcher.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            dispatcher.shutdownNow();
        }
        rooms.values().forEach(GameRoom::shutdown);
    }
}


public class Main {

    public static void main(String[] args) throws IOException {

        GameServer server = new GameServer();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {

            final String input = line.trim();

            try {
                String[] parts = input.split(",");
                if (parts.length != 3) {
                    System.err.println("Invalid input: " + input);
                    return;
                }

                String roomId = parts[0].trim();
                String playerId = parts[1].trim();
                ActionType actionType =
                        ActionType.valueOf(parts[2].trim());

                PlayerAction action =
                        new PlayerAction(playerId, actionType);

                server.submit(roomId, action);

            } catch (Exception e) {
                System.err.println(
                        "Failed to process line: " + input
                );
                e.printStackTrace();
            }
        }

        reader.close();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        server.shutdown();

        System.out.println("Game server stopped.");
    }
}

