package br.com.eventhub;

import br.com.eventhub.evento.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventoApiTest {
    @Autowired MockMvc mvc;
    @Autowired EventoRepository repository;

    @BeforeEach
    void limparBanco() { repository.deleteAll(); }

    private String evento(String vagas) {
        return """
                {"nome":"Java Day Recife","local":"Recife",
                 "data":"2026-10-10T09:00:00","vagas":%s}
                """.formatted(vagas);
    }

    @Test
    void criaEventoComIdEPersisteNoBanco() throws Exception {
        mvc.perform(post("/eventos").contentType("application/json").content(evento("100")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Java Day Recife"))
                .andExpect(jsonPath("$.local").value("Recife"))
                .andExpect(jsonPath("$.data").value("2026-10-10T09:00:00"))
                .andExpect(jsonPath("$.vagas").value(100));
        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findAll().getFirst().getNome()).isEqualTo("Java Day Recife");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-10", "null"})
    void rejeitaVagasInvalidasSemSalvar(String vagas) throws Exception {
        mvc.perform(post("/eventos").contentType("application/json").content(evento(vagas)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
        assertThat(repository.count()).isZero();
    }

    @Test
    void rejeitaNomeEmBranco() throws Exception {
        mvc.perform(post("/eventos").contentType("application/json")
                .content(evento("100").replace("Java Day Recife", " ")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O nome é obrigatório"));
        assertThat(repository.count()).isZero();
    }

    @Test
    void rejeitaCorpoSemCamposObrigatorios() throws Exception {
        mvc.perform(post("/eventos").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        assertThat(repository.count()).isZero();
    }

    @Test
    void rejeitaJsonMalformado() throws Exception {
        mvc.perform(post("/eventos").contentType("application/json").content("{invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("JSON inválido: confira os tipos dos campos e o formato da data"));
        assertThat(repository.count()).isZero();
    }
}
