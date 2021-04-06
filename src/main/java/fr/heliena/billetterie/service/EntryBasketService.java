package fr.heliena.billetterie.service;


import fr.heliena.billetterie.exception.EntryBasketIdMismatchException;
import fr.heliena.billetterie.exception.EntryBasketNotFoundException;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.EntryBasket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;


@Service
@Transactional //faire des lock pendant que fait action(findByIg et save) pour pas que qqun modifie entretemps
@RequiredArgsConstructor
public class EntryBasketService {

    private final BasketService basketService;

    public void deleteAnEntryBasketById(UUID basketId, UUID id) {
        //récupérer basket a qui appartient entry
        Basket basket = basketService.getABasketById(basketId);

        EntryBasket entryBasket = getEntryBasket(basket, id);

        //suppr l'entry concernée
        basket.getEntries().remove(entryBasket);

        //save le basket qui 'aura pas cette entrée
        basketService.updateABasket(basketId, basket);
    }

    public void addAnEntryBasket(UUID basketId, EntryBasket entryBasketToAdd) {
        Basket basket = basketService.getABasketById(basketId);
        basket.getEntries().add(entryBasketToAdd);
        basketService.updateABasket(basketId, basket);
    }

    public EntryBasket updateAnEntryBasket(UUID basketId, UUID id, EntryBasket entryBasketToUpdate) {
        if (!Objects.equals(id, entryBasketToUpdate.getId())) {
            throw new EntryBasketIdMismatchException(entryBasketToUpdate.getId(), id);
        }

        Basket basket = basketService.getABasketById(basketId);

        EntryBasket entryBasket = getEntryBasket(basket, id);

        entryBasket.setBillet(entryBasketToUpdate.getBillet());
        entryBasket.setQuantity(entryBasketToUpdate.getQuantity());

        basketService.updateABasket(basketId, basket);

        return entryBasketToUpdate;
    }

    private EntryBasket getEntryBasket(Basket basket, UUID id) {
        //récupérer entry du basket concerné: stream de entrybasket
        return basket.getEntries().stream()
                //filtre pour avoir entry qui correspond a notre id
                .filter(e -> Objects.equals(e.getId(), id))
                //prend que le premier élément du stream
                //findFirst return un optional
                .findFirst()
                //sinon exception si optional vide
                .orElseThrow(() -> new EntryBasketNotFoundException(id));
    }

}
