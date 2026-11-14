package com.manga.mangareader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;


@Service 
public class DriveServices {

    private record DriveList(List<DriveFile> files, String nextPageToken) {}

    private record DriveName(String name) {}

    private final RestClient http = RestClient.create("https://www.googleapis.com");

    @Value("${manga.api-key}")
    private String apiKey;

    public List<DriveFile> listFolder(String folderId) {
        List<DriveFile> all = new ArrayList<>();
        String pageToken = null;
        do {
            final String token = pageToken;
            DriveList res = http.get()
                .uri(U -> {
                    var b = U.path("/drive/v3/files")
                        .queryParam("q", "'" + folderId + "' in parents and trashed=false")
                        .queryParam("pageSize", 1000)
                        .queryParam("fields", "nextPageToken, files(id, name, mimeType)")
                        .queryParam("key", apiKey);
                    if (token != null) b.queryParam("pageToken", token);
                    return b.build();
                })
                .retrieve()
                .body(DriveList.class);
            if (res != null && res.files() != null) all.addAll(res.files());
            pageToken = (res == null) ? null : res.nextPageToken();
        } while (pageToken != null);
        return all;
    }


    public String findCover(String mangaFolderId) {
        return listFolder(mangaFolderId).stream()
            .filter(f -> f.mimeType().startsWith("image/"))
            .filter(f -> f.name().toLowerCase().startsWith("capa"))
            .map(DriveFile::id)
            .findFirst()
            .orElse(null);
    }

    public ResponseEntity<byte[]> fetchImage(String fileId) {
        return http.get()
            .uri(u -> u.path("/drive/v3/files/{id}")
            .queryParam("alt", "media")
            .queryParam("key", apiKey)
            .build(fileId))
        .retrieve()
        .toEntity(byte[].class);
    
    }

    public String getName(String fileId) {
        DriveName r = http.get()
            .uri(u -> u.path("/drive/v3/files/{id}")
            .queryParam("fields", "name")    
            .queryParam("key", apiKey)
            .build(fileId))
        .retrieve()
        .body(DriveName.class);
        return r == null ? "Mangá" : r.name();
    }
}