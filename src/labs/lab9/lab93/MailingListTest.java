package labs.lab9.lab93;

import javax.swing.event.ListDataEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

interface User {
    void notify(String mailingListName, String text);
}

class MailingListUser implements User {
    private final String name;
    private final String email;

    public MailingListUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public void notify(String mailingListName, String text) {
        System.out.println("[USER] " + name + " received email from " + mailingListName + ": " + text);
    }
}

class FilteredMailingListUser implements User {
    private final String name;
    private final String email;
    private final String keyword;

    public FilteredMailingListUser(String name, String email, String keyword) {
        this.name = name;
        this.email = email;
        this.keyword = keyword;
    }

    @Override
    public void notify(String mailingListName, String text) {
        if (text.toLowerCase().contains(keyword.toLowerCase()))
            System.out.println("[FILTERED USER] " + name + " received filtered email from " + mailingListName + ": " + text);
    }
}

class AdminUser implements User {
    private final String name;
    private final String email;

    public AdminUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public void notify(String mailingListName, String text) {
        System.out.println("[ADMIN LOG] MailingList=" + mailingListName + " | Message=" + text);
    }
}

interface MailingList {
    void subscribe(User user);

    void unsubscribe(User user);

    void publish(String text);
}

class SimpleMailingList implements MailingList {
    private final String listName;
    private final Set<User> users = new LinkedHashSet<>();

    public SimpleMailingList(String listName) {
        this.listName = listName;
    }

    @Override
    public void subscribe(User user) {
        users.add(user);
    }

    @Override
    public void unsubscribe(User user) {
        users.remove(user);
    }

    @Override
    public void publish(String text) {
        users.forEach(user -> user.notify(listName, text));
    }
}

public class MailingListTest {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());

        Map<String, MailingList> mailingLists = new HashMap<>();
        Map<String, User> usersByEmail = new HashMap<>();

        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            String[] parts = line.split(" ");

            String command = parts[0];

            switch (command) {

                case "CREATE_LIST": {
                    String listName = parts[1];
                    mailingLists.put(listName, new SimpleMailingList(listName));
                    break;
                }

                case "ADD_USER": {
                    String listName = parts[1];
                    String type = parts[2];
                    String name = parts[3];
                    String email = parts[4];

                    User user;
                    if (type.equals("NORMAL")) {
                        user = new MailingListUser(name, email);
                    } else if (type.equals("FILTERED")) {
                        String keyword = parts[5];
                        user = new FilteredMailingListUser(name, email, keyword);
                    } else { // ADMIN
                        user = new AdminUser(name, email);
                    }

                    usersByEmail.put(email, user);
                    mailingLists.get(listName).subscribe(user);
                    break;
                }

                case "REMOVE_USER": {
                    String listName = parts[1];
                    String email = parts[2];

                    User user = usersByEmail.get(email);
                    mailingLists.get(listName).unsubscribe(user);
                    break;
                }

                case "PUBLISH": {
                    String listName = parts[1];
                    String text = line.substring(
                            line.indexOf(listName) + listName.length() + 1
                    );
                    mailingLists.get(listName).publish(text);
                    break;
                }
            }
        }
    }
}
