package com.docmanager.document.web;

import com.docmanager.common.dto.DocumentCreateRequest;
import com.docmanager.common.dto.DocumentUpdateRequest;
import com.docmanager.common.exception.DocumentNotFoundException;
import com.docmanager.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller MVC (camada View) do Document Service.
 * <p>
 * Diferente do {@link com.docmanager.document.controller.DocumentController}
 * (que é uma API REST stateless), este controller serve páginas HTML
 * renderizadas com Thymeleaf, reutilizando o mesmo Model/Service da API.
 */
@Controller
@RequestMapping("/web/documents")
@RequiredArgsConstructor
public class DocumentWebController {

    private final DocumentService documentService;

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                        @RequestParam(required = false) String fileType,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        boolean hasFilter = (name != null && !name.isBlank()) || (fileType != null && !fileType.isBlank());

        model.addAttribute("page", hasFilter
                ? documentService.search(name, fileType, pageable)
                : documentService.findAll(pageable));

        return "documents/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return "documents/form";
    }

    @PostMapping
    public String create(@RequestParam String name,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) String createdBy,
                          @RequestParam(required = false) MultipartFile file,
                          RedirectAttributes redirectAttributes) {

        DocumentCreateRequest request = DocumentCreateRequest.builder()
                .name(name)
                .description(description)
                .createdBy(createdBy)
                .build();

        try {
            documentService.create(request, (file != null && !file.isEmpty()) ? file : null);
            redirectAttributes.addFlashAttribute("success", "Documento criado com sucesso.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar documento: " + ex.getMessage());
        }
        return "redirect:/web/documents";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable UUID id, Model model) {
        try {
            model.addAttribute("document", documentService.findById(id));
            return "documents/view";
        } catch (DocumentNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("page", documentService.findAll(PageRequest.of(0, 10)));
            return "documents/list";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("document", documentService.findById(id));
            return "documents/form";
        } catch (DocumentNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/web/documents";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                          @RequestParam String name,
                          @RequestParam(required = false) String description,
                          RedirectAttributes redirectAttributes) {

        DocumentUpdateRequest request = DocumentUpdateRequest.builder()
                .name(name)
                .description(description)
                .build();

        try {
            documentService.update(id, request);
            redirectAttributes.addFlashAttribute("success", "Documento atualizado com sucesso.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar documento: " + ex.getMessage());
        }
        return "redirect:/web/documents/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            documentService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Documento excluído com sucesso.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir documento: " + ex.getMessage());
        }
        return "redirect:/web/documents";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        var doc = documentService.findById(id);
        byte[] data = documentService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        doc.getFileType() != null ? doc.getFileType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getName() + "\"")
                .body(data);
    }
}
