package at.ac.fhcampuswien.fhmdb.database;

public interface Observable {
    void addObserver(Observer o);
    void removeObserver(Observer o);
}