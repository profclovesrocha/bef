package br.com.eventhub.erro;

import br.com.eventhub.evento.RegraDeEventoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse validar(MethodArgumentNotValidException exception) {
        String mensagem = exception.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .sorted().distinct().collect(Collectors.joining("; "));
        return new ErroResponse(400, mensagem);
    }

    @ExceptionHandler(RegraDeEventoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse regra(RegraDeEventoException exception) {
        return new ErroResponse(400, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse jsonInvalido() {
        return new ErroResponse(400, "JSON inválido: confira os tipos dos campos e o formato da data");
    }
}
