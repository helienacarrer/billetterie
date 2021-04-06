package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.exception.BilletIdMismatchException;
import fr.heliena.billetterie.exception.BilletNotFoundException;
import fr.heliena.billetterie.exception.NotEnoughRemainingBilletsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// cette classe gère les exceptions
@ControllerAdvice
public class BilletExceptionHandler extends ResponseEntityExceptionHandler {

    //cette fonction dit quel type de exception on va gérer, ici de type BilletNotFoundException
    @ExceptionHandler(value = BilletNotFoundException.class)
    protected ResponseEntity<String> handleBilletNotFoundException(BilletNotFoundException ex) {
        String bodyOfResponse = String.format("Billet with id [%s] not found", ex.getId());
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = BilletIdMismatchException.class)
    protected ResponseEntity<String> handleEntryBasketIdMissmatchException(BilletIdMismatchException ex) {
        String bodyOfResponse = String.format("Id of the body [%s] does not match id of the path [%s]", ex.getBodyId(), ex.getPathId());
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotEnoughRemainingBilletsException.class)
    protected ResponseEntity<String> handleNotEnoughBilletsException(NotEnoughRemainingBilletsException ex) {
        String bodyOfResponse = String.format("Not enought remaining billets: wanted [%d], remaining [%d]", ex.getWanted(), ex.getRemaining());
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
