package fr.heliena.billetterie;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.heliena.billetterie.model.Billet;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;
import static org.junit.jupiter.api.Assertions.*;

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

    //avoir objet java en string pour json
    @Autowired
    ObjectMapper mapper;

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
                .get()
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
                .get("60")
        .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    //throws Exception car mapper.writeValueAsString peut renvoyer des exceptions
    void shouldUpdateABillet() throws Exception {
        Billet billet = new Billet(UUID.randomUUID(), "vielles charrues", 70.0, 100, 50);
        billetRepository.save(billet);

        String body = mapper.writeValueAsString(new Billet(billet.getId(), billet.getName(), 80.0, billet.getTotalQuantity(), billet.getRemainingQuantity()));

        given() // équivaut à RestAssured.given() mais importé plus haut donc pas besoin
                .basePath("/billets")
                //dire que body est json
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .put(billet.getId().toString())
        .then()

                //vérfifier la réponse http
                .statusCode(200)
                .body("id", equalTo(billet.getId().toString())) //ou Matchers.equalTo si pas import
                .body("name", equalTo("vielles charrues"))
                .body("price", equalTo(80.0f)) //le f indique float, car double marche pas avec RestAssured
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
    void shouldCreateABillet() throws Exception {
        //test que quand fait un post on a retour 201 (ca veut dire created) et qu'on a un header location au bon format
        Billet billet = new Billet(UUID.randomUUID(), "vielles charrues", 70.0, 100, 50);
        String body = mapper.writeValueAsString(billet);

        given()
                .basePath("/billets")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post()
        .then()
                .statusCode(201)
                // un header avec une clé "location" avec valeur regex de l'id ([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})
                //on vérifie format du header location
                .header("Location", matchesRegex("http://localhost:" + port + "/billets/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        // TODO: récupérer url qui est dans le header location, faire le get (appel http) pour obtenir le billet,
        //TODO: et faire les assertions sur les caractéristiques du billet
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
        Billet billet = new Billet(UUID.randomUUID(), "vielles charrues", 70.0, 100, 50);
        //save ce billet dans repo
        billetRepository.save(billet); //sur instance créée plus haut

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
