package fr.heliena.billetterie;

import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.model.Status;
import fr.heliena.billetterie.repository.BasketRepository;
import fr.heliena.billetterie.repository.BilletsRepository;
import fr.heliena.billetterie.utils.IntegrationTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class BasketIntegrationTest {

    //besoin pour faire uri dans un test
    @LocalServerPort
    int port;

    @Autowired
    BasketRepository basketRepository;

    @Autowired
    BilletsRepository billetsRepository;

    @Test
    void shouldGetABasketByIdAndReturnANotFoundResponse () {
        given()
                .basePath("/baskets")
        .when()
                .get(UUID.randomUUID().toString()) // on définit un id random donc sera pas en base
        .then()
                .statusCode(404); //not found
    }

    @Test
    void shouldGetAEmptyBasketByIdAndReturnABasket() {
        Basket basket = basketRepository.save(new Basket(null, Status.VALIDE, List.of()));

        given()
                .basePath("/baskets")
        .when()
                .get(basket.getId().toString()) // id qui va être concaténé à basePath et verbe GET
        .then()
                .statusCode(200)
                .body("id", equalTo(basket.getId().toString())) //ou Matchers.equalTo si pas import
                .body("status", equalTo(Status.VALIDE.toString()))
                .body("entries.size()", equalTo(0));
    }

    @Test
    void shouldGetABasketByIdAndReturnABasket() {
        Billet billet = billetsRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));
        Basket basket = basketRepository.save(new Basket(null, Status.VALIDE, List.of(new EntryBasket(null, billet, 1), new EntryBasket(null, billet, 4))));

        given()
                .basePath("/baskets")
        .when()
                .get(basket.getId().toString()) // id qui va être concaténé à basePath et verbe GET
        .then()
                .statusCode(200)
                .body("id", equalTo(basket.getId().toString())) //ou Matchers.equalTo si pas import
                .body("status", equalTo(Status.VALIDE.toString()))
                .body("entries.size()", equalTo(2));
    }

    @Test
    void shouldDeleteABasketAndReturnANotFound() {
        given()
                .basePath("/baskets")
        .when()
                .delete(UUID.randomUUID().toString())
        .then()
                .statusCode(404);
    }

    @Test
    void shouldDeleteABasketAndReturnAnEmptyContent() {
        Basket basket = basketRepository.save(new Basket(null, Status.VALIDE, List.of()));

        given()
                .basePath("/baskets")
        .when()
                .delete(basket.getId().toString())
        .then()
                .statusCode(204);

        assertFalse(basketRepository.existsById(basket.getId()));
    }

    @Test
        //throws Exception car mapper.writeValueAsString peut renvoyer des exceptions: plus besoin
    void shouldUpdateABillet() {
        Basket basket = basketRepository.save(new Basket(null, Status.VALIDE, List.of()));

        //plus de mapper car restassured fait seul conversion objet en json
        Basket requestBody = new Basket(basket.getId(), Status.EN_COURS, List.of());

        given()
                .basePath("/baskets")
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .put(basket.getId().toString())
        .then()
                //vérfier la réponse http
                .statusCode(200)
                .body("id", equalTo(basket.getId().toString())) //ou Matchers.equalTo si pas import
                .body("status", equalTo(Status.EN_COURS.toString()))
                .body("entries.size()", equalTo(0));

        //vérifier que le billet est en base
        Optional<Basket> oSavedBasket = basketRepository.findById(basket.getId());
        assertTrue(oSavedBasket.isPresent());

        //vérifier les caract de ce nvau billet
        Basket savedBasket = oSavedBasket.get();
        assertEquals(savedBasket.getStatus(), Status.EN_COURS);
    }

    @Test
    void shouldCreateABasket() {
        //test que quand fait un post on a retour 201 (ca veut dire objet created) et qu'on a un header location au bon format
        Basket basket = new Basket(null, Status.VALIDE, List.of());

        String location = given()
                .basePath("/baskets")
                .contentType(ContentType.JSON)
                .body(basket)
        .when()
                .post()
        .then()
                .statusCode(201)
                // un header avec une clé "location" avec valeur regex de l'id ([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})
                //on vérifie format du header location
                .header("Location", matchesRegex("http://localhost:" + port + "/baskets/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
                // on extrait une valeur de la réponse, c'est la valeur associée à la clé location dans le header
                .extract()//car besoin pour le given suivant
                .header("Location");

        //tester que quand on get cet uri, on a statut ok bon body
        given()
                //pas path car aussi http, localhost...donc uri ici
                .baseUri(location)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("id", notNullValue()) //ou Matchers.equalTo si pas import
                //car restAssured veut des string pas des enum
                .body("status", equalTo(Status.VALIDE.toString()))
                .body("entries.size()", equalTo(0));
    }

    @Test
    void shouldNotCreateABasketIfValidationFails()  {
        // tester que valid marche pas si met nom vide
        Basket basket = new Basket(null, null, List.of());

        given()
                .basePath("/baskets")
                .contentType(ContentType.JSON)
                .body(basket)
        .when()
                .post()
        .then()
                //quad valid fonctionne pas, ca return bad request
                .statusCode(400);
    }

    @Test
    void shouldGetAllBaskets() {
        // créer 2 paniers car bdd vide
        Basket basket1 = basketRepository.save(new Basket(null, Status.EN_COURS, List.of()));
        Basket basket2 = basketRepository.save(new Basket(null, Status.VALIDE, List.of()));

        given() // équivaut à RestAssured.given() mais importé plus haut donc pas besoin
                .basePath("/baskets")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                //$ est racine du body (première accolade)
                .body("$", hasItems(
                        //Map car json
                        Matchers.<Map<String, Object>>allOf(
                                hasEntry("id", basket1.getId().toString()),
                                hasEntry("status", basket1.getStatus().toString())
                        ),
                        Matchers.<Map<String, Object>>allOf(
                                hasEntry("id", basket2.getId().toString()),
                                hasEntry("status", basket2.getStatus().toString())
                        )
                ));
    }

}
