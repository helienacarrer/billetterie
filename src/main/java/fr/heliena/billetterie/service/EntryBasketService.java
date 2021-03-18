package fr.heliena.billetterie.service;


import fr.heliena.billetterie.exception.BasketNotFoundException;
import fr.heliena.billetterie.exception.EntryBasketNotFoundException;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class EntryBasketService {

    private final BasketRepository basketRepository;

    public void deleteAnEntryBasketById(UUID basketId, UUID id) {
        //récupérer basket a qui appartient entry
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException(basketId));

        //récupérer entry du basket concerné: stream de entrybasket
        EntryBasket entryBasket = basket.getEntries().stream()
                //filtre pour avoir entry qui correspond a notre id
                .filter(e -> Objects.equals(e.getId(), id))
                //prend que le premier élément du stream
                //findFirst return un optional
                .findFirst()
                //sinon exception si optional vide
                .orElseThrow(() -> new EntryBasketNotFoundException(id));

        //suppr l'entry concernée
        basket.getEntries().remove(entryBasket);
        //save le basket qui 'aura pas cette entrée
        basketRepository.save(basket);
    }

    public void addAnEntryBasket(UUID basketId, EntryBasket entryBasketToAdd) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException(basketId));

        basket.getEntries().add(entryBasketToAdd);
        basketRepository.save(basket);
    }

    public EntryBasket updateAnEntryBasket(UUID basketId, UUID id, EntryBasket entryBasketToUpdate) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException(basketId));

        EntryBasket entryBasket = basket.getEntries().stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntryBasketNotFoundException(id));

        basket.getEntries().remove(entryBasket);
        basket.getEntries().add(entryBasketToUpdate);
        basketRepository.save(basket);

        return entryBasketToUpdate;
    }

}
