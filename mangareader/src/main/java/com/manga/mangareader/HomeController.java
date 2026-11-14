package com.manga.mangareader;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DriveServices drive;

    @Value("${manga.root-folder-id}")
    private String rootId;

    public HomeController(DriveServices drive) {
        this.drive = drive;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<MangaCard> mangas = drive.listFolder(rootId).stream()
            .filter(f -> f.mimeType().equals("application/vnd.google-apps.folder"))
            .sorted(Comparator.comparing(DriveFile::name, String.CASE_INSENSITIVE_ORDER))
            .map(f -> new MangaCard(f.id(), f.name(), drive.findCover(f.id())))
            .toList();
        model.addAttribute("mangas", mangas);
        return "home";
    }
}