package com.simplecity.amp_library.http;

import com.simplecity.amp_library.http.lastfm.LastFmService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    private static final String tag = "HttpClient"; // Renamed to match convention
    private static final String urlLastFm = "https://ws.audioscrobbler.com/2.0/";
    private static final String urlItunes = "https://itunes.apple.com/search/";
    public static final String tagArtwork = "artwork";

    private static HttpClient instance;
    private final OkHttpClient okHttpClient;
    public final LastFmService lastFmService;

    public static synchronized HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    private HttpClient() {
        this.okHttpClient = new OkHttpClient.Builder()
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.0.3", 8888)))
                .build();

        Retrofit lastFmRestAdapter = new Retrofit.Builder()
                .baseUrl(urlLastFm)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.lastFmService = lastFmRestAdapter.create(LastFmService.class);
    }
}