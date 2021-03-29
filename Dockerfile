#image docker de base ave ce qui nous intéresse pour exécuter le jar (contient java)
FROM openjdk:15

#dans le container on va avoir une appli (serveur) qui va écouter sur le port 8080 du container
EXPOSE 8080

#copier le .jar et dans le contanier il s'appellera app.jar
COPY target/*.jar app.jar

#commande exécutée au démarrage du container, pour éxécuter un jar (comme on le ferait sur intllij)
ENTRYPOINT ["java", "-jar", "app.jar"]
