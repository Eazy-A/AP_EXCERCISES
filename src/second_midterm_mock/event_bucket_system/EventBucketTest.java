package second_midterm_mock.event_bucket_system;

import java.util.*;

interface IFile {
    void print(int indent, StringBuilder sb);
    boolean isEmpty();
    default Folder asFolder(){
        return null;
    }
}

class File implements IFile {
    private final String name;

    public File(String name) {
        this.name = name;
    }

    @Override
    public void print(int indent, StringBuilder sb) {
        sb.append("    ".repeat(indent)).append(name).append("\n");
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

class Folder implements IFile {
    private final String name;
    private final Map<String, IFile> children = new LinkedHashMap<>();

    public Folder(String name) {
        this.name = name;
    }

    @Override
    public void print(int indent, StringBuilder sb) {
        sb.append("    ".repeat(indent)).append(name).append("/\n");
        for (IFile child : children.values()) {
            child.print(indent + 1, sb);
        }
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    public Map<String, IFile> getChildren() {
        return children;
    }

    @Override
    public Folder asFolder() {
        return this;
    }
}

class EventBucket {
    private final Folder root;

    public EventBucket(String name) {
        root = new Folder(name);
    }

    public void addEvent(String key) {
        String[] parts = key.split("/");
        Folder current = root;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            boolean isLastPart = (i == parts.length - 1);

            current.getChildren().putIfAbsent(part, isLastPart ? new File(part) : new Folder(part));

            if (!isLastPart){
                IFile next = current.getChildren().get(part);
                current = next.asFolder();
            }
        }
    }
    public void removeEvent(String key) {
        List<String> pathParts = new ArrayList<>(Arrays.asList(key.split("/")));
        removeFromFolder(root, pathParts);
    }
    private void removeFromFolder(Folder current, List<String> path){
        String target = path.removeFirst();
        IFile child = current.getChildren().get(target);

        if (child == null) return;

        if (!path.isEmpty()){
            Folder childFolder = child.asFolder();
            if(childFolder != null) {
                removeFromFolder((Folder) child, path);
            }
        }
        if (path.isEmpty() || child.isEmpty()){
            current.getChildren().remove(target);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        root.print(0, sb);
        return sb.toString();
    }
}

public class EventBucketTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EventBucket bucket = new EventBucket("events");

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            switch (parts[0]) {
                case "ADD":
                    bucket.addEvent(parts[1]);
                    break;
                case "REMOVE":
                    bucket.removeEvent(parts[1]);
                    break;
                case "PRINT":
                    System.out.print(bucket);
                    break;
            }
        }
    }
}
