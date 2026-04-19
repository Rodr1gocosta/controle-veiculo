package com.rodr1gocosta.controle_veiculo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VeiculoNotFoundException.class)
    public ProblemDetail handleVeiculoNotFound(VeiculoNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Veículo não encontrado");
        problem.setType(URI.create("/errors/not-found"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Dados inválidos");
        problem.setTitle("Erro de validação");
        problem.setType(URI.create("/errors/validation"));
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Erro interno");
        problem.setType(URI.create("/errors/internal"));
        return problem;
    }

    @ExceptionHandler(PlacaDuplicadaException.class)
    public ProblemDetail handlePlacaDuplicada(PlacaDuplicadaException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Placa duplicada");
        problem.setType(URI.create("/errors/placa-duplicada"));
        return problem;
    }

    @ExceptionHandler(CotacaoIndisponivelException.class)
    public ProblemDetail handleCotacaoIndisponivel(CotacaoIndisponivelException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setTitle("Serviço Indisponível");
        problem.setType(URI.create("/errors/cotacao-indisponivel"));
        return problem;
    }
}

