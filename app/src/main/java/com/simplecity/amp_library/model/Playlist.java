package com.simplecity.amp_library.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.simplecity.amp_library.R;
import com.simplecity.amp_library.utils.playlists.FavoritesPlaylistManager;
import com.simplecity.amp_library.utils.playlists.PlaylistManager;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import java.io.Serializable;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Playlist implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String TAG = "Playlist";

    public @interface Type {
        int PODCAST         = 0;
        int RECENTLY_ADDED  = 1;
        int MOST_PLAYED     = 2;
        int RECENTLY_PLAYED = 3;
        int FAVORITES       = 4;
        int USER_CREATED    = 5;
    }

    @Type
    private final int type;

    private final long id;
    private final String name;
    private final boolean canEdit;
    private final boolean canClear;
    private final boolean canDelete;
    private final boolean canRename;
    private final boolean canSort;

    /** Columns to retrieve from the playlists table. */
    protected static final String[] PROJECTION = new String[] {
        MediaStore.Audio.Playlists._ID,
        MediaStore.Audio.Playlists.NAME
    };

    /**
     * Builds the base query to list all playlists.
     */
    public static Query getQuery() {
        return new Query.Builder()
            .uri(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI)
            .projection(PROJECTION)
            .selection(null)
            .sort(null)
            .build();
    }

    /**
     * Creates a Playlist of a built-in type.
     */
    public Playlist(
        @Type int type,
        long id,
        String name,
        boolean canEdit,
        boolean canClear,
        boolean canDelete,
        boolean canRename,
        boolean canSort
    ) {
        this.type      = type;
        this.id        = id;
        this.name      = name;
        this.canEdit   = canEdit;
        this.canClear  = canClear;
        this.canDelete = canDelete;
        this.canRename = canRename;
        this.canSort   = canSort;
    }

    /**
     * Reads a user-created playlist from a cursor.
     */
    public Playlist(Context context, Cursor cursor) {
        this.id        = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
        this.name      = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
        this.type      = Type.USER_CREATED;
        this.canEdit   = true;
        this.canClear  = true;
        this.canDelete = true;
        this.canRename = true;
        this.canSort   = true;

        if (context.getString(R.string.fav_title).equals(name)) {
            // Treat the "Favorites" playlist specially
            this.type      = Type.FAVORITES;
            this.canDelete = false;
            this.canRename = false;
        }
    }

    /**
     * Clears the contents of this playlist.
     */
    public void clear(
        PlaylistManager playlistManager,
        FavoritesPlaylistManager favoritesPlaylistManager
    ) {
        switch (type) {
            case Type.FAVORITES:
                favoritesPlaylistManager.clearFavorites();
                break;
            case Type.MOST_PLAYED:
                playlistManager.clearMostPlayed();
                break;
            case Type.USER_CREATED:
                playlistManager.clearPlaylist(id);
                break;
            default:
                // Should never happen
                throw new IllegalStateException("Cannot clear unknown playlist type: " + type);
        }
    }

    /**
     * Removes a song from this playlist.
     */
    public void removeSong(
        @NonNull Song song,
        PlaylistManager playlistManager,
        @Nullable Function1<Boolean, Unit> success
    ) {
        playlistManager.removeFromPlaylist(this, song, success);
    }

    /**
     * Moves a song within this playlist.
     */
    public boolean moveSong(Context context, int from, int to) {
        return MediaStore.Audio.Playlists.Members
            .moveItem(context.getContentResolver(), id, from, to);
    }

    // --- Accessors ---

    @Type
    public int getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean canEdit() {
        return canEdit;
    }

    public boolean canClear() {
        return canClear;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public boolean canRename() {
        return canRename;
    }

    public boolean canSort() {
        return canSort;
    }

    // --- equals / hashCode / toString ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Playlist that = (Playlist) o;
        if (id != that.id) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Playlist{" +
               "type=" + type +
               ", id=" + id +
               ", name='" + name + '\'' +
               '}';
    }

    /**
     * Builds a Song instance from a playlist-members cursor.
     */
    public static Song createSongFromPlaylistCursor(Cursor cursor) {
        Song song = new Song(cursor);
        song.id                = cursor.getLong(
            cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID));
        song.playlistSongId    = cursor.getLong(
            cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID));
        song.playlistSongPlayOrder = cursor.getLong(
            cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.PLAY_ORDER));
        return song;
    }
}
