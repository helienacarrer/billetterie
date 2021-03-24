package fr.heliena.billetterie.integration.utils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//démarre spring sur un port random
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//profil pour activer tests
@ActiveProfiles("test")
//déclarer tous les TestExecutionListeners qu'on va rattacher a nos tests
//TestExecutionListener permet de faire des actions avant chaque test, après chaque...: lier spring au cycle de vie de nos tests
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {
                PostgresManager.class,
                RestAssuredManager.class
        }
)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
// dire que c'est une annotation
public @interface IntegrationTest {
}
