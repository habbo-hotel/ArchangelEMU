package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class YoutubeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(YoutubeManager.class);

    public class YoutubeVideo {
        private final String id;
        private final int duration;

        YoutubeVideo(String id, int duration) {
            this.id = id;
            this.duration = duration;
        }

        public String getId() {
            return id;
        }

        public int getDuration() {
            return duration;
        }
    }

    public class YoutubePlaylist {
        private final String id;
        private final String name;
        private final String description;
        private final ArrayList<YoutubeVideo> videos;

        YoutubePlaylist(String id, String name, String description, ArrayList<YoutubeVideo> videos) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.videos = videos;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public ArrayList<YoutubeVideo> getVideos() {
            return videos;
        }
    }

    private THashMap<Integer, ArrayList<YoutubePlaylist>> playlists = new THashMap<>();
    private THashMap<String, YoutubePlaylist> playlistCache = new THashMap<>();

    public void load() {
        this.playlists.clear();
        this.playlistCache.clear();

        long millis = System.currentTimeMillis();

        Emulator.getThreading().run(() -> {
            ExecutorService youtubeDataLoaderPool = Executors.newFixedThreadPool(10);

            LOGGER.info("YouTube Manager -> Loading...");

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM youtube_playlists")) {
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        final int itemId = set.getInt("item_id");
                        final String playlistId = set.getString("playlist_id");

                        youtubeDataLoaderPool.submit(() -> {
                            ArrayList<YoutubePlaylist> playlists = this.playlists.getOrDefault(itemId, new ArrayList<>());

                            YoutubePlaylist playlist = this.getPlaylistDataById(playlistId);
                            if (playlist != null) {
                                playlists.add(playlist);
                            } else {
                                LOGGER.error("Failed to load YouTube playlist: " + playlistId);
                            }

                            this.playlists.put(itemId, playlists);
                        });
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }

            youtubeDataLoaderPool.shutdown();
            try {
                youtubeDataLoaderPool.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LOGGER.info("YouTube Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
        });
    }

    public YoutubePlaylist getPlaylistDataById(String playlistId) {
        if (this.playlistCache.containsKey(playlistId)) return this.playlistCache.get(playlistId);

        try {
            URL myUrl = new URL("https://www.youtube.com/playlist?list=" + playlistId);

            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            conn.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3731.0 Safari/537.36");

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            YoutubePlaylist playlist;
            try (BufferedReader br = new BufferedReader(isr)) {
                playlist = null;
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    if (inputLine.contains("window[\"ytInitialData\"]")) {
                        JsonObject obj = new JsonParser().parse(inputLine.substring(inputLine.indexOf("{")).replace(";", "")).getAsJsonObject();
                        
                        JsonObject meta = obj.get("microformat").getAsJsonObject().get("microformatDataRenderer").getAsJsonObject();
                        String name = meta.get("title").getAsString();
                        String description = meta.get("description").getAsString();
                        
                        ArrayList<YoutubeVideo> videos = new ArrayList<>();
                        
                        JsonArray rawVideos = obj.get("contents").getAsJsonObject().get("twoColumnBrowseResultsRenderer").getAsJsonObject().get("tabs").getAsJsonArray().get(0).getAsJsonObject().get("tabRenderer").getAsJsonObject().get("content").getAsJsonObject().get("sectionListRenderer").getAsJsonObject().get("contents").getAsJsonArray().get(0).getAsJsonObject().get("itemSectionRenderer").getAsJsonObject().get("contents").getAsJsonArray().get(0).getAsJsonObject().get("playlistVideoListRenderer").getAsJsonObject().get("contents").getAsJsonArray();
                        
                        for (JsonElement rawVideo : rawVideos) {
                            JsonObject videoData = rawVideo.getAsJsonObject().get("playlistVideoRenderer").getAsJsonObject();
                            if (!videoData.has("lengthSeconds")) continue; // removed videos
                            videos.add(new YoutubeVideo(videoData.get("videoId").getAsString(), Integer.valueOf(videoData.get("lengthSeconds").getAsString())));
                        }
                        
                        playlist = new YoutubePlaylist(playlistId, name, description, videos);
                        
                        break;
                    }
                }
            }

            this.playlistCache.put(playlistId, playlist);

            return playlist;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<YoutubePlaylist> getPlaylistsForItemId(int itemId) {
        return this.playlists.get(itemId);
    }

    public void addPlaylistToItem(int itemId, YoutubePlaylist playlist) {
        this.playlists.computeIfAbsent(itemId, k -> new ArrayList<>()).add(playlist);
    }
}