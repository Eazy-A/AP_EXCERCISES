package ispit_juni_2023.social_network;

import java.util.*;

interface Ipost {
    void addComment(String username, String commentId, String content, String replyToId);
    void print(int indent);
    String getId();
    void likeComment(String commentId);
}

class Comment implements Ipost {
    private String username;
    private String commentId;
    private String content;
    private String replyToId;
    private int likes = 0;
    private List<Ipost> replies = new ArrayList<>();

    public Comment(String username, String commentId, String content, String replyToId) {
        this.username = username;
        this.commentId = commentId;
        this.content = content;
        this.replyToId = replyToId;
    }


    @Override
    public void addComment(String username, String commentId, String content, String replyToId) {
        if (this.commentId.equals(replyToId)) {
            replies.add(new Comment(username, commentId, content, replyToId));
        }else{
            for (Ipost reply : replies){
                reply.addComment(username, commentId, content, replyToId);
            }
        }
    }

    @Override
    public void print(int indent) {
        String space = "    ".repeat(indent);
        System.out.printf("%sComment: %s\n Written by: %s\n Likes: %d\n", space, content, username, likes);
        for (Ipost reply : replies){
            reply.print(indent + 1);
        }
    }

    @Override
    public String getId() {
        return commentId;
    }

    @Override
    public void likeComment(String targetId) {
        if (this.commentId.equals(targetId)) {
            this.likes++;
        } else {
            for (Ipost reply : replies) {
                reply.likeComment(targetId);
            }
        }
    }
    @Override
    public String toString() {
        return String.format("Comment: %s Written by: %s Likes: %d", content, username, likes);
    }

    public String formatComment(int indent) {
        StringBuilder sb = new StringBuilder();
        String space = "    ".repeat(indent);
        sb.append(String.format("%sComment: %s\n%sWritten by: %s\n%sLikes: %d\n", space, content,space, username,space, likes));
        for (Ipost reply : replies) {
            sb.append(((Comment)reply).formatComment(indent + 1));
        }
        return sb.toString();
    }


}

class Post implements Ipost {
    private String username;
    private String postContent;
    private List<Ipost> replies = new ArrayList<>();
    public Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
    }

    public void addComment(String username, String commentId, String content, String replyToId) {
        if (replyToId == null || replyToId.isEmpty()) {
            replies.add(new Comment(username, commentId, content, replyToId));
        } else {
            for (Ipost reply : replies) {
                reply.addComment(username, commentId, content, replyToId);
            }
        }
    }

    @Override
    public void print(int indent) {
        System.out.printf("Post: %s\nWritten by: %s\nComments:\n", postContent, username);
        for (Ipost reply : replies) {
            reply.print(1);
        }
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void likeComment(String targetId) {
        for (Ipost r : replies) {
            r.likeComment(targetId);
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Post: %s\nWritten by: %s\nComments:\n", postContent, username));
        for (Ipost reply : replies) {
            sb.append(((Comment)reply).formatComment(1));
        }
        return sb.toString().trim();
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
