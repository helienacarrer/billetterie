package fr.heliena.billetterie;

import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.model.Status;
import fr.heliena.billetterie.repository.BasketRepository;
import fr.heliena.billetterie.repository.BilletsRepository;
import fr.heliena.billetterie.utils.IntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class EntryBasketIntegrationTest {

    @Autowired
    BasketRepository basketRepository;

    @Autowired
    BilletsRepository billetsRepository;

    @Test
    void shouldAddAnEntryToTheBasket() {
        Billet billet = billetsRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));
        Basket basket = basketRepository.save(new Basket(null, Status.VALIDE, List.of()));

        EntryBasket body = new EntryBasket(null, billet, 1);

        given()
                .basePath("/baskets/" + basket.getId() + "/entries")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post()
        .then()
                .statusCode(201);

        Optional<Basket> oSavedBasket = basketRepository.findById(basket.getId());
        assertTrue(oSavedBasket.isPresent());

        Basket savedBasket = oSavedBasket.get();
        assertEquals(1, savedBasket.getEntries().size());

        EntryBasket savedEntry = savedBasket.getEntries().get(0);
        assertEquals(billet, savedEntry.getBillet());
        assertEquals(1, savedEntry.getQuantity());
    }

    @Test
    void shouldDeleteAnEntryFromTheBasket() {
        Billet billet = billetsRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));
        Basket basket = basketRepository.save(new Basket(null, Status.VALIDE, List.of(new EntryBasket(null, billet, 1))));

        given()
                .basePath("/baskets/" + basket.getId() + "/entries")
                .contentType(ContentType.JSON)
        .when()
                .delete(basket.getEntries().get(0).getId().toString())
        .then()
                .statusCode(204);

        Optional<Basket> oSavedBasket = basketRepository.findById(basket.getId());
        assertTrue(oSavedBasket.isPresent());

        Basket savedBasket = oSavedBasket.get();
        assertEquals(0, savedBasket.getEntries().size());
    }

    @Test
    void shouldUpdateAnEntryOfTheBasket() {
        Billet billet = billetsRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));
        Basket basket = basketRepository.save(new Basket(null, Status.VALIDE, List.of(new EntryBasket(null, billet, 1))));

        EntryBasket savedEntry = basket.getEntries().get(0);

        EntryBasket body = new EntryBasket(savedEntry.getId(), savedEntry.getBillet(), 2);

        given()
                .basePath("/baskets/" + basket.getId() + "/entries")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .put(savedEntry.getId().toString())
        .then()
                .statusCode(200);

        Optional<Basket> oSavedBasket = basketRepository.findById(basket.getId());
        assertTrue(oSavedBasket.isPresent());

        Basket savedBasket = oSavedBasket.get();
        assertEquals(1, savedBasket.getEntries().size());

        assertEquals(2, savedBasket.getEntries().get(0).getQuantity());
    }

}
