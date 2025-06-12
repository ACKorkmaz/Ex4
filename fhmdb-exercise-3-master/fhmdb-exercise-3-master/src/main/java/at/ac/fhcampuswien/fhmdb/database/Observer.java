package at.ac.fhcampuswien.fhmdb.database;

public interface Observer {
    void onWatchlistEvent(WatchlistEvent event);
}