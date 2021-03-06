package fr.heliena.billetterie.integration;

import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.repository.BilletsRepository;
import fr.heliena.billetterie.integration.utils.IntegrationTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

// annotation créée par moi-même (dans le package utils)
// annoter toutes les classes de test d'intégration par ca
// @IntegrationTest va démarrer appli spring et écoute sur un port random
@IntegrationTest
public class BilletIntegrationTest {

    //serveur a démarré sur un port random (dans IntegrationTest)
    //injecte le port sur lequel a démarré le serveur
    @LocalServerPort
    int port;

    //car dans tests on peut pas se faire injecter les composants dans constructeur, peut pas faire de constructeur
    @Autowired
    BilletsRepository billetRepository;

    @Test
    void shouldGetABilletByIdAndReturnANotFoundResponse() {
        given() // équivaut à RestAssured.given() mais importé plus haut donc pas besoin
                .basePath("/billets")
        .when()
                .get(UUID.randomUUID().toString()) // on définit un id random donc sera pas en base
        .then()
                .statusCode(404); //not found
    }

    @Test
    void shouldGetABilletByIdAndReturnABillet() {
        // créer un billet car bdd vide
        Billet billet = billetRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));

        given()
                .basePath("/billets")
        .when()
                .get(billet.getId().toString()) // id qui va être concaténé à basePath et verbe GET
        .then()
                .statusCode(200)
                // id du body doit être égal à ce qu'il y a dans equalTo
                .body("id", equalTo(billet.getId().toString())) //ou Matchers.equalTo si pas import
                .body("name", equalTo("vielles charrues"))
                .body("price", equalTo(70.0)) //le f indique float, car double marche pas avec RestAssured
                .body("totalQuantity", equalTo(100))
                .body("remainingQuantity", equalTo(50));
    }

    @Test
    void shouldGetAllBillets() {
        // créer 2 billets car bdd vide
        Billet billet1 = billetRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));
        Billet billet2 = billetRepository.save(new Billet(null, "roi Arthur", 50.0, 200, 50));

        given() // équivaut à RestAssured.given() mais importé plus haut donc pas beoin
                .basePath("/billets")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("$", hasItems(
                        Matchers.<Map<String, Object>>allOf(
                                hasEntry("id", billet1.getId().toString()),
                                hasEntry("name", billet1.getName()),
                                hasEntry("price", billet1.getPrice()),
                                hasEntry("totalQuantity", billet1.getTotalQuantity()),
                                hasEntry("remainingQuantity", billet1.getRemainingQuantity())
                        ),
                        Matchers.<Map<String, Object>>allOf(
                                hasEntry("id", billet2.getId().toString()),
                                hasEntry("name", billet2.getName()),
                                hasEntry("price", billet2.getPrice()),
                                hasEntry("totalQuantity", billet2.getTotalQuantity()),
                                hasEntry("remainingQuantity", billet2.getRemainingQuantity())
                        )
                ));
    }

    @Test
    void shouldGetABilletWithLimitPrice() {
        // créer 2 billets car bdd vide
        Billet billet1 = billetRepository.save(new Billet(UUID.randomUUID(), "vielles charrues", 70.0, 100, 50));
        Billet billet2 = billetRepository.save(new Billet(UUID.randomUUID(), "roi Arthur", 50.0, 200, 50));

        given() // équivaut à RestAssured.given() mais importé plus haut donc pas besoin
                .basePath("/billets/limitPrice")
        .when()
                .get("60")
        .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("$", hasItems(
                        Matchers.<Map<String, Object>>allOf(
                                hasEntry("id", billet2.getId().toString()),
                                hasEntry("name", billet2.getName()),
                                hasEntry("price", billet2.getPrice()),
                                hasEntry("totalQuantity", billet2.getTotalQuantity()),
                                hasEntry("remainingQuantity", billet2.getRemainingQuantity())
                        )
                ));
    }

    @Test
    void shouldUpdateABillet() {
        Billet billet = billetRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));

        Map<String, Object> requestBody = Map.of(
                "id", billet.getId(),
                "name", billet.getName(),
                "price", 80.0,
                "totalQuantity", billet.getTotalQuantity()
        );

        given() // équivaut à RestAssured.given() mais importé plus haut donc pas besoin
                .basePath("/billets")
                //dire que body est json
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .put(billet.getId().toString())
        .then()

                //vérfifier la réponse http
                .statusCode(200)
                .body("id", equalTo(billet.getId().toString())) //ou Matchers.equalTo si pas import
                .body("name", equalTo("vielles charrues"))
                .body("price", equalTo(80.0)) //le f indique float, car double marche pas avec RestAssured
                .body("totalQuantity", equalTo(100))
                .body("remainingQuantity", equalTo(50));

        //vérifier que le billet est en base
        Optional<Billet> oSavedBillet = billetRepository.findById(billet.getId());
        assertTrue(oSavedBillet.isPresent());

        //vérifier les caract de ce nvau billet
        Billet savedBillet = oSavedBillet.get();
        assertEquals(savedBillet.getName(), "vielles charrues");
        assertEquals(savedBillet.getPrice(), 80.0);
    }

    @Test
    void shouldCreateABillet() {
        //test que quand fait un post on a retour 201 (ca veut dire created) et qu'on a un header location au bon format
        Map<String, Object> body = Map.of(
                "name", "vielles charrues",
                "price", 70.0,
                "totalQuantity", 100
        );

        String location = given()
                .basePath("/billets")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post()
        .then()
                .statusCode(201)
                // un header avec une clé "location" avec valeur regex de l'id ([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})
                //on vérifie format du header location
                .header("Location", matchesRegex("http://localhost:" + port + "/billets/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
                // on extrait une valeur de la réponse, c'est la valeur associée à la clé location dans le header
                .extract()
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
                .body("name", equalTo("vielles charrues"))
                .body("price", equalTo(70.0)) //le f indique float, car double marche pas avec RestAssured
                .body("totalQuantity", equalTo(100))
                .body("remainingQuantity", equalTo(100));
    }

    @Test
    void shouldNotCreateABilletIfValidationFails() {
        // tester que valid marche pas si met nom vide
        Map<String, Object> body = Map.of(
                "name", "",
                "price", 70.0,
                "totalQuantity", 100
        );

        given()
                .basePath("/billets")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post()
        .then()
                //quad valid fonctionne pas, ca return bad request
                .statusCode(400);
    }

    @Test
    void shouldDeleteABilletAndReturnANotFound() {
        given()
                .basePath("/billets")
        .when()
                .delete(UUID.randomUUID().toString())
        .then()
                .statusCode(404);
    }


    @Test
    void shouldDeleteABilletAndReturnAnEmptyContent() {
        // créer un billet car bdd vide
        Billet billet = billetRepository.save(new Billet(null, "vielles charrues", 70.0, 100, 50));

        given()
                .basePath("/billets")
        .when()
                .delete(billet.getId().toString())
        .then()
                .statusCode(204);

        //vérifie que getId renvoie rien vu qu'on vient de le supprimer
        assertFalse(billetRepository.existsById(billet.getId()));
    }

}
