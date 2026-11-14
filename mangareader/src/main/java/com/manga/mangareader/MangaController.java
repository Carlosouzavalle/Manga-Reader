package com.manga.mangareader;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller 
public class MangaController {
    private final DriveServices drive;

    public MangaController(DriveServices drive) {
        this.drive = drive;
    }

    @GetMapping("/manga/{id}")
    public String manga(@PathVariable String id, Model model) {
        List<DriveFile> capitulos = drive.listFolder(id).stream()
            .filter(f -> f.mimeType().equals("application/vnd.google-apps.folder"))
            .sorted(Comparator.comparing(DriveFile::name, new NaturalOrder()))
            .toList();

        model.addAttribute("nome", drive.getName(id));
        model.addAttribute("mangaId", id);
        model.addAttribute("capitulos", capitulos);
        return "manga";
    }
}
