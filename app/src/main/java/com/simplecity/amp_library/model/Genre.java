package com.simplecity.amp_library.model;

import android.database.Cursor;
import android.provider.MediaStore;
import java.io.Serializable;

public class Genre implements Serializable {

    private long id;
    public String name;
    public int numSongs;

    public static String[] getProjection() {
        return new String[] {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
        };
    }

    public static Query getQuery() {
        return new Query.Builder()
                .uri(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI)
                .projection(Genre.getProjection())
                .selection(null)
                .args(null)
                .sort(MediaStore.Audio.Genres.DEFAULT_SORT_ORDER)
                .build();
    }

    public Genre(Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Genres._ID));
        this.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
    }

    public Genre(long genreId, String name) {
        this.id = genreId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genre genre = (Genre) o;

        if (id != genre.id) return false;
        if (numSongs != genre.numSongs) return false;
        return name != null ? name.equals(genre.name) : genre.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + numSongs;
        return result;
    }
}
