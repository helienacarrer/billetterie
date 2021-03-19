package fr.heliena.billetterie.controller;


import fr.heliena.billetterie.controller.dto.ResponseBilletDto;
import fr.heliena.billetterie.controller.mapper.BilletMapper;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.service.BilletsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/billets")
@RequiredArgsConstructor
public class BilletController {

    private final BilletsService billetsService;
    private final BilletMapper billetMapper;

    /////////////////////////////////////////// getById
    ///////////////// facon sans response entity
//    @GetMapping
//    public List<Billet> getAllBillets() {return this.billetsService.getAllBillets();
//    }


    //////////////// facon avec response entity
    @GetMapping
    public ResponseEntity<List<ResponseBilletDto>> getAllBillets() {
        // service return une liste
        List<Billet> result = this.billetsService.getAllBillets();
        // controller renvoie responseEntity
        return ResponseEntity.ok(billetMapper.toDto(result));
    }

    ////////////////////////////////////// getAll
    ///////////////// facon avec une exception
    @GetMapping("/{id}")
    public ResponseEntity<ResponseBilletDto> getBilletById(@PathVariable UUID id) {
        Billet billet = this.billetsService.getBilletById(id);
        return ResponseEntity.ok(billetMapper.toDto(billet));
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
    public ResponseEntity<List<ResponseBilletDto>> findABilletByPrice(@PathVariable double priceLimit) {
        List<Billet> result = billetsService.findABilletByPrice(priceLimit);
        return ResponseEntity.ok(billetMapper.toDto(result));
    }

    @PostMapping
    public ResponseEntity<Void> addOneBillet(@Valid @RequestBody Billet billetToAdd) {
        Billet billet = this.billetsService.addOneBillet(billetToAdd);
        // créer URI qui est une url de la ressource qu'on vient de créer
        //builder = on donne toutes infos pour créer URI grace au toUri
        URI location = ServletUriComponentsBuilder
                //builder initialisé avec la requete post qu'on vient de faire
                .fromCurrentRequest()
                //path est id en plus du path courant
                .path("/{id}")
                //remplace id par celui du billet
                .buildAndExpand(billet.getId())
                //construit URI
                .toUri();
        //created:créer une reponse entity avec le statut created + met un header dont la clé est location avec url de la ressource qu'on vient de créer
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseBilletDto updateOneBillet(@PathVariable UUID id, @Valid @RequestBody Billet billetToUpdate) {
        return billetMapper.toDto(this.billetsService.updateOneBillet(id, billetToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneBillet(@PathVariable UUID id) {
        this.billetsService.deleteOneBillet(id);
        return ResponseEntity.noContent().build();
    }

}

