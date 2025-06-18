package com.simplecity.amp_library.http.lastfm;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class LastFmUtils {

    private static final String TAG = "LastFmUtils";

    public static String getBestImageUrl(List<LastFmImage> images) {
        if (images == null || images.isEmpty()) return null;

        String[] sizes = {"mega", "extralarge", "large", "medium"};

        for (String size : sizes) {
            LastFmImage image = findSize(images, size);
            if (image != null && image.url != null && !image.url.isEmpty()) {
                // Clean and resize URL (avoid mutating original object)
                return image.url.replaceFirst("/\\d*s(/|$)|/\\d*x\\d*(/|$)", "/1080s/");
            }
        }

        return null;
    }

    private static LastFmImage findSize(List<LastFmImage> images, String size) {
        for (LastFmImage image : images) {
            if (size.equalsIgnoreCase(image.getSize())) {
                return image;
            }
        }
        return null;
    }
}
