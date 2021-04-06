package fr.heliena.billetterie.service;

import fr.heliena.billetterie.exception.BasketIdMismatchException;
import fr.heliena.billetterie.exception.BasketNotFoundException;
import fr.heliena.billetterie.exception.MultipleEntriesWithTheSameBilletException;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final BilletService billetService;

    public List<Basket> getAllBaskets() {
        return basketRepository.findAll();
    }

    public Basket getABasketById(UUID id) {
        return basketRepository.findById(id)
                .orElseThrow(() -> new BasketNotFoundException(id));
    }

    public Basket addABasket(Basket basketToAdd) {
        billetMustBeUnique(basketToAdd);

        basketToAdd.getEntries()
                .forEach(entryBasket ->
                        billetService.reserveBillets(entryBasket.getBillet().getId(), entryBasket.getQuantity())
                );

        return basketRepository.save(basketToAdd);
    }

    // id du path et basket mis dans body
    public Basket updateABasket(UUID id, Basket basketToUpdate) {
        //vérifier que id du path est celui du body
        if (!Objects.equals(id, basketToUpdate.getId())) {
            throw new BasketIdMismatchException(id, basketToUpdate.getId());
        }

        billetMustBeUnique(basketToUpdate);

        Basket basket = getABasketById(id);

        basket.getEntries().forEach(entryBasket -> {
            UUID billetId = entryBasket.getBillet().getId();

            int newQuantity = basketToUpdate.getEntries().stream()
                    .filter(e -> Objects.equals(e.getBillet().getId(), billetId))
                    .findFirst()
                    .map(EntryBasket::getQuantity)
                    .orElse(0);

            billetService.reserveBillets(billetId, newQuantity - entryBasket.getQuantity());
        });

        basketToUpdate.getEntries().stream()
                .filter(entryBasket -> basket.getEntries().stream().noneMatch(e -> Objects.equals(e.getBillet().getId(), entryBasket.getBillet().getId())))
                .forEach(entryBasket -> billetService.reserveBillets(entryBasket.getBillet().getId(), entryBasket.getQuantity()));

        return basketRepository.save(basketToUpdate);
    }

    public void deleteABasketById(UUID id) {
        Basket basket = getABasketById(id);

        basket.getEntries()
                .forEach(entryBasket ->
                        billetService.reserveBillets(entryBasket.getBillet().getId(), -entryBasket.getQuantity())
                );

        basketRepository.deleteById(id);
    }

    private void billetMustBeUnique(Basket basket) {
        long nbUniqueBillet = basket.getEntries().stream()
                .map(entryBasket -> entryBasket.getBillet().getId())
                .distinct()
                .count();

        if (nbUniqueBillet != basket.getEntries().size()) {
            throw new MultipleEntriesWithTheSameBilletException();
        }
    }

}
