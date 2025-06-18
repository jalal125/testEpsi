package com.simplecity.amp_library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.simplecity.amp_library.BuildConfig;
import com.simplecity.amp_library.R;
import com.simplecity.amp_library.model.CategoryItem;
import com.simplecity.amp_library.ui.adapters.ViewType;
import com.simplecity.amp_library.utils.sorting.SortManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SettingsManager extends BaseSettingsManager {

    private static final String TAG = "SettingsManager";

    @Inject
    public SettingsManager(@NotNull SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    // Support keys
    private static final String keyPrefChangelog           = "pref_changelog";
    private static final String keyPrefFaq                 = "pref_faq";
    private static final String keyPrefHelp                = "pref_help";
    private static final String keyPrefRate                = "pref_rate";
    private static final String keyPrefRestorePurchases    = "pref_restore_purchases";

    // Display keys
    private static final String keyPrefTabChooser          = "pref_tab_chooser";
    private static final String keyPrefDefaultPage         = "pref_default_page";
    private static final String keyDisplayRemainingTime    = "pref_display_remaining_time";

    // Theme keys
    private static final String keyPrefThemeBase                   = "pref_theme_base";
    private static final String keyPrefPrimaryColor               = "pref_theme_primary_color";
    private static final String keyPrefAccentColor                = "pref_theme_accent_color";
    private static final String keyPrefNavBar                     = "pref_nav_bar";
    private static final String keyPrefPalette                    = "pref_theme_use_palette";
    private static final String keyPrefPaletteNowPlayingOnly      = "pref_theme_use_palette_now_playing";

    // Artwork keys
    private static final String keyPrefDownloadArtwork    = "pref_download_artwork";
    private static final String keyPrefDeleteArtwork      = "pref_delete_artwork";

    // Scrobbler
    private static final String keyPrefDownloadScrobbler = "pref_download_simple_lastfm_scrobbler";

    // Blacklist/Whitelist
    private static final String keyPrefBlacklist         = "pref_blacklist_view";
    private static final String keyPrefWhitelist         = "pref_whitelist_view";

    // Playback
    private static final String keyPrefRememberShuffle   = "pref_remember_shuffle";

    // Upgrade
    private static final String keyPrefUpgrade           = "pref_upgrade";

    // Session flags
    private boolean hasSeenRateSnackbar = false;

    // Lockscreen artwork
    public static final String KEY_SHOW_LOCKSCREEN_ARTWORK = "pref_show_lockscreen_artwork";

    public boolean showLockscreenArtwork() {
        return getBool(KEY_SHOW_LOCKSCREEN_ARTWORK, true);
    }

    // Keep screen on
    private static final String keyKeepScreenOn = "pref_screen_on";

    public boolean keepScreenOn() {
        return getBool(keyKeepScreenOn, false);
    }

    // Display remaining time
    public boolean displayRemainingTime() {
        return getBool(keyDisplayRemainingTime, true);
    }

    // Album display type
    private static final String keyAlbumDisplayType = "album_display_type_new";

    public void setAlbumDisplayType(int type) {
        setInt(keyAlbumDisplayType, type);
    }

    @ViewType
    public int getAlbumDisplayType(Context context) {
        int def = ShuttleUtils.isTablet(context)
            ? ViewType.ALBUM_PALETTE
            : ViewType.ALBUM_LIST;
        return getInt(keyAlbumDisplayType, def);
    }

    // Artist display type
    private static final String keyArtistDisplayType = "artist_display_type_new";

    public void setArtistDisplayType(int type) {
        setInt(keyArtistDisplayType, type);
    }

    @ViewType
    public int getArtistDisplayType() {
        return getInt(keyArtistDisplayType, ViewType.ARTIST_PALETTE);
    }

    // Artist column counts
    private static final String keyArtistColumnCount            = "artist_column_count";
    private static final String keyArtistColumnCountLand        = "artist_column_count_land";
    private static final String keyArtistColumnCountTablet      = "artist_column_count_tablet";
    private static final String keyArtistColumnCountTabletLand  = "artist_column_count_tablet_land";

    private String getArtistColumnCountKey(Context context) {
        if (ShuttleUtils.isLandscape(context)) {
            return ShuttleUtils.isTablet(context)
                ? keyArtistColumnCountTabletLand
                : keyArtistColumnCountLand;
        }
        if (ShuttleUtils.isTablet(context)) {
            return keyArtistColumnCountTablet;
        }
        return keyArtistColumnCount;
    }

    public void setArtistColumnCount(Context context, int count) {
        setInt(getArtistColumnCountKey(context), count);
    }

    public int getArtistColumnCount(Context context) {
        int display = getArtistDisplayType();
        int defaultSpan = display == ViewType.ARTIST_LIST
            ? context.getResources().getInteger(R.integer.list_num_columns)
            : context.getResources().getInteger(R.integer.grid_num_columns);
        if (display == ViewType.ARTIST_LIST && defaultSpan == 1) {
            return 1;
        }
        return getInt(getArtistColumnCountKey(context), defaultSpan);
    }

    // Album column counts
    private static final String keyAlbumColumnCount            = "album_column_count";
    private static final String keyAlbumColumnCountLand        = "album_column_count_land";
    private static final String keyAlbumColumnCountTablet      = "album_column_count_tablet";
    private static final String keyAlbumColumnCountTabletLand  = "album_column_count_tablet_land";

    private String getAlbumColumnCountKey(Context context) {
        if (ShuttleUtils.isLandscape(context)) {
            return ShuttleUtils.isTablet(context)
                ? keyAlbumColumnCountTabletLand
                : keyAlbumColumnCountLand;
        }
        if (ShuttleUtils.isTablet(context)) {
            return keyAlbumColumnCountTablet;
        }
        return keyAlbumColumnCount;
    }

    public void setAlbumColumnCount(Context context, int count) {
        setInt(getAlbumColumnCountKey(context), count);
    }

    public int getAlbumColumnCount(Context context) {
        int display = getAlbumDisplayType(context);
        int defaultSpan = display == ViewType.ALBUM_LIST
            ? context.getResources().getInteger(R.integer.list_num_columns)
            : context.getResources().getInteger(R.integer.grid_num_columns);
        if (display == ViewType.ALBUM_LIST && defaultSpan == 1) {
            return 1;
        }
        return getInt(getAlbumColumnCountKey(context), defaultSpan);
    }

    // Equalizer
    public boolean getEqualizerEnabled() {
        return getBool("audiofx.global.enable", false);
    }

    // Document tree
    private static final String keyDocumentTreeUri = "document_tree_uri";

    public void setDocumentTreeUri(String uri) {
        setString(keyDocumentTreeUri, uri);
    }

    @Nullable
    public String getDocumentTreeUri() {
        return getString(keyDocumentTreeUri);
    }

    // Folder browser
    private static final String keyFolderBrowserInitialDir       = "folder_browser_initial_dir";
    private static final String keyFolderBrowserFilesSortOrder   = "folder_browser_files_sort_order";
    private static final String keyFolderBrowserFilesAscending   = "folder_browser_files_ascending";
    private static final String keyFolderBrowserFoldersSortOrder = "folder_browser_folders_sort_order";
    private static final String keyFolderBrowserFoldersAscending = "folder_browser_folders_ascending";
    private static final String keyFolderBrowserShowFileNames    = "folder_browser_show_file_names";

    public void setFolderBrowserInitialDir(String dir) {
        setString(keyFolderBrowserInitialDir, dir);
    }

    public String getFolderBrowserInitialDir() {
        return getString(keyFolderBrowserInitialDir, "");
    }

    public void setFolderBrowserFilesSortOrder(String order) {
        setString(keyFolderBrowserFilesSortOrder, order);
    }

    public String getFolderBrowserFilesSortOrder() {
        return getString(keyFolderBrowserFilesSortOrder, SortManager.SortFiles.DEFAULT);
    }

    public void setFolderBrowserFilesAscending(boolean asc) {
        setBool(keyFolderBrowserFilesAscending, asc);
    }

    public boolean getFolderBrowserFilesAscending() {
        return getBool(keyFolderBrowserFilesAscending, true);
    }

    public void setFolderBrowserFoldersSortOrder(String order) {
        setString(keyFolderBrowserFoldersSortOrder, order);
    }

    public String getFolderBrowserFoldersSortOrder() {
        return getString(keyFolderBrowserFoldersSortOrder, SortManager.SortFolders.DEFAULT);
    }

    public void setFolderBrowserFoldersAscending(boolean asc) {
        setBool(keyFolderBrowserFoldersAscending, asc);
    }

    public boolean getFolderBrowserFoldersAscending() {
        return getBool(keyFolderBrowserFoldersAscending, true);
    }

    public void setFolderBrowserShowFileNames(boolean show) {
        setBool(keyFolderBrowserShowFileNames, show);
    }

    public boolean getFolderBrowserShowFileNames() {
        return getBool(keyFolderBrowserShowFileNames, false);
    }

    // Launch count
    private static final String keyLaunchCount = "launch_count";

    public void incrementLaunchCount() {
        setInt(keyLaunchCount, getLaunchCount() + 1);
    }

    public int getLaunchCount() {
        return getInt(keyLaunchCount, 0);
    }

    // Nag message
    private static final String keyNagMessageRead = "nag_message_read";

    public void setNagMessageRead() {
        setBool(keyNagMessageRead, true);
    }

    public boolean getNagMessageRead() {
        return getBool(keyNagMessageRead, false);
    }

    // Has rated
    private static final String keyHasRated = "has_rated";

    public void setHasRated() {
        setBool(keyHasRated, true);
    }

    public boolean getHasRated() {
        return getBool(keyHasRated, false);
    }

    // Bluetooth
    private static final String keyBluetoothPauseDisconnect = "pref_bluetooth_disconnect";
    private static final String keyBluetoothResumeConnect   = "pref_bluetooth_connect";

    public boolean getBluetoothPauseDisconnect() {
        return getBool(keyBluetoothPauseDisconnect, true);
    }

    public boolean getBluetoothResumeConnect() {
        return getBool(keyBluetoothResumeConnect, false);
    }

    // Themes
    public boolean getUsePalette() {
        return getBool(keyPrefPalette, true);
    }

    public boolean getUsePaletteNowPlayingOnly() {
        return getBool(keyPrefPaletteNowPlayingOnly, false);
    }

    public boolean getTintNavBar() {
        return getBool(keyPrefNavBar, false);
    }

    public void storePrimaryColor(int color) {
        setInt(keyPrefPrimaryColor, color);
    }

    public int getPrimaryColor() {
        return getInt(keyPrefPrimaryColor, -1);
    }

    public void storeAccentColor(int color) {
        setInt(keyPrefAccentColor, color);
    }

    public int getAccentColor() {
        return getInt(keyPrefAccentColor, -1);
    }

    // Artwork download
    public boolean canDownloadArtwork() {
        return getBool(keyPrefDownloadArtwork, false);
    }

    public boolean canDeleteArtwork() {
        return getBool(keyPrefDeleteArtwork, false);
    }

    // Scrobbler
    public boolean canDownloadScrobbler() {
        return getBool(keyPrefDownloadScrobbler, false);
    }

    // Ignore duplicates in playlists
    private static final String keyPlaylistIgnoreDuplicates = "pref_ignore_duplicates";

    public boolean ignoreDuplicates() {
        return getBool(keyPlaylistIgnoreDuplicates, false);
    }

    public void setIgnoreDuplicates(boolean ignore) {
        setBool(keyPlaylistIgnoreDuplicates, ignore);
    }

    // Search settings
    private static final String keySearchFuzzy   = "search_fuzzy";
    private static final String keySearchArtists = "search_artists";
    private static final String keySearchAlbums  = "search_albums";

    public void setSearchFuzzy(boolean fuzzy) {
        setBool(keySearchFuzzy, fuzzy);
    }

    public boolean getSearchFuzzy() {
        return getBool(keySearchFuzzy, true);
    }

    public void setSearchArtists(boolean artists) {
        setBool(keySearchArtists, artists);
    }

    public boolean getSearchArtists() {
        return getBool(keySearchArtists, true);
    }

    public void setSearchAlbums(boolean albums) {
        setBool(keySearchAlbums, albums);
    }

    public boolean getSearchAlbums() {
        return getBool(keySearchAlbums, true);
    }

    // Changelog
    private static final String keyVersionCode          = "version_code";
    private static final String keyChangelogShowOnLaunch = "show_on_launch";

    public void setVersionCode() {
        setInt(keyVersionCode, BuildConfig.VERSION_CODE);
    }

    public int getStoredVersionCode() {
        return getInt(keyVersionCode, -1);
    }

    public void setShowChangelogOnLaunch(boolean show) {
        setBool(keyChangelogShowOnLaunch, show);
    }

    public boolean getShowChangelogOnLaunch() {
        return getBool(keyChangelogShowOnLaunch, true);
    }

    // Playback shuffle preference
    public boolean getRememberShuffle() {
        return getBool(keyPrefRememberShuffle, false);
    }

    public void setRememberShuffle(boolean remember) {
        setBool(keyPrefRememberShuffle, remember);
    }

    // Default page
    @CategoryItem.Type
    public int getDefaultPageType() {
        return getInt(keyPrefDefaultPage, CategoryItem.Type.ARTISTS);
    }

    public void setDefaultPageType(@CategoryItem.Type int type) {
        setInt(keyPrefDefaultPage, type);
    }

    // Legacy upgrade flag
    private static final String keyUpgraded = "pref_theme_gold";

    public boolean isLegacyUpgraded() {
        return getBool(keyUpgraded, false);
    }

    // Recently added weeks
    private static final String keyNumWeeks = "numweeks";

    public int getNumWeeks() {
        return getInt(keyNumWeeks, 2);
    }

    public void setNumWeeks(int weeks) {
        setInt(keyNumWeeks, weeks);
    }

    // Song list artwork
    private static final String keySongListArtwork = "pref_artwork_song_list";

    public boolean showArtworkInSongList() {
        return getBool(keySongListArtwork, true);
    }

    public void setShowArtworkInSongList(boolean show) {
        setBool(keySongListArtwork, show);
    }

    // --- Accessors for renamed fields ---

    public static String getKeyPrefChangelog() {
        return keyPrefChangelog;
    }

    public static String getKeyPrefFaq() {
        return keyPrefFaq;
    }

    public static String getKeyPrefHelp() {
        return keyPrefHelp;
    }

    public static String getKeyPrefRate() {
        return keyPrefRate;
    }

    public static String getKeyPrefRestorePurchases() {
        return keyPrefRestorePurchases;
    }

    public static String getKeyPrefTabChooser() {
        return keyPrefTabChooser;
    }

    public static String getKeyDisplayRemainingTime() {
        return keyDisplayRemainingTime;
    }

    public boolean hasSeenRateSnackbar() {
        return hasSeenRateSnackbar;
    }

    public void setHasSeenRateSnackbar(boolean seen) {
        this.hasSeenRateSnackbar = seen;
    }
}
