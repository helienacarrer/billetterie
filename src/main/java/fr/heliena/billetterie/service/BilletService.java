package fr.heliena.billetterie.service;


import fr.heliena.billetterie.exception.BilletIdMismatchException;
import fr.heliena.billetterie.exception.BilletNotFoundException;
import fr.heliena.billetterie.exception.NotEnoughRemainingBilletsException;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.repository.BilletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor // créer un constructeur avec uniquement les attributs requis de la classe (cad attributs final)
public class BilletService {

    private final BilletRepository billetRepository;

    public Billet getBilletById(final UUID id) {
        return billetRepository.findById(id)
                // orElseThrow revoie soit la valeur de l'optional soit balance mon exception
                .orElseThrow(() -> new BilletNotFoundException(id));
    }

    public List<Billet> getAllBillets() {
        return billetRepository.findAll();
    }

    public List<Billet> findABilletByPrice(double priceLimit) {
        return billetRepository.findABilletByPrice(priceLimit);
    }

    public Billet addOneBillet(Billet billet) {
        billet.setRemainingQuantity(billet.getTotalQuantity());
        return billetRepository.save(billet);
    }

    public Billet updateOneBillet(UUID id, Billet billetToUpdate) {
        if (!Objects.equals(id, billetToUpdate.getId())) {
            throw new BilletIdMismatchException(billetToUpdate.getId(), id);
        }

        Billet existingBillet = getBilletById(id);

        // si diff positif alors on cherche à diminuer le nombre total de billet
        int diff = existingBillet.getTotalQuantity() - billetToUpdate.getTotalQuantity();
        if (diff > existingBillet.getRemainingQuantity()) {
            // pb, on veut diminuer le nombre total de billet, mais trop de billet ont été vendus
            // il ne reste pas assez de billets
            throw new NotEnoughRemainingBilletsException(diff, existingBillet.getRemainingQuantity());
        }

        billetToUpdate.setRemainingQuantity(existingBillet.getRemainingQuantity() - diff);

        return this.billetRepository.save(billetToUpdate);
    }

    public void deleteOneBillet(UUID id) {
        if (!this.billetRepository.existsById(id)) {
            throw new BilletNotFoundException(id);
        }
        this.billetRepository.deleteById(id);
    }

    public void reserveBillets(UUID billetId, int nb) {
        if (nb == 0) return;

        Billet billet = getBilletById(billetId);

        if (billet.getRemainingQuantity() < nb) {
            throw new NotEnoughRemainingBilletsException(nb, billet.getRemainingQuantity());
        }

        billet.setRemainingQuantity(billet.getRemainingQuantity() - nb);

        billetRepository.save(billet);
    }

}
