package br.com.daltonpf3.model;

import java.util.List;

public record VideoMetadata(String title, List<MediaOption> options) {
}
