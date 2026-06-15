package ru.yandex.practicum.commerce.cart.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;

@RestControllerAdvice
public class ShoppingCartExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public NotAuthorizedUserException handleConstraintViolation(ConstraintViolationException exception) {
        return new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RuntimeException handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return new RuntimeException("Некорректный запрос");
    }
}
