package fr.heliena.billetterie.service;

import fr.heliena.billetterie.exception.BasketIdMissmatchException;
import fr.heliena.billetterie.exception.BasketNotFoundException;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasketService {

    private final BasketRepository basketRepository;

    public List<Basket> getAllBaskets() {
        return basketRepository.findAll();
    }

    public Basket getABasketById(UUID id) {
        return basketRepository.findById(id)
                .orElseThrow(() -> new BasketNotFoundException(id));
    }

    public Basket addABasket(Basket basketToAdd) {
        return basketRepository.save(basketToAdd);
    }

    //id du path et basket mis dans body
    public Basket updateABasket(UUID id, Basket basketToUpdate) {
        if (!basketRepository.existsById(id)) {
            throw new BasketNotFoundException(id);
        }
        //vérifier que id du path est celui du body
        if (!Objects.equals(id, basketToUpdate.getId())) {
            throw new BasketIdMissmatchException(id, basketToUpdate.getId());
        }
        return basketRepository.save(basketToUpdate);
    }

    public void deleteABasketById(UUID id){
        if (!basketRepository.existsById(id)){
            throw new BasketNotFoundException(id);
        }
        basketRepository.deleteById(id);
    }
}
