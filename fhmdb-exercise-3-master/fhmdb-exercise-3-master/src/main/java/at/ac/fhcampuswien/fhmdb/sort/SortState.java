package at.ac.fhcampuswien.fhmdb.sort;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import javafx.collections.ObservableList;
import java.util.List;

public interface SortState {
    SortState sort(List<Movie> source, ObservableList<Movie> target);
    at.ac.fhcampuswien.fhmdb.models.SortedState getState();
}