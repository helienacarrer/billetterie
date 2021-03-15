package fr.heliena.billetterie;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.repository.BasketRepository;
import fr.heliena.billetterie.repository.BilletsRepository;
import fr.heliena.billetterie.utils.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class BasketIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    BasketRepository basketRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); //si test échoue, RestAssured log tout
    }

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
    void shouldGetABasketByIdAndReturnABillet() {
        Basket basket = new Basket(UUID.randomUUID(), "vide");
        basketRepository.save(basket);

        given()
                .basePath("/baskets")
        .when()
                .get(basket.getId().toString()) // id qui va être concaténé à basePath et verbe GET
        .then()
                .statusCode(200)
                .body("id", equalTo(basket.getId().toString())) //ou Matchers.equalTo si pas import
                .body("status", equalTo("vide"));
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
        Basket basket = new Basket(UUID.randomUUID(), "vide");
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
        //throws Exception car mapper.writeValueAsString peut renvoyer des exceptions
    void shouldUpdateABillet() throws Exception {
        Basket basket = new Basket(UUID.randomUUID(), "vide");
        basketRepository.save(basket);

        //object to json et changer le statut du panier
        String requestBody = mapper.writeValueAsString(new Basket(basket.getId(), "rempli"));

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
                .body("status", equalTo("rempli"));

        //vérifier que le billet est en base
        Optional<Basket> oSavedBasket = basketRepository.findById(basket.getId());
        assertTrue(oSavedBasket.isPresent());

        //vérifier les caract de ce nvau billet
        Basket savedBasket = oSavedBasket.get();
        assertEquals(savedBasket.getStatus(), "rempli");
    }


    @Test
    void shouldCreateABasket() throws Exception {
        //test que quand fait un post on a retour 201 (ca veut dire objet created) et qu'on a un header location au bon format
        Basket basket = new Basket(UUID.randomUUID(), "vide");
        String body = mapper.writeValueAsString(basket);

        String location = given()
                .basePath("/baskets")
                .contentType(ContentType.JSON)
                .body(body)
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
                .body("status", equalTo("vide"));
    }

    @Test
    void shouldNotCreateABasketIfValidationFails() throws Exception {
        // tester que valid marche pas si met nom vide
        Basket basket = new Basket(UUID.randomUUID(), "");
        String body = mapper.writeValueAsString(basket);

        given()
                .basePath("/baskets")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post()
        .then()
                //quad valid fonctionne pas, ca return bad request
                .statusCode(400);
    }

}
