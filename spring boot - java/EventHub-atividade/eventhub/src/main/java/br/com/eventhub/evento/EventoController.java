package br.com.eventhub.evento;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evento criar(@Valid @RequestBody EventoRequest request) {
        return service.criar(request);
    }

    // ATIVIDADE: crie aqui GET /eventos, delegando a busca ao Service.
}
