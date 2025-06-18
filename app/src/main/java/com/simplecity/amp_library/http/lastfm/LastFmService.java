package com.simplecity.amp_library.http.lastfm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFmService {

    String FORMAT_JSON = "json";
    int AUTOCORRECT = 1;

    @GET("?method=track.getInfo")
    Call<LastFmTrack> getLastFmTrackResult(
        @Query("api_key") String apiKey,
        @Query("format") String format,
        @Query("autocorrect") int autocorrect,
        @Query("artist") String artist,
        @Query("track") String track
    );

    @GET("?method=album.getInfo")
    Call<LastFmAlbum> getLastFmAlbumResult(
        @Query("api_key") String apiKey,
        @Query("format") String format,
        @Query("autocorrect") int autocorrect,
        @Query("artist") String artist,
        @Query("album") String album
    );

    @GET("?method=artist.getInfo")
    Call<LastFmArtist> getLastFmArtistResult(
        @Query("api_key") String apiKey,
        @Query("format") String format,
        @Query("autocorrect") int autocorrect,
        @Query("artist") String artist
    );
}
