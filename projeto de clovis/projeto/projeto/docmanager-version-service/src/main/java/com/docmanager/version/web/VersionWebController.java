package com.docmanager.version.web;

import com.docmanager.common.dto.VersionCreateRequest;
import com.docmanager.common.exception.VersionNotFoundException;
import com.docmanager.version.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller MVC (camada View) do Version Service.
 * Serve páginas HTML para consulta, criação e restauração de versões,
 * reutilizando o {@link VersionService} usado pela API REST.
 */
@Controller
@RequestMapping("/web/versions")
@RequiredArgsConstructor
public class VersionWebController {

    private final VersionService versionService;

    @GetMapping
    public String search() {
        return "versions/search";
    }

    @GetMapping("/document")
    public String byDocument(@RequestParam UUID documentId,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        model.addAttribute("documentId", documentId);
        model.addAttribute("page", versionService.findByDocumentId(documentId, PageRequest.of(page, 10)));
        return "versions/list";
    }

    @GetMapping("/new")
    public String newForm() {
        return "versions/form";
    }

    @PostMapping
    public String create(@RequestParam String documentId,
                          @RequestParam(required = false) String changeDescription,
                          @RequestParam(required = false) String createdBy,
                          @RequestParam(required = false) String storageKey,
                          @RequestParam(required = false) String fileSize,
                          @RequestParam(required = false) String checksum,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            UUID docId = UUID.fromString(documentId);
            VersionCreateRequest request = VersionCreateRequest.builder()
                    .documentId(docId)
                    .changeDescription(changeDescription)
                    .createdBy(createdBy)
                    .build();

            Long size = (fileSize != null && !fileSize.isBlank()) ? Long.parseLong(fileSize.trim()) : null;

            var created = versionService.createVersion(request, storageKey, size, checksum);
            redirectAttributes.addFlashAttribute("success", "Versão criada com sucesso.");
            return "redirect:/web/versions/document?documentId=" + created.getDocumentId();
        } catch (Exception ex) {
            model.addAttribute("error", "Erro ao criar versão: " + ex.getMessage());
            return "versions/form";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable UUID id, Model model) {
        try {
            model.addAttribute("version", versionService.findById(id));
            return "versions/view";
        } catch (VersionNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            return "versions/search";
        }
    }

    @PostMapping("/{id}/restore")
    public String restore(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            var restored = versionService.restoreVersion(id);
            redirectAttributes.addFlashAttribute("success", "Versão restaurada como v" + restored.getVersionNumber() + ".");
            return "redirect:/web/versions/document?documentId=" + restored.getDocumentId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erro ao restaurar versão: " + ex.getMessage());
            return "redirect:/web/versions";
        }
    }
}
