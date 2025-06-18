package com.simplecity.amp_library.http;

import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private static final String tag = "HttpServer";
    private static final String mimeTypeHtml = "text/html";
    private static final String mimeTypeImage = "image/png";

    private static class Holder {
        private static final HttpServer instance = new HttpServer();
    }

    private final NanoServer server;
    private String audioFileToServe;
    private byte[] imageBytesToServe;
    private FileInputStream audioInputStream;
    private ByteArrayInputStream imageInputStream;
    private boolean isStarted = false;

    public static HttpServer getInstance() {
        return Holder.instance;
    }

    private HttpServer() {
        this.server = new NanoServer();
    }

    public void serveAudio(String audioUri) {
        if (audioUri != null) this.audioFileToServe = audioUri;
    }

    public void serveImage(byte[] imageBytes) {
        if (imageBytes != null) this.imageBytesToServe = imageBytes;
    }

    public void clearImage() {
        this.imageBytesToServe = null;
    }

    public void start() {
        if (!isStarted) {
            try {
                server.start();
                isStarted = true;
            } catch (IOException e) {
                Log.e(tag, "Error starting server: " + e.getMessage());
            }
        }
    }

    public void stop() {
        if (isStarted) {
            server.stop();
            isStarted = false;
            cleanupAudioStream();
            cleanupImageStream();
        }
    }

    private class NanoServer extends NanoHTTPD {
        NanoServer() {
            super(5000);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();

            if (uri.contains("audio")) return serveAudioFile(session);
            else if (uri.contains("image")) return serveImageBytes();

            Log.e(tag, "Returning NOT_FOUND response");
            return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeTypeHtml, "File not found");
        }

        private Response serveAudioFile(IHTTPSession session) {
            if (audioFileToServe == null) {
                Log.e(tag, "Audio file to serve null");
                return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeTypeHtml, "File not found");
            }

            try {
                File file = new File(audioFileToServe);
                Map<String, String> headers = session.getHeaders();
                String range = headers.getOrDefault("range", "bytes=0-");

                long start;
                long end;
                long fileLength = file.length();

                String rangeValue = range.trim().substring("bytes=".length());
                if (rangeValue.startsWith("-")) {
                    end = fileLength - 1;
                    start = fileLength - 1 - Long.parseLong(rangeValue.substring(1));
                } else {
                    String[] ranges = rangeValue.split("-");
                    start = Long.parseLong(ranges[0]);
                    end = (ranges.length > 1) ? Long.parseLong(ranges[1]) : fileLength - 1;
                }

                end = Math.min(end, fileLength - 1);

                if (start > end) {
                    return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, mimeTypeHtml, range);
                }

                cleanupAudioStream();
                audioInputStream = new FileInputStream(file);
                long skipped = audioInputStream.skip(start); // Check how many bytes were actually skipped
                if (skipped != start) {
                    Log.w(tag, "Expected to skip " + start + " bytes, but skipped " + skipped + " bytes.");
                }
                long contentLength = end - start + 1;

                Response response = newFixedLengthResponse(
                    Response.Status.PARTIAL_CONTENT,
                    getMimeType(audioFileToServe),
                    audioInputStream,
                    contentLength
                );

                response.addHeader("Content-Length", String.valueOf(contentLength));
                response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                response.addHeader("Content-Type", getMimeType(audioFileToServe));

                return response;
            } catch (IOException e) {
                Log.e(tag, "Error serving audio: " + e.getMessage(), e);
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, mimeTypeHtml, "Internal server error");
            }
        }

        private Response serveImageBytes() {
            if (imageBytesToServe == null) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeTypeHtml, "Image bytes null");
            }

            cleanupImageStream();
            imageInputStream = new ByteArrayInputStream(imageBytesToServe);
            Log.i(tag, "Serving image bytes: " + imageBytesToServe.length);

            return newFixedLengthResponse(Response.Status.OK, mimeTypeImage, imageInputStream, imageBytesToServe.length);
        }
    }

    private void cleanupAudioStream() {
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException ignored) {}
        }
    }

    private void cleanupImageStream() {
        if (imageInputStream != null) {
            try {
                imageInputStream.close();
            } catch (IOException ignored) {}
        }
    }

    private static final Map<String, String> mimeTypes;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("css", "text/css");
        map.put("htm", "text/html");
        map.put("html", "text/html");
        map.put("xml", "text/xml");
        map.put("java", "text/x-java-source, text/java");
        map.put("md", "text/plain");
        map.put("txt", "text/plain");
        map.put("asc", "text/plain");
        map.put("gif", "image/gif");
        map.put("jpg", "image/jpeg");
        map.put("jpeg", "image/jpeg");
        map.put("png", "image/png");
        map.put("mp3", "audio/mpeg");
        map.put("m3u", "audio/mpeg-url");
        map.put("mp4", "video/mp4");
        map.put("ogv", "video/ogg");
        map.put("flv", "video/x-flv");
        map.put("mov", "video/quicktime");
        map.put("swf", "application/x-shockwave-flash");
        map.put("js", "application/javascript");
        map.put("pdf", "application/pdf");
        map.put("doc", "application/msword");
        map.put("ogg", "application/x-ogg");
        map.put("zip", "application/octet-stream");
        map.put("exe", "application/octet-stream");
        map.put("class", "application/octet-stream");
        mimeTypes = Collections.unmodifiableMap(map);
    }

    private String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        return mimeTypes.getOrDefault(extension, "application/octet-stream");
    }
}
