package fr.heliena.billetterie.service;


import fr.heliena.billetterie.exception.BasketNotFoundException;
import fr.heliena.billetterie.exception.BilletNotFoundException;
import fr.heliena.billetterie.exception.EntryBasketIdMissmatchException;
import fr.heliena.billetterie.exception.EntryBasketNotFoundException;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.repository.BasketRepository;
import fr.heliena.billetterie.repository.BilletsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional //faire des lock pendant que fait action(findByIg et save) pour pas que qqun modifie entretemps
@RequiredArgsConstructor
public class EntryBasketService {

    private final BasketRepository basketRepository;
    private final BilletsRepository billetsRepository;

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
        // décrémenter nbr billet restants quand on l'ajoute au panier
        UUID idBilletToAdd = entryBasketToAdd.getBillet().getId();
        Billet billet = billetsRepository.findById(idBilletToAdd)
                .orElseThrow(() -> new BilletNotFoundException(idBilletToAdd));

        if (billet.getRemainingQuantity() < entryBasketToAdd.getQuantity()) {
            // fixme create new exception and handle it
            throw new RuntimeException();
        }

        billet.setRemainingQuantity(billet.getRemainingQuantity() - entryBasketToAdd.getQuantity());
        billetsRepository.save(billet);

        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException(basketId));

//        // ne pas ajouter deux fois le meme billet mais augmenter quantity dans entryBasket
//        List<EntryBasket> listEntryBasketOfTheBasket = basket.getEntries();
//        boolean existingEntry = false;
//        for (EntryBasket entryBasket : listEntryBasketOfTheBasket) {
//            UUID idBillet = entryBasket.getBillet().getId();
//            if (Objects.equals(idBillet, entryBasketToAdd.getBillet().getId())) {
//                int actualQuantityOfThisBillet = entryBasket.getQuantity() + entryBasketToAdd.getQuantity();
//                entryBasket.setQuantity(actualQuantityOfThisBillet);
//                existingEntry = true;
//                break;
//            }
//        }
//
//        // existingEntry == false // existingEntry != true // !existingEntry
//        // si existingEntry true => false // false // false
//        // si existingEntry false => true // true // true
//        // si existingEntry est faux, ca signifie qu'aucune entrée ne contient ce billet
//        // du coup, on doit créer une nouvelle entrée avec ce billet
//        if (!existingEntry) {
//            basket.getEntries().add(entryBasketToAdd);
//        }

        // autre possibilité
        Optional<EntryBasket> existingEntryWithGivenBillet = basket.getEntries().stream()
                .filter(entryBasket -> Objects.equals(entryBasket.getBillet().getId(), entryBasketToAdd.getBillet().getId()))
                .findFirst();

        existingEntryWithGivenBillet.ifPresentOrElse(
                entryBasket -> entryBasket.setQuantity(entryBasket.getQuantity() + entryBasketToAdd.getQuantity()),
                () -> basket.getEntries().add(entryBasketToAdd)
        );

        basketRepository.save(basket);
    }

    public EntryBasket updateAnEntryBasket(UUID basketId, UUID id, EntryBasket entryBasketToUpdate) {
        if (!Objects.equals(id, entryBasketToUpdate.getId())) {
            throw new EntryBasketIdMissmatchException(entryBasketToUpdate.getId(), id);
        }

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
