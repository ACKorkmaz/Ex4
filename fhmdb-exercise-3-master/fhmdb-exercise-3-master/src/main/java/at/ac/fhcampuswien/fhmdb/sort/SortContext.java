package at.ac.fhcampuswien.fhmdb.sort;

import at.ac.fhcampuswien.fhmdb.models.SortedState;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import javafx.collections.ObservableList;
import java.util.List;

public class SortContext {
    private SortState currentState;

    public SortContext() {
        this.currentState = new NoSortState();
    }

    public SortedState sort(List<Movie> source, ObservableList<Movie> target) {
        target.clear();
        SortState next = currentState.sort(source, target);
        currentState = next;
        return currentState.getState();
    }

    public void setState(SortedState state) {
        switch(state) {
            case ASCENDING -> currentState = new AscSortState();
            case DESCENDING -> currentState = new DescSortState();
            default -> currentState = new NoSortState();
        }
    }

    public SortedState getState() {
        return currentState.getState();
    }
}