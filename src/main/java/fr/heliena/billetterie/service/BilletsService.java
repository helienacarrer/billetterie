package fr.heliena.billetterie.service;


import fr.heliena.billetterie.exception.BilletNotFoundException;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.repository.BilletsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
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
        return billetsRepository.save(billet);
    }

    public Billet updateOneBillet(UUID id, Billet billetToUpdate) {
        if (!this.billetsRepository.existsById(id)) {
            throw new BilletNotFoundException(id);
        }
        return this.billetsRepository.save(billetToUpdate);
    }

    public void deleteOneBillet(UUID id) {
        if (!this.billetsRepository.existsById(id)) {
            throw new BilletNotFoundException(id);
        }
        this.billetsRepository.deleteById(id);
    }

}
