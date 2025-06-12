package at.ac.fhcampuswien.fhmdb.sort;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import javafx.collections.ObservableList;
import java.util.Comparator;
import java.util.List;

public class DescSortState implements SortState {
    @Override
    public SortState sort(List<Movie> source, ObservableList<Movie> target) {
        source.stream()
                .sorted(Comparator.comparing(Movie::getTitle).reversed())
                .forEachOrdered(target::add);
        return new NoSortState();
    }
    @Override
    public at.ac.fhcampuswien.fhmdb.models.SortedState getState() {
        return at.ac.fhcampuswien.fhmdb.models.SortedState.DESCENDING;
    }
}