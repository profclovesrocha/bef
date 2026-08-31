package com.docmanager.storage.web;

import com.docmanager.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller MVC (camada View) do Storage Service.
 * Serve páginas HTML para upload, verificação, download e remoção de arquivos,
 * reutilizando o {@link StorageService} usado pela API REST.
 */
@Controller
@RequestMapping("/web/storage")
@RequiredArgsConstructor
public class StorageWebController {

    private final StorageService storageService;

    @GetMapping
    public String index() {
        return "storage/index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file, Model model) {
        try {
            model.addAttribute("result", storageService.store(file));
        } catch (Exception ex) {
            model.addAttribute("error", "Erro ao enviar arquivo: " + ex.getMessage());
        }
        return "storage/index";
    }

    @GetMapping("/check")
    public String check(@RequestParam String key, Model model) {
        model.addAttribute("checkedKey", key);
        model.addAttribute("exists", storageService.exists(key));
        return "storage/index";
    }

    @GetMapping("/{key}/download")
    public ResponseEntity<byte[]> download(@PathVariable String key) {
        byte[] data = storageService.retrieve(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(data);
    }

    @PostMapping("/{key}/delete")
    public String delete(@PathVariable String key, RedirectAttributes redirectAttributes) {
        try {
            storageService.delete(key);
            redirectAttributes.addFlashAttribute("success", "Arquivo removido com sucesso.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erro ao remover arquivo: " + ex.getMessage());
        }
        return "redirect:/web/storage";
    }
}
