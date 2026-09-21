package br.com.eventhub.evento;

import org.springframework.stereotype.Service;

@Service
public class EventoService {
    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public Evento criar(EventoRequest request) {
        if (request.vagas() == null || request.vagas() <= 0) {
            throw new RegraDeEventoException("O evento precisa possuir pelo menos uma vaga");
        }
        return repository.save(new Evento(request));
    }

    // ATIVIDADE: implemente aqui a operação de listar eventos.
}
