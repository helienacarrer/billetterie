package fr.heliena.billetterie.controller;


import fr.heliena.billetterie.exception.BasketIdMissmatchException;
import fr.heliena.billetterie.exception.BasketNotFoundException;
import fr.heliena.billetterie.exception.BilletNotFoundException;
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
    protected ResponseEntity<String> handleBasketNotFoundException(BasketNotFoundException ex) {
        String bodyOfResponse = "Basket with id " + ex.getId() + " not found";
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = BasketIdMissmatchException.class)
    protected ResponseEntity<String> handleBasketIdMissmatchException(BasketIdMissmatchException ex) {
        String bodyOfResponse = "Id of the body " + ex.getBodyId() + " doesnt match id of thr path " + ex.getPathId();
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


}
