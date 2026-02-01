package labs.lab5.lab53;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class Book {
    private final String isbn;
    private final String title;
    private final int year;
    private final String author;
    private int numCopies;
    private int totalBorrows;

    public Book(String isbn, String title, int year, String author, int numCopies) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.author = author;
        this.numCopies = numCopies;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getAuthor() {
        return author;
    }

    public int getNumCopies() {
        return numCopies;
    }

    public void setNumCopies(int numCopies) {
        this.numCopies = numCopies;
    }

    public int getTotalBorrows() {
        return totalBorrows;
    }

    public void incrementBorrows() {
        totalBorrows++;
    }
}

class Member {
    private final String id;
    private final String name;
    private int totalBorrows;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalBorrows() {
        return totalBorrows;
    }

    public void incrementTotalBorrows() {
        totalBorrows++;
    }
}

class LibrarySystem {
    private String name;
    List<Book> books = new ArrayList<>();
    List<Member> members = new ArrayList<>();
    Map<Member, List<Book>> borrowedBooks = new HashMap<>();
    Map<String, Queue<Member>> waitingList = new HashMap<>();

    public LibrarySystem(String name) {
        this.name = name;
    }

    public void registerMember(String id, String fullName) {
        Member newMember = new Member(id, fullName);
        members.add(newMember);
        borrowedBooks.computeIfAbsent(newMember, k -> new ArrayList<Book>());
    }

    public void addBook(String isbn, String title, String author, int year) {
        books.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .ifPresentOrElse(
                        b -> b.setNumCopies(b.getNumCopies() + 1),
                        () -> books.add(new Book(isbn, title, year, author, 1))
                );
    }

    public void borrowBook(String memberId, String isbn) {
        members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .ifPresentOrElse(
                        member -> {
                            books.stream()
                                    .filter(b -> b.getIsbn().equals(isbn))
                                    .findFirst()
                                    .ifPresentOrElse(
                                            book -> {
                                                if (book.getNumCopies() > 0) {
                                                    book.setNumCopies(book.getNumCopies() - 1);
                                                    borrowedBooks.get(member).add(book);
                                                    book.incrementBorrows();
                                                    member.incrementTotalBorrows();
                                                } else {
                                                    waitingList
                                                            .computeIfAbsent(isbn, k -> new LinkedList<>())
                                                            .add(member);
//                                                    System.out.println("No copies available, added to waiting list");
                                                }

                                            },
                                            () -> System.out.println("Book not found")
                                    );
                        },
                        () -> System.out.println("Member not found")
                );
    }

    public void returnBook(String memberId, String isbn) {
        members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .ifPresentOrElse(
                        member -> borrowedBooks.get(member).stream()
                                .filter(b -> b.getIsbn().equals(isbn))
                                .findFirst()
                                .ifPresentOrElse(
                                        book -> {
                                            borrowedBooks.get(member).remove(book);

                                            Queue<Member> queue = waitingList.get(isbn);

                                            if (queue != null && !queue.isEmpty()) {
                                                Member next = queue.poll();
                                                borrowedBooks.get(next).add(book);
                                                next.incrementTotalBorrows();
                                                book.incrementBorrows();
//                                                System.out.println("Book returned and given to " + next.getName());
                                                if (queue.isEmpty()) waitingList.remove(isbn);
                                            } else {
                                                book.setNumCopies(book.getNumCopies() + 1);
//                                                System.out.println("Book returned and now available");
                                            }
                                        },
                                        () -> System.out.println("This member did not borrow that book")),
                        () -> System.out.println("Member not found")

                );
    }

    public void printMembers() {
        members.stream()
                .sorted(Comparator
                        .comparing((Member m) -> borrowedBooks.get(m).size()).reversed()
                        .thenComparing(Member::getName))
                .forEach(m -> System.out.printf(
                        "%s (%s) - borrowed now: %d, total borrows: %d%n",
                        m.getName(),
                        m.getId(),
                        borrowedBooks.get(m).size(),
                        m.getTotalBorrows()
                ));
    }

    public void printBooks() {
        books.stream()
                .sorted(Comparator
                        .comparing(Book::getTotalBorrows).reversed()
                        .thenComparing(Book::getYear))
                .forEach(b -> System.out.printf(
                        "%s - \"%s\" by %s (%d), available: %d, total borrows: %d%n",
                        b.getIsbn(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getYear(),
                        b.getNumCopies(),
                        b.getTotalBorrows()
                ));
    }


    public void printBookCurrentBorrowers(String isbn) {
        String result = borrowedBooks.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(b -> b.getIsbn().equals(isbn)))
                .map(entry -> entry.getKey().getId())
                .sorted()
                .collect(Collectors.joining(", "));

        System.out.println(result.isEmpty() ? "No current borrowers" : result);
    }

    public void printTopAuthors() {
        books.stream()
                .collect(Collectors.groupingBy(
                        Book::getAuthor,
                        Collectors.summingInt(Book::getTotalBorrows)
                ))
                .entrySet().stream()
                .sorted(Comparator
                        .comparing(Map.Entry<String, Integer>::getValue).reversed()
                        .thenComparing(Map.Entry::getKey))
                .forEach(entry -> System.out.printf("%s - %d%n", entry.getKey(), entry.getValue()));
    }

}

public class LibraryTester {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            String libraryName = br.readLine();
            //   System.out.println(libraryName); //test
            if (libraryName == null) return;

            libraryName = libraryName.trim();
            LibrarySystem lib = new LibrarySystem(libraryName);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("END")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");

                switch (parts[0]) {

                    case "registerMember": {
                        lib.registerMember(parts[1], parts[2]);
                        break;
                    }

                    case "addBook": {
                        String isbn = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        int year = Integer.parseInt(parts[4]);
                        lib.addBook(isbn, title, author, year);
                        break;
                    }

                    case "borrowBook": {
                        lib.borrowBook(parts[1], parts[2]);
                        break;
                    }

                    case "returnBook": {
                        lib.returnBook(parts[1], parts[2]);
                        break;
                    }

                    case "printMembers": {
                        lib.printMembers();
                        break;
                    }

                    case "printBooks": {
                        lib.printBooks();
                        break;
                    }

                    case "printBookCurrentBorrowers": {
                        lib.printBookCurrentBorrowers(parts[1]);
                        break;
                    }

                    case "printTopAuthors": {
                        lib.printTopAuthors();
                        break;
                    }

                    default:
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
