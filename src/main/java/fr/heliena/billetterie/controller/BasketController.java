package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/baskets")
public class BasketController {

    private final BasketService basketService;

    @GetMapping
    public ResponseEntity<List<Basket>> getAllBaskets(){
        List<Basket> result = basketService.getAllBaskets();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Basket> getABasketById(@PathVariable UUID id) {
        Basket result = basketService.getABasketById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> addABasket(@Valid @RequestBody Basket basketToAdd){
        Basket result = basketService.addABasket(basketToAdd);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        //created:créer une reponse entity avec le statut pannier created + met un header dont la clé est location avec url de la ressource qu'on vient de créer
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public Basket updateABasket(@PathVariable UUID id, @Valid @RequestBody Basket basketToUpdate){
        return basketService.updateABasket(id, basketToUpdate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteABasketById(@PathVariable UUID id){
        basketService.deleteABasketById(id);
        return ResponseEntity.noContent().build();

    }


}
