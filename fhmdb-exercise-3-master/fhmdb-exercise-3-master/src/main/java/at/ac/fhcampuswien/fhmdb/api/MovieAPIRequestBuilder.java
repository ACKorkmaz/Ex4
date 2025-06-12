package at.ac.fhcampuswien.fhmdb.api;

import at.ac.fhcampuswien.fhmdb.models.Genre;

public class MovieAPIRequestBuilder {
    private final StringBuilder url;
    private boolean hasParams = false;

    public MovieAPIRequestBuilder(String baseUrl) {
        this.url = new StringBuilder(baseUrl);
    }

    private void addParam(String key, String value) {
        if (!hasParams) {
            url.append("?");
            hasParams = true;
        } else {
            url.append("&");
        }
        url.append(key).append("=").append(value);
    }

    public MovieAPIRequestBuilder query(String q) {
        if (q != null && !q.isEmpty()) addParam("query", q);
        return this;
    }

    public MovieAPIRequestBuilder genre(Genre genre) {
        if (genre != null) addParam("genre", genre.name());
        return this;
    }

    public MovieAPIRequestBuilder releaseYear(String year) {
        if (year != null && !year.isEmpty()) addParam("releaseYear", year);
        return this;
    }

    public MovieAPIRequestBuilder ratingFrom(String rating) {
        if (rating != null && !rating.isEmpty()) addParam("ratingFrom", rating);
        return this;
    }

    public String build() {
        return url.toString();
    }
}