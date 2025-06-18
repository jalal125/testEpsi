package com.simplecity.amp_library.http.lastfm;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class LastFmArtist implements LastFmResult {

    @SerializedName("artist")
    private Artist artist;

    @Override
    public String getImageUrl() {
        return (artist != null && artist.getImages() != null)
                ? LastFmUtils.getBestImageUrl(artist.getImages())
                : null;
    }

    public Artist getArtist() {
        return artist;
    }

    public static class Artist {
        @SerializedName("name")
        private String name;

        @SerializedName("image")
        private List<LastFmImage> images = new ArrayList<>();

        @SerializedName("bio")
        private Bio bio;

        public String getName() {
            return name;
        }

        public List<LastFmImage> getImages() {
            return images;
        }

        public Bio getBio() {
            return bio;
        }
    }

    public static class Bio {
        @SerializedName("summary")
        private String summary;

        public String getSummary() {
            return summary;
        }
    }
}
