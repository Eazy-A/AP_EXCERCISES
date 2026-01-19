package e18;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String categoryName) {
        super("Category " + categoryName + " was not found");
    }
}

class Category {
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

interface INewsItem {
    String getTeaser();
}

abstract class NewsItem implements INewsItem {
    protected String title;
    protected LocalDateTime date;
    protected Category category;

    public NewsItem(String title, LocalDateTime date, Category category) {
        this.title = title;
        this.date = date;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
    public long getMinutesAgo() {
        return Duration.between(date, LocalDateTime.now()).toMinutes();
    }
}

class TextNewsItem extends NewsItem {
    private String text;

    public TextNewsItem(String title, LocalDateTime date, Category category, String text) {
        super(title, date, category);
        this.text = text;
    }

    @Override
    public String getTeaser() {
        int MAX_CHARACTERS = 80;
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");

        sb.append(getMinutesAgo()).append("\n");
        String shortText = "";
        if(MAX_CHARACTERS < text.length()){
        shortText = text.substring(0, MAX_CHARACTERS)  ;
        }else{
            shortText = text;
        }

        sb.append(shortText).append("\n");

        return sb.toString();
    }
}

class MediaNewsItem extends NewsItem {
    private String url;
    private int views;

    public MediaNewsItem(String title, LocalDateTime date, Category category, String url, int views) {
        super(title, date, category);
        this.url = url;
        this.views = views;
    }

    @Override
    public String getTeaser() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");

        sb.append(getMinutesAgo()).append("\n");
        sb.append(url).append("\n");
        sb.append(views).append("\n");

        return sb.toString();
    }
}

class FrontPage {
    private Category[] categories;
    private List<NewsItem> newsList = new ArrayList<>();

    public FrontPage(Category[] categories) {
        this.categories = categories;
    }

    public void addNewsItem(NewsItem newsItem) {
        newsList.add(newsItem);
    }

    public List<NewsItem> listByCategory(Category category) {
        return newsList.stream()
                .filter(newsItem -> newsItem.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
        List<NewsItem> filtered = newsList.stream()
                .filter(newsItem -> newsItem.getCategory().getName().equals(category))
                .collect(Collectors.toList());
        if (filtered.isEmpty()) throw new CategoryNotFoundException(category);
        return filtered;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        newsList.forEach(newsItem -> sb.append(newsItem.getTeaser()));
        return sb.toString();
    }
}

public class FrontPageTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];

        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }

        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);

        // First Loop: TextNewsItem
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            // Calculate time using LocalDateTime
            LocalDateTime date = LocalDateTime.now().minusMinutes(min);

            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();

            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();

        // Second Loop: MediaNewsItem
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            // Calculate time using LocalDateTime
            LocalDateTime date = LocalDateTime.now().minusMinutes(min);

            scanner.nextLine();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();

            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }

        // Execution
        if (scanner.hasNextLine()) {
            String category = scanner.nextLine();
            System.out.println(frontPage);
            for (Category c : categories) {
                System.out.println(frontPage.listByCategory(c).size());
            }
            try {
                System.out.println(frontPage.listByCategoryName(category).size());
            } catch (CategoryNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
