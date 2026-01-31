package labs.lab5.lab51;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class NoSuchRoomException extends Exception {
    public NoSuchRoomException(String message) {
        super(message);
    }
}

class NoSuchUserException extends Exception {
    public NoSuchUserException(String message) {
        super(message);
    }
}

class ChatRoom {
    private String name;
    private Set<String> users;

    public ChatRoom(String name) {
        this.name = name;
        users = new HashSet<>();
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }

    public int numUsers() {
        return users.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");

        if (users.isEmpty()) {
            sb.append("EMPTY\n");
            return sb.toString();
        }

        users.stream()
                .sorted()
                .forEach(u -> sb.append(u).append("\n"));

        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Set<String> getUsers() {
        return users;
    }
}

class ChatSystem {
    private Map<String, Set<ChatRoom>> userRooms;
    private Map<String, ChatRoom> rooms;

    public ChatSystem() {
        userRooms = new TreeMap<>();
        rooms = new TreeMap<>();
    }

    public void addRoom(String roomName) {
        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
    }

    public void removeRoom(String roomName) {
        ChatRoom room = rooms.remove(roomName);
        if (room == null) return;

        for (Set<ChatRoom> set : userRooms.values()) {
            set.remove(room);
        }
    }

    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if (!rooms.containsKey(roomName))
            throw new NoSuchRoomException(roomName);
        return rooms.get(roomName);
    }

    public void register(String userName) {
        userRooms.putIfAbsent(userName, new HashSet<>());

        if (rooms.isEmpty()) return;

        ChatRoom minRoom = rooms.values()
                .stream()
                .min(Comparator.comparing(ChatRoom::numUsers))
                .orElse(null);

        minRoom.addUser(userName);
        userRooms.get(userName).add(minRoom);
    }

    public void registerAndJoin(String userName, String roomName)
            throws NoSuchRoomException {

        userRooms.putIfAbsent(userName, new HashSet<>());

        ChatRoom room = getRoom(roomName);
        room.addUser(userName);
        userRooms.get(userName).add(room);
    }

    public void joinRoom(String userName, String roomName)
            throws NoSuchRoomException, NoSuchUserException {

        if (!userRooms.containsKey(userName))
            throw new NoSuchUserException(userName);

        ChatRoom room = getRoom(roomName);
        room.addUser(userName);
        userRooms.get(userName).add(room);
    }

    public void leaveRoom(String userName, String roomName)
            throws NoSuchRoomException, NoSuchUserException {

        if (!userRooms.containsKey(userName))
            throw new NoSuchUserException(userName);

        ChatRoom room = getRoom(roomName);
        room.removeUser(userName);
        userRooms.get(userName).remove(room);
    }

    public void followFriend(String userName, String friendUsername)
            throws NoSuchUserException {

        if (!userRooms.containsKey(userName))
            throw new NoSuchUserException(userName);

        if (!userRooms.containsKey(friendUsername))
            throw new NoSuchUserException(friendUsername);

        for (ChatRoom r : userRooms.get(friendUsername)) {
            r.addUser(userName);
            userRooms.get(userName).add(r);
        }
    }
}


public class ChatSystemTest {

    public static void main(String[] args)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchRoomException {

        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();

        if (k == 0) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr.addUser(jin.next());
                if (k == 1) cr.removeUser(jin.next());
                if (k == 2) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println(cr.toString());
            n = jin.nextInt();
            if (n == 0) return;

            ChatRoom cr2 = new ChatRoom(jin.next());
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr2.addUser(jin.next());
                if (k == 1) cr2.removeUser(jin.next());
                if (k == 2) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }

        if (k == 1) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while (true) {
                String cmd = jin.next();
                if (cmd.equals("stop")) break;
                if (cmd.equals("print")) {
                    System.out.println(cs.getRoom(jin.next()) + "\n");
                    continue;
                }

                for (Method m : mts) {
                    if (m.getName().equals(cmd)) {
                        String params[] = new String[m.getParameterTypes().length];
                        for (int i = 0; i < params.length; ++i)
                            params[i] = jin.next();

                        m.invoke(cs, (Object[]) params);
                    }
                }
            }
        }
    }
}
