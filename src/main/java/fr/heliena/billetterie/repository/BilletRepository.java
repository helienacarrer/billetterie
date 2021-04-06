package fr.heliena.billetterie.repository;

import fr.heliena.billetterie.model.Billet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BilletRepository extends JpaRepository<Billet, UUID> {

    @Query("SELECT b FROM Billet b WHERE b.price < :priceLimit")
    List<Billet> findABilletByPrice(@Param("priceLimit") double priceLimit);

}
