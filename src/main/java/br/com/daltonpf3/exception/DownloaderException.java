package br.com.daltonpf3.exception;

public class DownloaderException extends RuntimeException {
    public DownloaderException(String message) {
        super(message);
    }

    public DownloaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
