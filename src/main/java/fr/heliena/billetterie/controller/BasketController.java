package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.controller.dto.PostRequestBasketDto;
import fr.heliena.billetterie.controller.dto.PutRequestBasketDto;
import fr.heliena.billetterie.controller.dto.ResponseBasketDto;
import fr.heliena.billetterie.controller.mapper.BasketMapper;
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
    private final BasketMapper basketMapper;

    @GetMapping
    public ResponseEntity<List<ResponseBasketDto>> getAllBaskets() {
        List<Basket> result = basketService.getAllBaskets();
        return ResponseEntity.ok(basketMapper.toDto(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBasketDto> getABasketById(@PathVariable UUID id) {
        Basket result = basketService.getABasketById(id);
        return ResponseEntity.ok(basketMapper.toDto(result));
    }

    @PostMapping
    public ResponseEntity<Void> addABasket(@Valid @RequestBody PostRequestBasketDto basketToAdd) {
        Basket result = basketService.addABasket(basketMapper.toModel(basketToAdd));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        //created:créer une reponse entity avec le statut pannier created + met un header dont la clé est location avec url de la ressource qu'on vient de créer
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseBasketDto updateABasket(@PathVariable UUID id, @Valid @RequestBody PutRequestBasketDto basketToUpdate) {
        return basketMapper.toDto(basketService.updateABasket(id, basketMapper.toModel(basketToUpdate)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteABasketById(@PathVariable UUID id) {
        basketService.deleteABasketById(id);
        return ResponseEntity.noContent().build();

    }


}
