package at.ac.fhcampuswien.fhmdb.database;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.dao.Dao;
import java.util.List;

public class MovieRepository {
    private static MovieRepository instance;
    private final Dao<MovieEntity, Long> dao;

    // Privater Konstruktor
    private MovieRepository() throws DataBaseException {
        try {
            this.dao = DatabaseManager.getInstance().getMovieDao();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    // Singleton-Zugriff
    public static synchronized MovieRepository getInstance() throws DataBaseException {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    public long countRows() throws DataBaseException {
        try {
            return dao.countOf();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    public List<MovieEntity> getAllMovies() throws DataBaseException {
        try {
            return dao.queryForAll();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    public int removeAll() throws DataBaseException {
        try {
            return dao.deleteBuilder().delete();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    public MovieEntity getMovie(String apiId) throws DataBaseException {
        try {
            return dao.queryBuilder()
                    .where()
                    .eq("apiId", apiId)
                    .queryForFirst();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    public int addAllMovies(List<Movie> movies) throws DataBaseException {
        try {
            List<MovieEntity> entities = MovieEntity.fromMovies(movies);
            return dao.create(entities);
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }
}