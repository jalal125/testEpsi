package com.simplecity.amp_library.model;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.simplecity.amp_library.R;
import com.simplecity.amp_library.sql.SqlUtils;
import com.simplecity.amp_library.sql.providers.PlayCountTable;
import com.simplecity.amp_library.sql.sqlbrite.SqlBriteUtils;
import com.simplecity.amp_library.utils.ArtworkUtils;
import com.simplecity.amp_library.utils.ComparisonUtils;
import com.simplecity.amp_library.utils.FileHelper;
import com.simplecity.amp_library.utils.StringUtils;
import io.reactivex.Single;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

public class Song implements
        Serializable,
        Comparable<Song>,
        ArtworkProvider,
        Sortable {

    private static final String TAG = "Song";

    // Fields made non-public with getters
    private long id;
    private String name;
    private String artistName;
    private long artistId;
    private String albumName;
    private long albumId;
    private long duration;
    private int year;
    private int dateAdded;
    private long playlistSongId;
    private long playlistSongPlayOrder;
    private int playCount;
    private long lastPlayed;
    private long startTime;
    private long elapsedTime = 0;
    private boolean isPaused;
    private int track;
    private int discNumber;
    private boolean isPodcast;
    private String path;
    private long bookMark;
    private String albumArtistName;

    private TagInfo tagInfo;
    private String durationLabel;
    private String bitrateLabel;
    private String sampleRateLabel;
    private String formatLabel;
    private String trackNumberLabel;
    private String discNumberLabel;
    private String fileSizeLabel;

    private String artworkKey;
    private String sortKey;

    public static final String COLUMN_ALBUM_ARTIST = "album_artist";

    public static String[] getProjection() {
        return new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.IS_PODCAST,
            MediaStore.Audio.Media.BOOKMARK,
            COLUMN_ALBUM_ARTIST
        };
    }

    public static Query getQuery() {
        return new Query.Builder()
            .uri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .projection(getProjection())
            .selection(MediaStore.Audio.Media.IS_MUSIC + "=1 OR " + MediaStore.Audio.Media.IS_PODCAST + "=1")
            .args(null)
            .sort(MediaStore.Audio.Media.TRACK)
            .build();
    }

    public Song(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
        artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        year = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
        track = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
        if (track >= 1000) {
            discNumber = track / 1000;
            track = track % 1000;
        }
        dateAdded = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        albumArtistName = artistName;
        if (cursor.getColumnIndex(COLUMN_ALBUM_ARTIST) != -1) {
            String aa = cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM_ARTIST));
            if (aa != null) {
                albumArtistName = aa;
            }
        }
        isPodcast = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_PODCAST)) == 1;
        bookMark = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK));
        setSortKey();
        setArtworkKey();
    }

    public Song() {
        // Default constructor
    }

    // Field accessors

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artistName;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getDuration() {
        return duration;
    }

    public int getYear() {
        return year;
    }

    public int getDateAdded() {
        return dateAdded;
    }

    public long getPlaylistSongId() {
        return playlistSongId;
    }

    public long getPlaylistSongPlayOrder() {
        return playlistSongPlayOrder;
    }

    public int getPlayCount() {
        return playCount;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getTrack() {
        return track;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public boolean isPodcast() {
        return isPodcast;
    }

    public String getPath() {
        return path;
    }

    public long getBookMark() {
        return bookMark;
    }

    public String getAlbumArtistName() {
        return albumArtistName;
    }

    // Async genre lookup

    public Single<Genre> fetchGenre(Context context) {
        Query q = Genre.getQuery();
        q.uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", (int) id);
        return SqlBriteUtils.createSingle(context, Genre::new, q, null);
    }

    // Database play count lookup (renamed to avoid overload conflict)
    public int fetchPlayCount(Context context) {
        Uri base = PlayCountTable.URI;
        Uri uri = ContentUris.withAppendedId(base, id);
        if (uri == null) return 0;
        Query q = new Query.Builder()
            .uri(uri)
            .projection(new String[]{PlayCountTable.COLUMN_ID, PlayCountTable.COLUMN_PLAY_COUNT})
            .build();
        return SqlUtils.createSingleQuery(
            context,
            c -> c.getInt(c.getColumnIndex(PlayCountTable.COLUMN_PLAY_COUNT)),
            0,
            q
        );
    }

    // Playback position helpers

    public void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public boolean hasPlayed() {
        return getElapsedTime() != 0
            && ((float) getElapsedTime() / (float) duration) > 0.75f;
    }

    public void setPaused() {
        elapsedTime += System.currentTimeMillis() - startTime;
        isPaused = true;
    }

    public void setResumed() {
        startTime = System.currentTimeMillis();
        isPaused = false;
    }

    private long getElapsedTime() {
        return isPaused
            ? elapsedTime
            : elapsedTime + System.currentTimeMillis() - startTime;
    }

    // Metadata labels

    public String getDurationLabel(Context ctx) {
        if (durationLabel == null) {
            durationLabel = StringUtils.makeTimeString(ctx, duration / 1000);
        }
        return durationLabel;
    }

    public TagInfo getTagInfo() {
        if (tagInfo == null) {
            tagInfo = new TagInfo(path);
        }
        return tagInfo;
    }

    public String getBitrateLabel(Context ctx) {
        if (bitrateLabel == null) {
            bitrateLabel = getTagInfo().bitrate + ctx.getString(R.string.song_info_bitrate_suffix);
        }
        return bitrateLabel;
    }

    public String getSampleRateLabel(Context ctx) {
        if (sampleRateLabel == null) {
            int sr = getTagInfo().sampleRate;
            sampleRateLabel = (sr == -1)
                ? "Unknown"
                : ((float) sr / 1000) + ctx.getString(R.string.song_info_sample_rate_suffix);
        }
        return sampleRateLabel;
    }

    public String getFormatLabel() {
        if (formatLabel == null) {
            formatLabel = getTagInfo().format;
        }
        return formatLabel;
    }

    public String getTrackNumberLabel() {
        if (trackNumberLabel == null) {
            trackNumberLabel = (track == -1)
                ? String.valueOf(getTagInfo().trackNumber)
                : String.valueOf(track);
        }
        return trackNumberLabel;
    }

    public String getDiscNumberLabel() {
        if (discNumberLabel == null) {
            discNumberLabel = (discNumber == -1)
                ? String.valueOf(getTagInfo().discNumber)
                : String.valueOf(discNumber);
        }
        return discNumberLabel;
    }

    public String getFileSizeLabel() {
        if (fileSizeLabel == null && !TextUtils.isEmpty(path)) {
            File f = new File(path);
            fileSizeLabel = FileHelper.getHumanReadableSize(f.length());
        }
        return fileSizeLabel;
    }

    // Album and album-artist builders

    public Album getAlbum() {
        return new Album.Builder()
            .id(albumId)
            .name(albumName)
            .addArtist(new Artist(artistId, artistName))
            .albumArtist(albumArtistName)
            .year(year)
            .numSongs(1)
            .numDiscs(discNumber)
            .lastPlayed(lastPlayed)
            .dateAdded(dateAdded)
            .path(path)
            .songPlayCount(playCount)
            .build();
    }

    public AlbumArtist getAlbumArtist() {
        return new AlbumArtist.Builder()
            .name(albumArtistName)
            .album(getAlbum())
            .build();
    }

    // Sorting and artwork

    @Override
    public String getSortKey() {
        if (sortKey == null) setSortKey();
        return sortKey;
    }

    @Override
    public void setSortKey() {
        sortKey = StringUtils.keyFor(name);
    }

    @Override
    @NonNull
    public String getArtworkKey() {
        if (artworkKey == null) setArtworkKey();
        return artworkKey;
    }

    private void setArtworkKey() {
        artworkKey = String.format("%s_%s", albumArtistName, albumName);
    }

    @Nullable
    @Override
    public String getRemoteArtworkUrl() {
        try {
            return "https://artwork.shuttlemusicplayer.app/api/v1/artwork"
                + "?artist=" + URLEncoder.encode(albumArtistName, Charset.forName("UTF-8").name())
                + "&album="  + URLEncoder.encode(albumName, Charset.forName("UTF-8").name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public InputStream getMediaStoreArtwork(Context ctx) {
        return ArtworkUtils.getMediaStoreArtwork(ctx, this);
    }

    @Override
    public InputStream getFolderArtwork() {
        return ArtworkUtils.getFolderArtwork(path);
    }

    @Override
    public InputStream getTagArtwork() {
        return ArtworkUtils.getTagArtwork(path);
    }

    @Override
    public List<File> getFolderArtworkFiles() {
        return ArtworkUtils.getAllFolderArtwork(path);
    }

    // equals / hashCode / toString / compareTo

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song other = (Song) o;
        return id == other.id
            && artistId == other.artistId
            && albumId == other.albumId;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (artistId ^ (artistId >>> 32));
        result = 31 * result + (int) (albumId ^ (albumId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "\nSong{" +
               "\nid=" + id +
               "\nname='" + name + '\'' +
               "\nalbumArtistName='" + albumArtistName + '\'' +
               '}';
    }

    @Override
    public int compareTo(@NonNull Song other) {
        return ComparisonUtils.compare(getSortKey(), other.getSortKey());
    }
}
