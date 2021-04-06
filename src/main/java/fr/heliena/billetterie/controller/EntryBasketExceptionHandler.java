package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.exception.EntryBasketIdMismatchException;
import fr.heliena.billetterie.exception.EntryBasketNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EntryBasketExceptionHandler extends ResponseEntityExceptionHandler {

    //cette fonction dit quel type de exception on va gérer
    @ExceptionHandler(value = EntryBasketNotFoundException.class)
    protected ResponseEntity<String> handleEntryBasketNotFoundException(EntryBasketNotFoundException ex) {
        String bodyOfResponse = String.format("EntryBasket with id [%s] not found", ex.getId());
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EntryBasketIdMismatchException.class)
    protected ResponseEntity<String> handleEntryBasketIdMissmatchException(EntryBasketIdMismatchException ex) {
        String bodyOfResponse = String.format("Id of the body [%s] does not match id of the path [%s]", ex.getBodyId(), ex.getPathId());
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
