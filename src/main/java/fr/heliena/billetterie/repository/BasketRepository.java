package fr.heliena.billetterie.repository;

import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BasketRepository extends JpaRepository<Basket, UUID> {
}
