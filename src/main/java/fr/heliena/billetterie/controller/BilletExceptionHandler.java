package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.exception.BilletNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//cette classe gère exception
@ControllerAdvice
public class BilletExceptionHandler extends ResponseEntityExceptionHandler {

    //cette fonction dit quel type de exception on va gérer, ici de type BilletNotFoundException
    @ExceptionHandler(value = BilletNotFoundException.class)
    protected ResponseEntity<Object> handleBilletNotFoundException(BilletNotFoundException ex, WebRequest request) {
        String bodyOfResponse = "Billet with id " + ex.getId() + " not found";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

}
