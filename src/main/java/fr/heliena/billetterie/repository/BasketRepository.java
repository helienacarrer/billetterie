package fr.heliena.billetterie.repository;

import fr.heliena.billetterie.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BasketRepository extends JpaRepository<Basket, UUID> {
}
