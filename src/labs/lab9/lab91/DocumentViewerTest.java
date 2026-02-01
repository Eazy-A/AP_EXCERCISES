package labs.lab9.lab91;


import java.util.*;

// component
interface Document {
    String getText();
    String getId();
}

// concrete component
class PlainText implements Document {
    private final String id;
    private final String text;

    public PlainText(String id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }
}

// base decorator
abstract class DocumentDecorator implements Document {
    Document decoratedDocument;

    public DocumentDecorator(Document decoratedDocument) {
        this.decoratedDocument = decoratedDocument;
    }


    @Override
    public String getText() {
        return decoratedDocument.getText();
    }


    @Override
    public String getId() {
        return decoratedDocument.getId();
    }
}

// concrete decorator
class LineNumberDecorator extends DocumentDecorator {
    public LineNumberDecorator(Document decoratedDocument) {
        super(decoratedDocument);
    }

    @Override
    public String getText() {
        String[] lines = decoratedDocument.getText().split("\n");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            sb.append(i + 1).append(": ").append(lines[i]).append("\n");
        }
        return sb.toString();
    }
}

class WordCountDecorator extends DocumentDecorator {
    public WordCountDecorator(Document decoratedDocument) {
        super(decoratedDocument);
    }

    @Override
    public String getText() {
        String[] lines = decoratedDocument.getText().split("\n");
        StringBuilder sb = new StringBuilder();

        int wordCount = 0;
        for (String line : lines) {
            sb.append(line).append("\n");
            wordCount += line.split("\\s+").length;
        }

        sb.append("Words: ").append(wordCount).append("\n");

        return sb.toString();
    }
}

class RedactionDecorator extends DocumentDecorator {
    private final List<String> forbiddenWords;

    public RedactionDecorator(Document decoratedDocument, List<String> forbiddenWords) {
        super(decoratedDocument);
        this.forbiddenWords = forbiddenWords;
    }

    @Override
    public String getText() {
        String text = decoratedDocument.getText();

        for (String word : forbiddenWords) {
            // (?i) is the regex flag for case-insensitive
//          String regex = "(?i)" + word;  valid but the task wants literals

            String regex = "(?i)\\b\\Q" + word + "\\E\\b";
            text = text.replaceAll(regex, "*");
        }
        return text;
    }
}

class DocumentViewer {
    private final List<Document> documents;

    public DocumentViewer() {
        documents = new ArrayList<>();
    }

    public void addDocument(String id, String text) {
        documents.add(new PlainText(id, text));
    }

    public void enableLineNumbers(String id) {
        for (int i = 0; i < documents.size(); i++) {
            Document d = documents.get(i);
            if (d.getId().equals(id)) {
                documents.set(i, new LineNumberDecorator(d));
                break;
            }
        }
    }

    public void enableWordCount(String id) {
        for (int i = 0; i < documents.size(); i++) {
            Document d = documents.get(i);
            if (d.getId().equals(id)) {
                documents.set(i, new WordCountDecorator(d));
                break;
            }
        }
    }

    public void enableRedaction(String id, List<String> forbiddenWords) {
        for (int i = 0; i < documents.size(); i++) {
            Document d = documents.get(i);
            if (d.getId().equals(id)) {
                documents.set(i, new RedactionDecorator(d, forbiddenWords));
                break;
            }
        }
    }

    public void display(String id) {
        System.out.println("=== Document " + id + " ===");
        for (Document d : documents) {
            if (d.getId().equals(id)) {
                System.out.print(d.getText());
                break;
            }
        }
    }
}

public class DocumentViewerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DocumentViewer viewer = new DocumentViewer();

        int n = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < n; i++) {
            String id = scanner.nextLine();
            int numLines = Integer.parseInt(scanner.nextLine());

            StringBuilder textBuilder = new StringBuilder();
            for (int j = 0; j < numLines; j++) {
                textBuilder.append(scanner.nextLine()).append("\n");
            }
            viewer.addDocument(id, textBuilder.toString());
        }
        Set<String> ids = new HashSet<>();
        while (scanner.hasNext()) {
            String[] commandLineWords = scanner.nextLine().split("\\s+");
            String command = commandLineWords[0];
            if (command.equals("exit")) break;

            String id = commandLineWords[1];
            ids.add(id);
            List<String> forbiddenWords = new ArrayList<>();


            if (command.equals("enableLineNumbers")) {
                viewer.enableLineNumbers(id);
            }
            if (command.equals("enableWordCount")) {
                viewer.enableWordCount(id);
            }
            if (command.equals("enableRedaction")) {
                for (int i = 2; i < commandLineWords.length; i++) {
                    forbiddenWords.add(commandLineWords[i]);
                }
                viewer.enableRedaction(id, forbiddenWords);
            }
        }

        for (String id : ids) {
            viewer.display(id);
        }
    }
}

