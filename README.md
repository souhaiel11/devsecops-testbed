# devsecops-testbed

Application Spring Boot **fonctionnelle** avec 30 anomalies connues, pour valider
la plateforme DevSecOps IA (Jenkins + n8n).

Voir **PROTOCOLE_VALIDATION.md** pour la liste complète des failles et les
résultats attendus (AUTO_FIX / BLOCK / NOTIFY).

## Résumé des anomalies
- 6 BLOCKER SonarQube (SQL injection, secrets, crypto faible, command injection, path traversal) -> AUTO_FIX
- 6 BUG SonarQube (division par zero, NPE, catch vide, fuite ressource, == sur String, boucle infinie) -> AUTO_FIX
- 6 CVE dependances (Log4Shell, Jackson, Commons-Collections, SnakeYAML, Text4Shell, Spring4Shell) -> BLOCK
- 6 alertes ZAP (XSS, open redirect, info leak, cookie, command, path traversal) -> NOTIFY
- 6 code smells -> NOTIFY
- Image Docker vulnerable (openjdk:8 + root) -> BLOCK + NOTIFY

## Lancer
    mvn clean package -DskipTests
    java -jar target/devsecops-testbed-1.0.0.jar
