package br.com.eventhub.evento;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String local;
    private LocalDateTime data;
    private Integer vagas;

    protected Evento() { } // Construtor usado pelo JPA.

    public Evento(EventoRequest request) {
        this.nome = request.nome();
        this.local = request.local();
        this.data = request.data();
        this.vagas = request.vagas();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getLocal() { return local; }
    public LocalDateTime getData() { return data; }
    public Integer getVagas() { return vagas; }
}
