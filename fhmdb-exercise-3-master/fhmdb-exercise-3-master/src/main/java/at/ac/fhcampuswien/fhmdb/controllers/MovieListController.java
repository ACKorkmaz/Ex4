package at.ac.fhcampuswien.fhmdb.controllers;

import at.ac.fhcampuswien.fhmdb.ClickEventHandler;
import at.ac.fhcampuswien.fhmdb.api.MovieAPI;
import at.ac.fhcampuswien.fhmdb.api.MovieApiException;
import at.ac.fhcampuswien.fhmdb.database.*;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.SortedState;
import at.ac.fhcampuswien.fhmdb.sort.SortContext;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import at.ac.fhcampuswien.fhmdb.ui.UserDialog;
import at.ac.fhcampuswien.fhmdb.database.Observer;
import at.ac.fhcampuswien.fhmdb.database.WatchlistEvent;
import at.ac.fhcampuswien.fhmdb.database.WatchlistEventType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MovieListController implements Initializable, Observer {
    @FXML private JFXButton searchBtn;
    @FXML private JFXButton sortBtn;
    @FXML private TextField searchField;
    @FXML private JFXListView<Movie> movieListView;
    @FXML private JFXComboBox<Object> genreComboBox;
    @FXML private JFXComboBox<Integer> releaseYearComboBox;
    @FXML private JFXComboBox<Integer> ratingFromComboBox;

    private List<Movie> allMovies = new ArrayList<>();
    private final ObservableList<Movie> observableMovies = FXCollections.observableArrayList();
    private SortContext sortContext;

    private final ClickEventHandler onAddToWatchlistClicked = item -> {
        if (item instanceof Movie movie) {
            try {
                WatchlistRepository.getInstance()
                        .addToWatchlist(new WatchlistMovieEntity(movie.getId()));
                // Dialogs für Erfolg/Fehler kommen jetzt via Observer
            } catch (DataBaseException e) {
                // Nur bei Datenbankfehlern
                Platform.runLater(() ->
                        new UserDialog("Database Error", "Could not add movie to watchlist").show()
                );
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Als Observer bei der Watchlist anmelden
        try {
            WatchlistRepository.getInstance().addObserver(this);
        } catch (DataBaseException ignored) { }

        sortContext = new SortContext();
        initializeState();
        initializeLayout();
    }

    /** Observer‐Callback für Watchlist‐Events */
    @Override
    public void onWatchlistEvent(WatchlistEvent event) {
        Platform.runLater(() ->
                new UserDialog(
                        event.getType() == WatchlistEventType.ADDED ? "Watchlist" : "Watchlist Error",
                        event.getMessage()
                ).show()
        );
    }

    private void initializeState() {
        List<Movie> result;
        try {
            result = MovieAPI.getAllMovies();
            cacheMovies(result);
        } catch (MovieApiException e) {
            new UserDialog("MovieAPI Error", "Could not load from API; using cache").show();
            result = loadCachedMovies();
        }
        allMovies = result;
        observableMovies.setAll(result);
        sortContext.setState(SortedState.NONE);
    }

    private List<Movie> loadCachedMovies() {
        try {
            return MovieEntity.toMovies(MovieRepository.getInstance().getAllMovies());
        } catch (DataBaseException e) {
            new UserDialog("DB Error", "Could not load cache").show();
            return new ArrayList<>();
        }
    }

    private void cacheMovies(List<Movie> movies) {
        try {
            MovieRepository repo = MovieRepository.getInstance();
            repo.removeAll();
            repo.addAllMovies(movies);
        } catch (DataBaseException e) {
            new UserDialog("DB Error", "Could not write cache").show();
        }
    }

    private void initializeLayout() {
        movieListView.setItems(observableMovies);
        movieListView.setCellFactory(lv -> new MovieCell(onAddToWatchlistClicked));

        // Genre
        genreComboBox.getItems().add("No filter");
        genreComboBox.getItems().addAll(Arrays.asList(Genre.values()));
        genreComboBox.setPromptText("Filter by Genre");

        // Release Year
        releaseYearComboBox.setValue(null);
        List<Integer> years = new ArrayList<>();
        for (int y = 1900; y <= 2023; y++) years.add(y);
        releaseYearComboBox.getItems().addAll(years);
        releaseYearComboBox.setPromptText("Filter by Release Year");

        // Rating
        ratingFromComboBox.setValue(null);
        List<Integer> ratings = new ArrayList<>();
        for (int r = 0; r <= 10; r++) ratings.add(r);
        ratingFromComboBox.getItems().addAll(ratings);
        ratingFromComboBox.setPromptText("Filter by Rating");
    }

    @FXML
    public void searchBtnClicked(ActionEvent e) {
        String q = searchField.getText().trim();
        String query = q.isEmpty() ? null : q.toLowerCase();
        String year = (releaseYearComboBox.getValue() == null) ? null : releaseYearComboBox.getValue().toString();
        String rating = (ratingFromComboBox.getValue() == null) ? null : ratingFromComboBox.getValue().toString();
        Genre genre = (genreComboBox.getValue() instanceof Genre g) ? g : null;

        List<Movie> fetched;
        try {
            fetched = MovieAPI.getAllMovies(query, genre, year, rating);
        } catch (MovieApiException ex) {
            new UserDialog("MovieAPI Error", "Could not fetch filtered movies").show();
            fetched = new ArrayList<>();
        }

        allMovies = fetched;
        observableMovies.setAll(fetched);

        if (sortContext.getState() != SortedState.NONE) {
            sortContext.sort(allMovies, observableMovies);
            updateSortBtnLabel(sortContext.getState());
        }
    }

    @FXML
    public void sortBtnClicked(ActionEvent e) {
        SortedState newState = sortContext.sort(allMovies, observableMovies);
        updateSortBtnLabel(newState);
    }

    private void updateSortBtnLabel(SortedState state) {
        sortBtn.setText(
                switch (state) {
                    case ASCENDING -> "↑";
                    case DESCENDING -> "↓";
                    default -> "↕";
                }
        );
    }
}
