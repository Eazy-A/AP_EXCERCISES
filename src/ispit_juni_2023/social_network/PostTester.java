package ispit_juni_2023.social_network;

import java.util.*;


interface IReply {
    void addComment(String username, String commentId, String content, String replyToId);
    void likeComment(String commentId);
    String print(int indent);
    int totalLikes();
}

class Comment implements IReply{
    private final String username, commentId, content, replyToId;
    private final Map<String, IReply> replies = new LinkedHashMap<>();
    private int likes = 0;

    public Comment(String username, String commentId, String content, String replyToId) {
        this.username = username;
        this.commentId = commentId;
        this.content = content;
        this.replyToId = replyToId;
    }

    @Override
    public void addComment(String username, String commentId, String content, String replyToId) {
        if (replyToId.equals(this.commentId)){
            replies.put(commentId, new Comment(username, commentId, content, replyToId));
        }else{
            for (IReply reply : replies.values()) {
                reply.addComment(username, commentId, content, replyToId);
            }
        }
    }

    @Override
    public void likeComment(String commentId) {
        if (this.commentId.equals(commentId)){
            likes++;
        }else{
            for (IReply reply : replies.values()) {
                reply.likeComment(commentId);
            }
        }
    }


    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
        String space = "    ".repeat(indent);
        sb.append(space).append("Comment: ").append(content).append("\n");
        sb.append(space).append("Written by: ").append(username).append("\n");
        sb.append(space).append("Likes: ").append(likes).append("\n");

        replies.values().stream()
                .sorted(Comparator.comparingInt(IReply::totalLikes).reversed())
                .forEach(r -> sb.append(r.print(indent+1)));
        return sb.toString();
    }

    @Override
    public int totalLikes() {
        int sum = likes;
        for (IReply reply : replies.values()) {
            sum += reply.totalLikes();
        }
        return sum;
    }
}

class Post implements IReply{
    private final String username, postContent;
    private final Map<String, IReply> replies = new LinkedHashMap<>();

    public Post(String username, String postContent) {
        this.username =  username;
        this.postContent = postContent;
    }
    @Override
    public void addComment(String username, String commentId, String content, String replyToId) {
        if (replyToId == null || replyToId.isEmpty()){
            replies.put(commentId, new Comment(username, commentId, content, replyToId));
        }else{
            for (IReply reply : replies.values()) {
                reply.addComment(username, commentId, content, replyToId);
            }
        }
    }
    @Override
    public void likeComment(String commentId) {
        for (IReply reply : replies.values()) {
            reply.likeComment(commentId);
        }
    }

    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
       sb.append("Post: ").append(postContent).append("\n");
        sb.append("Written by: ").append(username).append("\n");
        sb.append("Comments: ").append("\n");

        replies.values().stream()
                .sorted(Comparator.comparingInt(IReply::totalLikes).reversed())
                .forEach(r -> sb.append(r.print(indent+1)));
        return sb.toString();
    }

    @Override
    public int totalLikes() {
        return replies.values()
                .stream()
                .mapToInt(IReply::totalLikes)
                .sum();
    }


    @Override
    public String toString() {
        return print(1);
    }
}

public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}
