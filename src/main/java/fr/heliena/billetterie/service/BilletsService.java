package fr.heliena.billetterie.service;


import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.repository.BilletsRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Service
public class BilletsService {

    private final BilletsRepository billetsRepository;

    public BilletsService(BilletsRepository billetsRepository) {
        this.billetsRepository = billetsRepository;
    }

    public Optional<Billet> getBilletById(final UUID id) {
        return billetsRepository.findById(id);
    }

    public List<Billet> getAllBillets() {
        return billetsRepository.findAll();
    }

    public List<Billet> findABilletByPrice(double priceLimit) {
        return billetsRepository.findABilletByPrice(priceLimit);
    }

}
