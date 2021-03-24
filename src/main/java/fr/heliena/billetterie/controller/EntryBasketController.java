package fr.heliena.billetterie.controller;

import fr.heliena.billetterie.controller.dto.PostRequestBilletDto;
import fr.heliena.billetterie.controller.dto.PostRequestEntryBasketDto;
import fr.heliena.billetterie.controller.dto.PutRequestEntryBasketDto;
import fr.heliena.billetterie.controller.dto.ResponseEntryBasketDto;
import fr.heliena.billetterie.controller.mapper.EntryBasketMapper;
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
    private final EntryBasketMapper entryBasketMapper;

    @PostMapping
    public ResponseEntity<Void> addOneEntry(@PathVariable UUID basketId,
                                            @Valid @RequestBody PostRequestEntryBasketDto entryBasket) {
        entryBasketService.addAnEntryBasket(basketId, entryBasketMapper.toModel(entryBasket));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneEntry(@PathVariable UUID basketId,
                                               @PathVariable UUID id) {
        entryBasketService.deleteAnEntryBasketById(basketId, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseEntryBasketDto> updateOneEntry(@PathVariable UUID basketId,
                                                                 @PathVariable UUID id,
                                                                 @Valid @RequestBody PutRequestEntryBasketDto entryBasket) {
        EntryBasket updatedEntryBasket = entryBasketService.updateAnEntryBasket(basketId, id, entryBasketMapper.toModel(entryBasket));
        return ResponseEntity.ok(entryBasketMapper.toDto(updatedEntryBasket));
    }

}
