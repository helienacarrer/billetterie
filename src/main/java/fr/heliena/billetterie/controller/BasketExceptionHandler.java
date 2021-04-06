package fr.heliena.billetterie.controller;


import fr.heliena.billetterie.exception.BasketIdMismatchException;
import fr.heliena.billetterie.exception.BasketNotFoundException;
import fr.heliena.billetterie.exception.MultipleEntriesWithTheSameBilletException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BasketExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BasketNotFoundException.class)
    protected ResponseEntity<String> handleBasketNotFoundException(BasketNotFoundException ex) {
        String bodyOfResponse = String.format("Basket with id [%s] not found", ex.getId());
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = BasketIdMismatchException.class)
    protected ResponseEntity<String> handleBasketIdMissmatchException(BasketIdMismatchException ex) {
        String bodyOfResponse = String.format("Id of the body [%s] doesnt match id of the path [%s]", ex.getBodyId(), ex.getPathId());
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MultipleEntriesWithTheSameBilletException.class)
    protected ResponseEntity<String> handleMultipleEntriesWithTheSameBilletException() {
        String bodyOfResponse = "Multiple entries with the same billet;";
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
