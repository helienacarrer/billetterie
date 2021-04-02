package fr.heliena.billetterie.repository;

import fr.heliena.billetterie.model.Basket;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, UUID> {
}
