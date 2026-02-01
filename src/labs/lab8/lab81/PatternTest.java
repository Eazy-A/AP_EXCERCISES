package labs.lab8.lab81;

import java.util.ArrayList;
import java.util.List;

class Song {
    private final String title;
    private final String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Song{title=" + title + ", artist=" + artist + "}";
    }
}

interface State {
    void pressPlay();

    void pressStop();

    void pressFWD();

    void pressREW();
}

class MP3Player {
    private final List<Song> songs;
    private int currentIndex = 0;
    private State currentState;

    private final State playingState;
    private final State pausedState;

    public MP3Player(List<Song> songs) {
        this.songs = songs;
        this.playingState = new PlayingState(this);
        this.pausedState = new PausedState(this);
        this.currentState = pausedState;
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public State getPlayingState() {
        return playingState;
    }

    public State getPausedState() {
        return pausedState;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void resetIndex() {
        this.currentIndex = 0;
    }

    public void next() {
        this.currentIndex = (currentIndex + 1) % songs.size();
    }

    public void prev() {
        this.currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
    }

    public void pressPlay() {
        currentState.pressPlay();
    }

    public void pressStop() {
        currentState.pressStop();
    }

    public void pressFWD() {
        currentState.pressFWD();
    }

    public void pressREW() {
        currentState.pressREW();
    }

    public void printCurrentSong() {
        System.out.println(songs.get(currentIndex).toString());
    }

    @Override
    public String toString() {
        return "MP3Player{currentSong = " + currentIndex + ", songList = " + songs + "}";
    }
}

class PlayingState implements State {
    private final MP3Player player;

    public PlayingState(MP3Player player) {
        this.player = player;
    }

    public void pressPlay() {
        System.out.println("Song is already playing");
    }

    public void pressStop() {
        System.out.println("Song " + player.getCurrentIndex() + " is paused");
        player.setState(player.getPausedState());
    }

    public void pressFWD() {
        System.out.println("Forward...");
        player.next();
        player.setState(player.getPausedState());
    }

    public void pressREW() {
        System.out.println("Reward...");
        player.prev();
        player.setState(player.getPausedState());
    }
}

class PausedState implements State {
    private final MP3Player player;
    private boolean isInitialOrStopped = true;

    public PausedState(MP3Player player) {
        this.player = player;
    }

    public void pressPlay() {
        System.out.println("Song " + player.getCurrentIndex() + " is playing");
        isInitialOrStopped = false;
        player.setState(player.getPlayingState());
    }

    public void pressStop() {
        if (isInitialOrStopped) {
            System.out.println("Songs are already stopped");
        } else {
            System.out.println("Songs are stopped");
            player.resetIndex();
            isInitialOrStopped = true;
        }
    }

    public void pressFWD() {
        System.out.println("Forward...");
        player.next();
    }

    public void pressREW() {
        System.out.println("Reward...");
        player.prev();
    }
}

public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}
