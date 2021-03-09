package fr.heliena.billetterie.controller;


import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.service.BilletsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/billets")
public class BilletController {

    private final BilletsService billetsService;

    public BilletController(BilletsService billetsService) {
        this.billetsService = billetsService;
    }


    /////////////////////////////////////////// getById
    ///////////////// facon sans response entity
//    @GetMapping
//    public List<Billet> getAllBillets() {return this.billetsService.getAllBillets();
//    }


    //////////////// facon avec response entity
    @GetMapping
    public ResponseEntity<List<Billet>> getAllBillets() {
        // service return une liste
        List<Billet> result = this.billetsService.getAllBillets();
        // controller renvoie responseEntity
        return ResponseEntity.ok(result);
    }

    ////////////////////////////////////// getAll
    ///////////////// facon avec une exception
    @GetMapping("/{id}")
    public Billet getBilletById(@PathVariable UUID id) {
        return this.billetsService.getBilletById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    ////////////////// facon avec une réponse entity facon 1
//    @GetMapping("/{id}")
//    public ResponseEntity<Billet> getBilletById(@PathVariable UUID id) {
//        Optional<Billet> oBillet = this.billetsService.getBilletById(id);
//
//        if (oBillet.isPresent()) {
//            // return ResponseEntity.status(HttpStatus.OK).body(oBillet.get());
//            // return une réponse http avec le statut ok et dans le body le Billet
//            return ResponseEntity.ok(oBillet.get());
//
//        }
//        // return un body vide et un statut not found
//        return ResponseEntity.notFound().build();
//    }

    /////////////////// facon avec une réponse entity facon 2
//    @GetMapping("/{id}")
//    public ResponseEntity<Billet> getBilletById(@PathVariable UUID id) {
//        Optional<Billet> oBillet = this.billetsService.getBilletById(id);
//        //map sur un optional return un optional
//        // si optional n'est pas vide, alors le résultat est un optional avec le résultats de la fonction
//        // si optional vide alors le résultat est un optional vide
//        Optional<ResponseEntity<Billet>> oResponse = oBillet.map(billet -> ResponseEntity.ok(billet));
//        // si optional pas vide ca return son contenu, sinon return not found
//        ResponseEntity responseEntity = oResponse.orElse(ResponseEntity.notFound().build());
//        return responseEntity;
//    }

    @GetMapping("/limitPrice/{priceLimit}")
    public ResponseEntity<List<Billet>> FindABilletByPrice(@PathVariable double priceLimit) {
        List<Billet> result = billetsService.findABilletByPrice(priceLimit);
        return ResponseEntity.ok(result);
    }

}

