package fr.heliena.billetterie.service;


import fr.heliena.billetterie.exception.BasketIdMissmatchException;
import fr.heliena.billetterie.exception.BilletNotFoundException;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.repository.BilletsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor //constructeur avec que les final
public class BilletsService {

    private final BilletsRepository billetsRepository;

    public Billet getBilletById(final UUID id) {
        return billetsRepository.findById(id)
                //orElseThrow revoie soit la valeur de l'optional soit balance mon exception
                .orElseThrow(() -> new BilletNotFoundException(id));
    }

    public List<Billet> getAllBillets() {
        return billetsRepository.findAll();
    }

    public List<Billet> findABilletByPrice(double priceLimit) {
        return billetsRepository.findABilletByPrice(priceLimit);
    }

    public Billet addOneBillet(Billet billet) {
        billet.setRemainingQuantity(billet.getTotalQuantity());
        return billetsRepository.save(billet);
    }

    public Billet updateOneBillet(UUID id, Billet billetToUpdate) {
        if (!Objects.equals(id, billetToUpdate.getId())) {
            throw new BasketIdMissmatchException(billetToUpdate.getId(), id);
        }

        Billet existingBillet = billetsRepository.findById(id)
                .orElseThrow(() -> new BilletNotFoundException(id));

        // si diff positif alors on cherche à diminuer le nombre total de billet
        int diff = existingBillet.getTotalQuantity() - billetToUpdate.getTotalQuantity();
        if (diff > existingBillet.getRemainingQuantity()) {
            // fixme
            // pb, on veut diminuer le nombre total de billet, mais trop de billet ont été vendus
            // il ne reste pas assez de billets
            throw new RuntimeException();
        }

        billetToUpdate.setRemainingQuantity(existingBillet.getRemainingQuantity() - diff);

        return this.billetsRepository.save(billetToUpdate);
    }

    public void deleteOneBillet(UUID id) {
        if (!this.billetsRepository.existsById(id)) {
            throw new BilletNotFoundException(id);
        }
        this.billetsRepository.deleteById(id);
    }

}
