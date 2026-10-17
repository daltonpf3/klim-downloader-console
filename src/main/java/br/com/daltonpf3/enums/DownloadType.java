package br.com.daltonpf3.enums;

public enum DownloadType {
    AUDIO,
    VIDEO;

    public static DownloadType fromString(String value) {
        if ("video".equalsIgnoreCase(value != null ? value.trim() : "")) {
            return VIDEO;
        }
        return AUDIO;
    }
}
