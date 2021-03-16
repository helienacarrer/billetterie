package fr.heliena.billetterie;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.model.Status;
import fr.heliena.billetterie.repository.BasketRepository;
import fr.heliena.billetterie.repository.BilletsRepository;
import fr.heliena.billetterie.utils.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class BasketIntegrationTest {

    //besoin pour faire uri dans un test
    @LocalServerPort
    int port;

    @Autowired
    BasketRepository basketRepository;


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
    void shouldGetABasketByIdAndReturnABasket() {
        Basket basket = new Basket(UUID.randomUUID(), Status.VALIDE);
        basketRepository.save(basket);

        given()
                .basePath("/baskets")
        .when()
                .get(basket.getId().toString()) // id qui va être concaténé à basePath et verbe GET
        .then()
                .statusCode(200)
                .body("id", equalTo(basket.getId().toString())) //ou Matchers.equalTo si pas import
                .body("status", equalTo(Status.VALIDE.toString()));
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
        Basket basket = new Basket(UUID.randomUUID(), Status.VALIDE);
        basketRepository.save(basket);

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
        Basket basket = new Basket(UUID.randomUUID(), Status.VALIDE);
        basketRepository.save(basket);

        //plus de mapper car restassured fait seul conversion objet en json
        Basket requestBody = new Basket(basket.getId(), Status.EN_COURS);

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
                .body("status", equalTo(Status.EN_COURS.toString()));

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
        Basket basket = new Basket(UUID.randomUUID(), Status.VALIDE);

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
                .body("status", equalTo(Status.VALIDE.toString()));
    }

    @Test
    void shouldNotCreateABasketIfValidationFails()  {
        // tester que valid marche pas si met nom vide
        Basket basket = new Basket(UUID.randomUUID(), null);

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
        Basket basket1 = new Basket(UUID.randomUUID(), Status.EN_COURS);
        Basket basket2 = new Basket(UUID.randomUUID(), Status.VALIDE);
        basketRepository.save(basket1);
        basketRepository.save(basket2);

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
