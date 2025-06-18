package com.simplecity.amp_library.model;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class TagUpdate {

    private String title;
    private String album;
    private String artist;
    private String albumArtist;
    private String genre;
    private String year;
    private String track;
    private String trackTotal;
    private String disc;
    private String discTotal;
    private String lyrics;
    private String comment;

    private boolean titleHasChanged;
    private boolean albumHasChanged;
    private boolean artistHasChanged;
    private boolean albumArtistHasChanged;
    private boolean genreHasChanged;
    private boolean yearHasChanged;
    private boolean trackHasChanged;
    private boolean trackTotalHasChanged;
    private boolean discHasChanged;
    private boolean discTotalHasChanged;
    private boolean lyricsHasChanged;
    private boolean commentHasChanged;

    public TagUpdate(Tag tag) {
        try {
            this.title = tag.getFirst(FieldKey.TITLE);
        } catch (UnsupportedOperationException ignored) {
            // Field not supported
        }
        try {
            this.album = tag.getFirst(FieldKey.ALBUM);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.artist = tag.getFirst(FieldKey.ARTIST);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.genre = tag.getFirst(FieldKey.GENRE);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.year = tag.getFirst(FieldKey.YEAR);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.track = tag.getFirst(FieldKey.TRACK);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.trackTotal = tag.getFirst(FieldKey.TRACK_TOTAL);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.disc = tag.getFirst(FieldKey.DISC_NO);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.discTotal = tag.getFirst(FieldKey.DISC_TOTAL);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.lyrics = tag.getFirst(FieldKey.LYRICS);
        } catch (UnsupportedOperationException ignored) {}
        try {
            this.comment = tag.getFirst(FieldKey.COMMENT);
        } catch (UnsupportedOperationException ignored) {}
    }

    public boolean hasChanged() {
        return titleHasChanged || albumHasChanged || artistHasChanged ||
                albumArtistHasChanged || genreHasChanged || yearHasChanged ||
                trackHasChanged || trackTotalHasChanged || discHasChanged ||
                discTotalHasChanged || lyricsHasChanged || commentHasChanged;
    }

    public void updateTag(Tag tag) {
        if (tag == null) return;

        if (titleHasChanged) trySetField(tag, FieldKey.TITLE, title);
        if (albumHasChanged) trySetField(tag, FieldKey.ALBUM, album);
        if (artistHasChanged) trySetField(tag, FieldKey.ARTIST, artist);
        if (albumArtistHasChanged) trySetField(tag, FieldKey.ALBUM_ARTIST, albumArtist);
        if (genreHasChanged) trySetField(tag, FieldKey.GENRE, genre);
        if (yearHasChanged) trySetField(tag, FieldKey.YEAR, year);
        if (trackHasChanged) trySetField(tag, FieldKey.TRACK, track);
        if (trackTotalHasChanged) trySetField(tag, FieldKey.TRACK_TOTAL, trackTotal);
        if (discHasChanged) trySetField(tag, FieldKey.DISC_NO, disc);
        if (discTotalHasChanged) trySetField(tag, FieldKey.DISC_TOTAL, discTotal);
        if (lyricsHasChanged) trySetField(tag, FieldKey.LYRICS, lyrics);
        if (commentHasChanged) trySetField(tag, FieldKey.COMMENT, comment);
    }

    private void trySetField(Tag tag, FieldKey key, String value) {
        try {
            tag.setField(key, value);
        } catch (Exception ignored) {
            // Failed to set field, skip silently
        }
    }

    public void softSetTitle(String value) { softSet(value, this.title, v -> this.title = v, b -> this.titleHasChanged = b); }
    public void softSetAlbum(String value) { softSet(value, this.album, v -> this.album = v, b -> this.albumHasChanged = b); }
    public void softSetArtist(String value) { softSet(value, this.artist, v -> this.artist = v, b -> this.artistHasChanged = b); }
    public void softSetAlbumArtist(String value) { softSet(value, this.albumArtist, v -> this.albumArtist = v, b -> this.albumArtistHasChanged = b); }
    public void softSetGenre(String value) { softSet(value, this.genre, v -> this.genre = v, b -> this.genreHasChanged = b); }
    public void softSetYear(String value) { softSet(value, this.year, v -> this.year = v, b -> this.yearHasChanged = b); }
    public void softSetTrack(String value) { softSet(value, this.track, v -> this.track = v, b -> this.trackHasChanged = b); }
    public void softSetTrackTotal(String value) { softSet(value, this.trackTotal, v -> this.trackTotal = v, b -> this.trackTotalHasChanged = b); }
    public void softSetDisc(String value) { softSet(value, this.disc, v -> this.disc = v, b -> this.discHasChanged = b); }
    public void softSetDiscTotal(String value) { softSet(value, this.discTotal, v -> this.discTotal = v, b -> this.discTotalHasChanged = b); }
    public void softSetLyrics(String value) { softSet(value, this.lyrics, v -> this.lyrics = v, b -> this.lyricsHasChanged = b); }
    public void softSetComment(String value) { softSet(value, this.comment, v -> this.comment = v, b -> this.commentHasChanged = b); }

    private interface ValueSetter { void accept(String value); }
    private interface FlagSetter { void accept(boolean flag); }

    private void softSet(String newValue, String currentValue, ValueSetter setter, FlagSetter flagSetter) {
        if (newValue == null) return;
        if (currentValue == null || !currentValue.equals(newValue)) {
            setter.accept(newValue);
            flagSetter.accept(true);
        }
    }
}
