package br.com.daltonpf3.model;

public record MediaOption(
        String formatId,
        String ext,
        double bitrate,
        long filesize,
        String codec,
        String resolution,
        int height,
        int fps
) {
    public String getFormattedSize() {
        if (filesize <= 0) return "Unknown size";
        return String.format("%.2f MB", filesize / (1024.0 * 1024.0));
    }
}
