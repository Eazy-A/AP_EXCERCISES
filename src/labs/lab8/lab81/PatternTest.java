package labs.lab8.lab81;

import java.util.ArrayList;
import java.util.List;

interface Action {
    void play(MP3Player player);

    void stop(MP3Player player);

    void fwd(MP3Player player);

    void rew(MP3Player player);
}

class MP3Player {
    private List<Song> songs = new ArrayList<>();
    private Action currentState;
    private int currentSongIndex;

    public MP3Player(List<Song> songs) {
        this.songs = songs;
        this.currentState = new StoppedState();
        this.currentSongIndex = 0;
    }

    public void printCurrentSong() {
        System.out.println(songs.get(currentSongIndex));
    }

    public void setCurrentState(Action currentState) {
        this.currentState = currentState;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void incrementSongIndex() {
        currentSongIndex = (currentSongIndex + 1) % songs.size();
    }

    public void decrementSongIndex() {
        currentSongIndex = (currentSongIndex - 1 + songs.size()) % songs.size();
    }

    public void resetIndex() {
        currentSongIndex = 0;
    }

    public void pressPlay() {
        currentState.play(this);
    }

    public void pressStop() {
        currentState.stop(this);
    }

    public void pressFWD() {
        currentState.fwd(this);
    }

    public void pressREW() {
        currentState.rew(this);
    }

    @Override
    public String toString() {
        return "MP3Player{currentSong = " + currentSongIndex +
                ", songList = " + songs + "}";
    }
}

class Song {
    private final String title;
    private final String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title=" + title +
                ", artist=" + artist +
                '}';
    }
}

class StoppedState implements Action {
    @Override
    public void play(MP3Player player) {
        player.setCurrentState(new PlayingState());
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
    }

    @Override
    public void stop(MP3Player player) {
        System.out.println("Songs are already stopped");
    }

    @Override
    public void fwd(MP3Player player) {
        player.incrementSongIndex();
        System.out.println("Forward...");
    }

    @Override
    public void rew(MP3Player player) {
        player.decrementSongIndex();
        System.out.println("Reward...");
    }
}

class PlayingState implements Action {
    @Override
    public void play(MP3Player player) {
        System.out.println("Song is already playing");
    }

    @Override
    public void stop(MP3Player player) {
        System.out.println("Song " + player.getCurrentSongIndex() + " is paused");
        player.setCurrentState(new PausedState());
    }

    @Override
    public void fwd(MP3Player player) {
        player.incrementSongIndex();
        System.out.println("Forward...");
        player.setCurrentState(new PausedState());
    }

    @Override
    public void rew(MP3Player player) {
        player.decrementSongIndex();
        System.out.println("Reward...");
        player.setCurrentState(new PausedState());
    }
}

class PausedState implements Action {
    @Override
    public void play(MP3Player player) {
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
        player.setCurrentState(new PlayingState());
    }

    @Override
    public void stop(MP3Player player) {
        System.out.println("Songs are stopped");
        player.resetIndex();
        player.setCurrentState(new StoppedState());
    }

    @Override
    public void fwd(MP3Player player) {
        player.incrementSongIndex();
        System.out.println("Forward...");
    }

    @Override
    public void rew(MP3Player player) {
        player.decrementSongIndex();
        System.out.println("Reward...");
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
