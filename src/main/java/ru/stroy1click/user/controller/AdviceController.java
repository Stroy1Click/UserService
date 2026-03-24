package ru.stroy1click.user.controller;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.AuthorizationException;
import org.springframework.context.MessageSource;
import ru.stroy1click.common.exception.*;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class AdviceController {

    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleException(NotFoundException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, this.messageSource.getMessage(
                        exception.getMessageKey(),
                        exception.getArgs(),
                        Locale.getDefault()
                )
        );
        problemDetail.setTitle(
                this.messageSource.getMessage(
                        "error.title.not_found",
                        null,
                        Locale.getDefault()
                )
        );
        return problemDetail;
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleException(ValidationException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, exception.getMessage()
        );
        problemDetail.setTitle(
                this.messageSource.getMessage(
                        "error.title.validation",
                        null,
                        Locale.getDefault()
                )
        );
        return problemDetail;
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ProblemDetail handleException(AlreadyExistsException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, this.messageSource.getMessage(
                        exception.getMessageKey(),
                        exception.getArgs(),
                        Locale.getDefault()
                )
        );
        problemDetail.setTitle(
                this.messageSource.getMessage(
                        "error.title.already_exist",
                        null,
                        Locale.getDefault()
                )
        );
        return problemDetail;
    }

    @ExceptionHandler(AuthorizationException.class)
    public ProblemDetail handleException(AuthorizationException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, exception.getMessage()
        );
        problemDetail.setTitle(
                this.messageSource.getMessage(
                        "error.title.unauthorized",
                        null,
                        Locale.getDefault()
                )
        );
        return problemDetail;
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ProblemDetail handleException(RequestNotPermitted exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS, exception.getMessage()
        );
        problemDetail.setTitle(this.messageSource.getMessage(
                "error.title.too_many_requests",
                null,
                Locale.getDefault()
        ));
        problemDetail.setDetail(
                this.messageSource.getMessage(
                        "error.details.too_many_requests",
                        null,
                        Locale.getDefault()
                )
        );
        return problemDetail;
    }
}
