package at.ac.fhcampuswien.fhmdb.sort;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import javafx.collections.ObservableList;
import java.util.List;

public class NoSortState implements SortState {
    @Override
    public SortState sort(List<Movie> source, ObservableList<Movie> target) {
        target.setAll(source);
        return new AscSortState();
    }
    @Override
    public at.ac.fhcampuswien.fhmdb.models.SortedState getState() {
        return at.ac.fhcampuswien.fhmdb.models.SortedState.NONE;
    }
}