package at.ac.fhcampuswien.fhmdb.controllers;

import at.ac.fhcampuswien.fhmdb.ClickEventHandler;
import at.ac.fhcampuswien.fhmdb.database.*;
import at.ac.fhcampuswien.fhmdb.ui.UserDialog;
import at.ac.fhcampuswien.fhmdb.ui.WatchlistCell;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WatchlistController implements Initializable {

    @FXML
    public JFXListView<MovieEntity> watchlistView;

    private WatchlistRepository watchlistRepository;

    protected ObservableList<MovieEntity> observableWatchlist = FXCollections.observableArrayList();

    private final ClickEventHandler onRemoveFromWatchlistClicked = (o) -> {
        if (o instanceof MovieEntity movieEntity) {
            try {
                // statt new: Singleton
                WatchlistRepository.getInstance().removeFromWatchlist(movieEntity.getApiId());
                observableWatchlist.remove(movieEntity);
            } catch (DataBaseException e) {
                new UserDialog("Database Error", "Could not remove movie from watchlist").show();
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // hol dir die Instanz
            watchlistRepository = WatchlistRepository.getInstance();
            List<WatchlistMovieEntity> watchlist = watchlistRepository.getWatchlist();

            MovieRepository movieRepository = MovieRepository.getInstance();
            List<MovieEntity> movies = new ArrayList<>();
            for (WatchlistMovieEntity entry : watchlist) {
                movies.add(movieRepository.getMovie(entry.getApiId()));
            }

            observableWatchlist.addAll(movies);
            watchlistView.setItems(observableWatchlist);
            watchlistView.setCellFactory(lv -> new WatchlistCell(onRemoveFromWatchlistClicked));

        } catch (DataBaseException e) {
            new UserDialog("Database Error", "Could not read movies from DB").show();
        }

        if (observableWatchlist.isEmpty()) {
            watchlistView.setPlaceholder(new javafx.scene.control.Label("Watchlist is empty"));
        }
    }
}
