package fr.heliena.billetterie.utils;

import org.flywaydb.core.Flyway;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresManager implements TestExecutionListener {
    //PostgreSQLContainer fait le lien avec container  docker sur ma machine avec postgres dedans
    // déclarer variable POSTGRE_SQL_CONTAINER
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    //initialisation static = exécuté une fois
    static {
        // intialiser POSTGRE_SQL_CONTAINER en définissant son comportement
        // nom de image est postegres, version 13
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
                DockerImageName.parse("postgres").withTag("13")
        );
        // démarrer le container docker (container = fait tourner une appli)
        POSTGRE_SQL_CONTAINER.start();
        // propriétés pour que spring sache comment démarrer: quel username et password pour accéder à bdd postgres
        //ici par défaut
        System.setProperty("spring.datasource.url", POSTGRE_SQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRE_SQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRE_SQL_CONTAINER.getPassword());
    }

    @Override
    //faire cette méthode avant chaque test appartenant aux classes annotées @IntegrationTest
    // la méthode qu'on override autorise de lancer des exceptions
    public void beforeTestMethod(TestContext testContext) throws Exception {
        // récupérer tous beans (controller...) dans contexte spring(au lieu de autowirred)
        // ici on récupère bean flyway
        Flyway flyway = testContext.getApplicationContext().getBean(Flyway.class);
        // cleaner bdd avant chaque test donc tables vierges, uniquement données déclarées dans flyway
        flyway.clean();
        flyway.migrate();
    }
}
