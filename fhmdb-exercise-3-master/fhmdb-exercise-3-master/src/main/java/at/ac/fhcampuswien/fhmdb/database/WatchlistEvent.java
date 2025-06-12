package at.ac.fhcampuswien.fhmdb.database;

public class WatchlistEvent {
    private final WatchlistEventType type;
    private final String apiId;
    private final String message;

    public WatchlistEvent(WatchlistEventType type, String apiId, String message) {
        this.type = type;
        this.apiId = apiId;
        this.message = message;
    }

    public WatchlistEventType getType() { return type; }
    public String getApiId()       { return apiId; }
    public String getMessage()     { return message; }
}