package fr.heliena.billetterie;

import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.repository.BilletsRepository;
import fr.heliena.billetterie.utils.IntegrationTest;
import io.restassured.RestAssured;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// annotation créée par moi-même (dans le package utils)
// annoter toutes les classes de test d'intégration par ca
//@IntegrationTest va démarrer appli spring et écoute sur un port random
@IntegrationTest
public class BilletterieIntegrationTest {

    // récupérer le port random grâce à l'annotation et le mettre dans variable port
    @LocalServerPort
    int port;

    @Autowired
    BilletsRepository billetRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port; //RestAssured ira tjs taper sur ce port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); //si test échoue, RestAssured log tout
    }

    @Test
    void shouldGetABilletByIdAndReturnANotFoundResponse() {
        given() // équivaut à RestAssured.given() mais importé plus haut donc pas beoin
                .basePath("/billets")
        .when()
                .get(UUID.randomUUID().toString()) // on définit un id random donc sera pas en base
        .then()
                .statusCode(404); //not found
    }

    @Test
    void shouldGetABilletByIdAndReturnABillet() {

        // créer un billet car bdd vide
        Billet billet = new Billet(UUID.randomUUID(), "vielles charrues", 70.0, 100, 50);
        //save ce billet dans repo
        billetRepository.save(billet); //sur instance créée plus haut

        given()
                .basePath("/billets")
        .when()
                .get(billet.getId().toString()) // id qui va être concaténé à basePath et verbe GET
        .then()
                .statusCode(200)
                // id du body doit être égal à ce qu'il y a dans equalTo
                .body("id", equalTo(billet.getId().toString())) //ou Matchers.equalTo si pas import
                .body("name", equalTo("vielles charrues"))
                .body("price", equalTo(70.0f)) //le f indique float, car double marche pas avec RestAssured
                .body("totalQuantity", equalTo(100))
                .body("remainingQuantity", equalTo(50));
    }

    @Test
    void shouldGetAllBillets() {
        // créer 2 billets car bdd vide
        Billet billet1 = new Billet(UUID.randomUUID(), "vielles charrues", 70.0, 100, 50);
        Billet billet2= new Billet(UUID.randomUUID(), "roi Arthur", 50.0, 200, 50);

        //save ce billet dans repo
        billetRepository.save(billet1); //sur instance créée plus haut
        billetRepository.save(billet2);

        given() // équivaut à RestAssured.given() mais importé plus haut donc pas beoin
                .basePath("/billets")
        .when()
        .get() // on définit un id random donc sera pas en base
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    void shouldGetABilletWithLimitPrice() {

        // créer 2 billets car bdd vide
        Billet billet1 = new Billet(UUID.randomUUID(), "vielles charrues", 70.0, 100, 50);
        Billet billet2 = new Billet(UUID.randomUUID(), "roi Arthur", 50.0, 200, 50);

        //save ce billet dans repo
        billetRepository.save(billet1); //sur instance créée plus haut
        billetRepository.save(billet2);

        given() // équivaut à RestAssured.given() mais importé plus haut donc pas besoin
                .basePath("/billets/limitPrice")
        .when()
                .get("60") // on définit un id random donc sera pas en base
        .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }
}
