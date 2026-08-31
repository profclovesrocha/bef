package com.docmanager.metadata.web;

import com.docmanager.common.dto.MetadataRequest;
import com.docmanager.common.exception.MetadataNotFoundException;
import com.docmanager.metadata.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller MVC (camada View) do Metadata Service.
 * Serve páginas HTML para CRUD de metadados, reutilizando o
 * {@link MetadataService} usado pela API REST.
 */
@Controller
@RequestMapping("/web/metadata")
@RequiredArgsConstructor
public class MetadataWebController {

    private final MetadataService metadataService;

    @GetMapping
    public String list(@RequestParam(required = false) String category,
                        @RequestParam(required = false) String tag,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {

        Pageable pageable = PageRequest.of(page, 10);
        model.addAttribute("page", metadataService.search(category, tag, pageable));
        return "metadata/list";
    }

    @GetMapping("/new")
    public String newForm() {
        return "metadata/form";
    }

    @PostMapping
    public String create(@RequestParam String documentId,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String tagsCsv,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            MetadataRequest request = MetadataRequest.builder()
                    .documentId(UUID.fromString(documentId))
                    .category(category)
                    .tags(parseTags(tagsCsv))
                    .build();
            metadataService.create(request);
            redirectAttributes.addFlashAttribute("success", "Metadado criado com sucesso.");
            return "redirect:/web/metadata";
        } catch (Exception ex) {
            model.addAttribute("error", "Erro ao criar metadado: " + ex.getMessage());
            return "metadata/form";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable UUID id, Model model) {
        try {
            model.addAttribute("metadata", metadataService.findById(id));
            return "metadata/view";
        } catch (MetadataNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("page", metadataService.search(null, null, PageRequest.of(0, 10)));
            return "metadata/list";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var metadata = metadataService.findById(id);
            model.addAttribute("metadata", metadata);
            model.addAttribute("tagsCsv", metadata.getTags() == null ? "" : String.join(", ", metadata.getTags()));
            return "metadata/form";
        } catch (MetadataNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/web/metadata";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                          @RequestParam String documentId,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String tagsCsv,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            MetadataRequest request = MetadataRequest.builder()
                    .documentId(UUID.fromString(documentId))
                    .category(category)
                    .tags(parseTags(tagsCsv))
                    .build();
            metadataService.update(id, request);
            redirectAttributes.addFlashAttribute("success", "Metadado atualizado com sucesso.");
            return "redirect:/web/metadata/" + id;
        } catch (Exception ex) {
            model.addAttribute("error", "Erro ao atualizar metadado: " + ex.getMessage());
            var metadata = metadataService.findById(id);
            model.addAttribute("metadata", metadata);
            model.addAttribute("tagsCsv", metadata.getTags() == null ? "" : String.join(", ", metadata.getTags()));
            return "metadata/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            metadataService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Metadado excluído com sucesso.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir metadado: " + ex.getMessage());
        }
        return "redirect:/web/metadata";
    }

    private Set<String> parseTags(String tagsCsv) {
        if (tagsCsv == null || tagsCsv.isBlank()) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(tagsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
