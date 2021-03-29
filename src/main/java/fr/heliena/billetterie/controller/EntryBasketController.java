package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.service.EntryBasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/baskets/{basketId}/entries")
@RequiredArgsConstructor
public class EntryBasketController {

    private final EntryBasketService entryBasketService;

    @PostMapping
    public ResponseEntity<Void> addOneEntry(@PathVariable UUID basketId,
                                            @Valid @RequestBody EntryBasket entryBasket) {
        entryBasketService.addAnEntryBasket(basketId, entryBasket);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneEntry(@PathVariable UUID basketId,
                                               @PathVariable UUID id) {
        entryBasketService.deleteAnEntryBasketById(basketId, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntryBasket> updateOneEntry(@PathVariable UUID basketId,
                                                      @PathVariable UUID id,
                                                      @Valid @RequestBody EntryBasket entryBasket) {
        EntryBasket updatedEntryBasket = entryBasketService.updateAnEntryBasket(basketId, id, entryBasket);
        return ResponseEntity.ok(updatedEntryBasket);
    }

}
