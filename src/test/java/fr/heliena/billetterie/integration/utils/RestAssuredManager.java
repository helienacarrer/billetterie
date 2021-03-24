package fr.heliena.billetterie.integration.utils;

import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class RestAssuredManager implements TestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        //chercher dans contexte spring dans var d'env une propriété qui contient le port sur lequel le serveur écoute
        //donc récupérer port random choisi par spring
        RestAssured.port = testContext.getApplicationContext().getEnvironment().getRequiredProperty("local.server.port", Integer.class);
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config()
                .jsonConfig(
                        JsonConfig.jsonConfig()
                                //par défaut ca return des float mais avoir des double (car le prix est double dans model)
                                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)
                );
    }

}
