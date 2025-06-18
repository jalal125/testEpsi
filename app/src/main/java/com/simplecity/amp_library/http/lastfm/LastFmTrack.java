package com.simplecity.amp_library.http.lastfm;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
class LastFmTrack implements LastFmResult {

    @SerializedName("track")
    public Track track;

    public static class Track {
        @SerializedName("album")
        public TrackAlbum album;
    }

    public static class TrackAlbum {
        @SerializedName("image")
        public List<LastFmImage> images = new ArrayList<>();
    }

    @Override
    public String getImageUrl() {
        if (track != null && track.album != null) {
            return LastFmUtils.getBestImageUrl(track.album.images);
        } else {
            return null;
        }
    }
}
