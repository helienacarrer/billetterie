package fr.heliena.billetterie.controller;


import fr.heliena.billetterie.exception.BasketNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BasketExceptionHandler extends ResponseEntityExceptionHandler {

    //cette fonction dit quel type de exception on va gérer, ici de type BilletNotFoundException
    @ExceptionHandler(value = BasketNotFoundException.class)
    protected ResponseEntity<Object> handleBasketNotFoundException(BasketNotFoundException ex, WebRequest request) {
        String bodyOfResponse = "Billet with id " + ex.getId() + " not found";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

}
