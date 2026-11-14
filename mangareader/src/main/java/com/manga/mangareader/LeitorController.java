package com.manga.mangareader;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LeitorController {

    private final DriveServices drive;

    public LeitorController(DriveServices drive) {
        this.drive = drive;
    }

    @GetMapping("/manga/{mangaId}/cap/{capId}")
    public String ler(@PathVariable String mangaId, @PathVariable String capId, Model model) {
        List<DriveFile> capitulos = drive.listFolder(mangaId).stream()
            .filter(f -> f.mimeType().equals("application/vnd.google-apps.folder"))
            .sorted(Comparator.comparing(DriveFile::name, new NaturalOrder()))
            .toList();

        int idx = -1;
        for (int i = 0; i < capitulos.size(); i++) {
            if (capitulos.get(i).id().equals(capId)) {
                idx = i;
                break;
            }
        }

        List<DriveFile> paginas = drive.listFolder(capId).stream()
            .filter(f -> f.mimeType().startsWith("image/"))
            .sorted(Comparator.comparing(DriveFile::name, new NaturalOrder()))
            .toList();

        String anterior = idx > 0 ? capitulos.get(idx - 1).id() : null;
        String proximo = (idx >= 0 && idx < capitulos.size() - 1) ? capitulos.get(idx + 1).id() : null;

        model.addAttribute("mangaId", mangaId);
        model.addAttribute("mangaNome", drive.getName(mangaId));
        model.addAttribute("capNome", drive.getName(capId));
        model.addAttribute("paginas", paginas);
        model.addAttribute("anterior", anterior);
        model.addAttribute("proximo", proximo);
        return "leitor";
    }
}