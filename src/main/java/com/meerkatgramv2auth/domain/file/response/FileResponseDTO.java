package com.meerkatgramv2auth.domain.file.response;

public record FileResponseDTO(
    String fileUri
) {
    public static FileResponseDTO from(String fileUri) {
        return new FileResponseDTO(fileUri);
    }
}
