package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.exception.EntryBasketIdMissmatchException;
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
        String bodyOfResponse = "EntryBasket with id " + ex.getId() + " not found";
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EntryBasketIdMissmatchException.class)
    protected ResponseEntity<String> handleEntryBasketIdMissmatchException(EntryBasketIdMissmatchException ex) {
        String bodyOfResponse = "Id of the body " + ex.getBodyId() + " does not match id of the path " + ex.getPathId();
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
