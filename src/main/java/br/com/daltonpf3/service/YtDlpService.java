package br.com.daltonpf3.service;

import br.com.daltonpf3.config.ApplicationPropertiesLoader;
import br.com.daltonpf3.exception.DownloaderException;
import br.com.daltonpf3.enums.DownloadType;
import br.com.daltonpf3.model.MediaOption;
import br.com.daltonpf3.model.VideoMetadata;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class YtDlpService {

    public VideoMetadata fetchVideoMetadata(String url, DownloadType type) {
        try {
            ProcessBuilder pb = new ProcessBuilder("yt-dlp", "-J", "--no-warnings", "--no-playlist", url);
            Process process = pb.start();

            StringBuilder jsonOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonOutput.append(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new DownloaderException("Failed to query yt-dlp. Please check if the URL is valid.");
            }

            return parseMetadata(jsonOutput.toString(), type);

        } catch (DownloaderException e) {
            throw e;
        } catch (Exception e) {
            throw new DownloaderException("Unexpected error while fetching video metadata.", e);
        }
    }

    private VideoMetadata parseMetadata(String json, DownloadType type) {
        JSONObject root = new JSONObject(json);
        String title = root.optString("title", "Untitled video");
        JSONArray formats = root.optJSONArray("formats");

        List<MediaOption> options = (type == DownloadType.VIDEO)
                ? extractVideoOptions(formats)
                : extractAudioOptions(formats);

        return new VideoMetadata(title, options);
    }

    private List<MediaOption> extractAudioOptions(JSONArray formats) {
        List<MediaOption> pureAudio = new ArrayList<>();
        List<MediaOption> fallback = new ArrayList<>();

        if (formats != null) {
            for (int i = 0; i < formats.length(); i++) {
                JSONObject f = formats.getJSONObject(i);
                String vcodec = f.optString("vcodec", "none");
                String acodec = f.optString("acodec", "none");

                if ("none".equals(acodec)) continue;

                MediaOption option = new MediaOption(
                        f.optString("format_id", "N/A"),
                        f.optString("ext", "N/A"),
                        f.optDouble("abr", f.optDouble("tbr", 0.0)),
                        f.optLong("filesize", f.optLong("filesize_approx", 0L)),
                        acodec,
                        "audio only",
                        0,
                        0
                );

                if ("none".equals(vcodec)) {
                    pureAudio.add(option);
                } else {
                    fallback.add(option);
                }
            }
        }

        List<MediaOption> finalOptions = pureAudio.isEmpty() ? fallback : pureAudio;
        finalOptions.sort(Comparator.comparingDouble(MediaOption::bitrate));
        return finalOptions;
    }

    private List<MediaOption> extractVideoOptions(JSONArray formats) {
        List<MediaOption> videoOptions = new ArrayList<>();

        if (formats != null) {
            for (int i = 0; i < formats.length(); i++) {
                JSONObject f = formats.getJSONObject(i);
                String vcodec = f.optString("vcodec", "none");
                String formatNote = f.optString("format_note", "");

                // Ignora streams sem vídeo ou storyboards (miniaturas)
                if ("none".equals(vcodec) || "storyboard".equalsIgnoreCase(formatNote)) continue;

                int height = f.optInt("height", 0);
                String resolution = f.optString("resolution", height > 0 ? height + "p" : "Unknown");

                MediaOption option = new MediaOption(
                        f.optString("format_id", "N/A"),
                        f.optString("ext", "N/A"),
                        f.optDouble("vbr", f.optDouble("tbr", 0.0)),
                        f.optLong("filesize", f.optLong("filesize_approx", 0L)),
                        vcodec,
                        resolution,
                        height,
                        f.optInt("fps", 0)
                );

                videoOptions.add(option);
            }
        }

        // Ordena pela resolução (altura em pixels) e depois pelo bitrate
        videoOptions.sort(Comparator.comparingInt(MediaOption::height)
                .thenComparingDouble(MediaOption::bitrate));
        return videoOptions;
    }

    public void downloadMedia(String url, String formatId, DownloadType type, boolean convertToMp3) {
        String downloadFolder = ApplicationPropertiesLoader.APP_DOWNLOAD_FOLDER;
        File dir = new File(downloadFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<String> command = new ArrayList<>();
        command.add("yt-dlp");
        command.add("--no-playlist");

        if (type == DownloadType.VIDEO) {
            command.add("-f");
            command.add(formatId + "+bestaudio/best");
            command.add("--merge-output-format");
            command.add("mp4");
        } else {
            command.add("-f");
            command.add(formatId);
            command.add("-x");

            if (convertToMp3) {
                command.add("--audio-format");
                command.add("mp3");
            }
        }

        command.add("-o");
        command.add("%s/%%(title)s.%%(ext)s".formatted(downloadFolder));
        command.add(url);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new DownloaderException("Download process failed with exit code: " + exitCode);
            }
        } catch (DownloaderException e) {
            throw e;
        } catch (Exception e) {
            throw new DownloaderException("Failed to execute yt-dlp download command.", e);
        }
    }
}
