package com.simplecity.amp_library.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.simplecity.amp_library.R;
import com.simplecity.amp_library.ui.screens.album.list.AlbumListFragment;
import com.simplecity.amp_library.ui.screens.artist.list.AlbumArtistListFragment;
import com.simplecity.amp_library.ui.screens.folders.FolderFragment;
import com.simplecity.amp_library.ui.screens.genre.list.GenreListFragment;
import com.simplecity.amp_library.ui.screens.playlist.list.PlaylistListFragment;
import com.simplecity.amp_library.ui.screens.songs.list.SongListFragment;
import com.simplecity.amp_library.ui.screens.suggested.SuggestedFragment;
import com.simplecity.amp_library.utils.ComparisonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryItem {

    public @interface Type {
        int GENRES     = 0;
        int SUGGESTED  = 1;
        int ARTISTS    = 2;
        int ALBUMS     = 3;
        int SONGS      = 4;
        int PLAYLISTS  = 5;
        int FOLDERS    = 6;
    }

    @Type
    private final int type;
    private final int sortOrder;
    private boolean isChecked;

    private CategoryItem(@Type int type, SharedPreferences prefs) {
        this.type      = type;
        this.isChecked = prefs.getBoolean(getEnabledKey(), isEnabledByDefault());
        this.sortOrder = prefs.getInt(getSortKey(), 0);
    }

    /**
     * Loads all category items in user-defined sort order.
     */
    public static List<CategoryItem> getCategoryItems(SharedPreferences prefs) {
        List<CategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem(Type.GENRES, prefs));
        items.add(new CategoryItem(Type.SUGGESTED, prefs));
        items.add(new CategoryItem(Type.ARTISTS, prefs));
        items.add(new CategoryItem(Type.ALBUMS, prefs));
        items.add(new CategoryItem(Type.SONGS, prefs));
        items.add(new CategoryItem(Type.FOLDERS, prefs));
        items.add(new CategoryItem(Type.PLAYLISTS, prefs));
        Collections.sort(items,
            (a, b) -> ComparisonUtils.compareInt(a.getSortOrder(), b.getSortOrder()));
        return items;
    }

    /** Persists this item's enabled state and sort order. */
    public void savePrefs(SharedPreferences.Editor editor) {
        editor.putBoolean(getEnabledKey(), isChecked);
        editor.putInt(getSortKey(), sortOrder);
        editor.apply();
    }

    @StringRes
    public int getTitleResId() {
        switch (type) {
            case Type.GENRES:     return R.string.genres_title;
            case Type.SUGGESTED:  return R.string.suggested_title;
            case Type.ARTISTS:    return R.string.artists_title;
            case Type.ALBUMS:     return R.string.albums_title;
            case Type.SONGS:      return R.string.tracks_title;
            case Type.FOLDERS:    return R.string.folders_title;
            case Type.PLAYLISTS:  return R.string.playlists_title;
            default:
                throw new IllegalStateException("Unknown category type: " + type);
        }
    }

    public String getKey() {
        switch (type) {
            case Type.GENRES:     return "genres";
            case Type.SUGGESTED:  return "suggested";
            case Type.ARTISTS:    return "artists";
            case Type.ALBUMS:     return "albums";
            case Type.SONGS:      return "songs";
            case Type.FOLDERS:    return "folders";
            case Type.PLAYLISTS:  return "playlists";
            default:
                throw new IllegalStateException("Unknown category type: " + type);
        }
    }

    public boolean isEnabledByDefault() {
        switch (type) {
            case Type.GENRES:
            case Type.SUGGESTED:
            case Type.ARTISTS:
            case Type.ALBUMS:
            case Type.SONGS:
                return true;
            case Type.FOLDERS:
            case Type.PLAYLISTS:
                return false;
            default:
                throw new IllegalStateException("Unknown category type: " + type);
        }
    }

    public String getSortKey() {
        return getKey() + "_sort";
    }

    public String getEnabledKey() {
        return getKey() + "_enabled";
    }

    public Fragment getFragment(Context context) {
        String title = context.getString(getTitleResId());
        switch (type) {
            case Type.GENRES:
                return GenreListFragment.Companion.newInstance(title);
            case Type.SUGGESTED:
                return SuggestedFragment.Companion.newInstance(title);
            case Type.ARTISTS:
                return AlbumArtistListFragment.Companion.newInstance(title);
            case Type.ALBUMS:
                return AlbumListFragment.Companion.newInstance(title);
            case Type.SONGS:
                return SongListFragment.Companion.newInstance(title);
            case Type.FOLDERS:
                return FolderFragment.newInstance(title, true);
            case Type.PLAYLISTS:
                return PlaylistListFragment.Companion.newInstance(title);
            default:
                throw new IllegalStateException("Unknown category type: " + type);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryItem that = (CategoryItem) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type;
    }
}
