package at.ac.fhcampuswien.fhmdb.database;

import com.j256.ormlite.dao.Dao;
import java.util.ArrayList;
import java.util.List;

public class WatchlistRepository implements Observable {
    private static WatchlistRepository instance;
    private final Dao<WatchlistMovieEntity, Long> dao;
    private final List<Observer> observers = new ArrayList<>();

    // Privater Konstruktor für Singleton
    private WatchlistRepository() throws DataBaseException {
        try {
            this.dao = DatabaseManager.getInstance().getWatchlistDao();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    // Singleton-Zugriff
    public static synchronized WatchlistRepository getInstance() throws DataBaseException {
        if (instance == null) {
            instance = new WatchlistRepository();
        }
        return instance;
    }

    // Observable-Implementierung
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    private void notifyObservers(WatchlistEvent event) {
        for (Observer o : observers) {
            o.onWatchlistEvent(event);
        }
    }

    // Gibt die gesamte Watchlist zurück
    public List<WatchlistMovieEntity> getWatchlist() throws DataBaseException {
        try {
            return dao.queryForAll();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    // Fügt einen Film hinzu und feuert Events
    public int addToWatchlist(WatchlistMovieEntity movie) throws DataBaseException {
        try {
            long count = dao.queryBuilder()
                    .where()
                    .eq("apiId", movie.getApiId())
                    .countOf();
            if (count == 0) {
                dao.create(movie);
                // Erfolg
                notifyObservers(new WatchlistEvent(
                        WatchlistEventType.ADDED,
                        movie.getApiId(),
                        "Movie successfully added to watchlist"
                ));
                return 1;
            } else {
                // Bereits vorhanden → Fehler
                notifyObservers(new WatchlistEvent(
                        WatchlistEventType.ADD_FAILED,
                        movie.getApiId(),
                        "Movie already on watchlist"
                ));
                return 0;
            }
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    // Entfernt einen Film und (optional) könnte hier ebenfalls Events feuern
    public int removeFromWatchlist(String apiId) throws DataBaseException {
        try {
            List<WatchlistMovieEntity> toDelete = dao.queryBuilder()
                    .where()
                    .eq("apiId", apiId)
                    .query();
            return dao.delete(toDelete);
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }
}
