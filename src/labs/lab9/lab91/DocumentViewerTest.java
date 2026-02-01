package labs.lab9.lab91;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

interface Document {
    String getText();
}

class SimpleDocument implements Document {
    private final String id;
    private final String text;

    public SimpleDocument(String id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}

abstract class DocumentDecorator implements Document {
    protected Document document;

    public DocumentDecorator(Document document) {
        if (document == null){
            throw new IllegalArgumentException("Wrapped object cannot be null");
        }
        this.document = document;
    }

    @Override
    public String getText() {
        return document.getText();
    }
}

class LineNumbersDecorator extends DocumentDecorator {

    public LineNumbersDecorator(Document document) {
        super(document);
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        String[] lines = super.getText().split("\n");
        Arrays.stream(lines).forEach(line -> sb.append(count.getAndIncrement()).append(": ").append(line).append("\n"));

        return sb.toString();
    }
}

class WordCountDecorator extends DocumentDecorator {
    public WordCountDecorator(Document document) {
        super(document);
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        String[] words = super.getText().split("\\s++");
        sb.append(super.getText()).append("Words: ").append(words.length);
        return sb.toString();
    }
}

class RedactionDecorator extends DocumentDecorator {
    private final List<String> forbiddenWords;

    public RedactionDecorator(Document document, List<String> forbiddenWords) {
        super(document);
        this.forbiddenWords = forbiddenWords;
    }

    @Override
    public String getText() {
        String text = super.getText();
        for (String forbiddenWord : forbiddenWords) {
            text = text.replaceAll("(?i)" + forbiddenWord, "*");
        }
        return text;
    }
}

class DocumentViewer {
    private final Map<String, Document> documents = new HashMap<>();


    public DocumentViewer() {
    }

    public void addDocument(String id, String text) {
        documents.put(id, new SimpleDocument(id, text));
    }

    public void enableLineNumbers(String id) {
        documents.compute(id, (k, document) -> {
            if (document == null) return null;
            return new LineNumbersDecorator(document);
        });

    }

    public void enableWordCount(String id) {
        documents.compute(id, (k, document) -> new WordCountDecorator(document));
    }

    public void enableRedaction(String id, List<String> forbiddenWords) {
        documents.compute(id, (k, document) -> new RedactionDecorator(document, forbiddenWords));
    }

    public void display(String id) {
        System.out.println("=== Document "+ id +" ===");
        System.out.println(documents.get(id).getText());
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
