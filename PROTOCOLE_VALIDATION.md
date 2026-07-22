# Application de validation `devsecops-testbed`

Application Spring Boot **complète et fonctionnelle** (API de gestion de tâches)
contenant des failles **connues à l'avance**, organisées par catégorie.
Objectif : vérifier que la plateforme DevSecOps IA (Jenkins + n8n) détecte et
traite correctement chaque type d'anomalie.

L'application marche vraiment : les endpoints `/api/tasks` sont fonctionnels.
Les failles sont ajoutées à côté du code sain, comme dans une vraie application.

---

## Rappel : « traiter » a trois sens

| Décision | Signification | Types concernés |
|----------|---------------|-----------------|
| **AUTO_FIX** | La plateforme génère un patch + une PR | BLOCKER et BUG SonarQube |
| **BLOCK** | La plateforme bloque, elle ne corrige pas | CVE des dépendances / image |
| **NOTIFY** | La plateforme remonte l'alerte | Code smells, alertes ZAP |

Attendre une correction automatique d'une CVE Log4j serait une erreur : le bon
comportement est le **blocage**.

---

## Catégorie A — 6 BLOCKER SonarQube (Vulnerability) → AUTO_FIX

Fichier : `security/VulnerableSecurity.java`

| # | Faille | Règle | Fix attendu |
|---|--------|-------|-------------|
| A1 | Injection SQL par concaténation | S3649 | requête paramétrée (?) |
| A2 | Mot de passe en dur | S2068 | externaliser le secret |
| A3 | Clé de chiffrement en dur + AES/ECB | S6437 / S5542 | clé hors code + AES/GCM |
| A4 | Hachage MD5 (faible) | S4790 | SHA-256 ou bcrypt |
| A5 | Injection de commande OS | S2076 | liste blanche / pas de exec |
| A6 | Path traversal | S6096 | normaliser + confiner le chemin |

## Catégorie B — 6 BUG SonarQube → AUTO_FIX

Fichier : `service/BuggyService.java`

| # | Faille | Règle | Fix attendu |
|---|--------|-------|-------------|
| B1 | Division par zéro | S3518 | vérifier count != 0 |
| B2 | Déréférencement de null | S2259 | vérifier la nullité |
| B3 | Bloc catch vide | S2486 / S1166 | logger ou propager |
| B4 | Ressource non fermée | S2095 | try-with-resources |
| B5 | Comparaison String avec == | S4973 | utiliser .equals() |
| B6 | Boucle infinie (i jamais incrémenté) | S2189 | corriger la condition |

## Catégorie C — 6 CVE (dépendances) → BLOCK

Fichier : `pom.xml` — détectées par OWASP Dependency-Check + Trivy

| # | Dépendance | CVE | Gravité |
|---|-----------|-----|---------|
| C1 | log4j-core 2.14.1 | CVE-2021-44228 (Log4Shell) | CRITIQUE 10.0 |
| C2 | jackson-databind 2.9.8 | multiples CVE désérialisation | CRITIQUE |
| C3 | commons-collections 3.2.1 | CVE-2015-6420 | CRITIQUE |
| C4 | snakeyaml 1.30 | CVE-2022-1471 | CRITIQUE |
| C5 | commons-text 1.9 | CVE-2022-42889 (Text4Shell) | CRITIQUE |
| C6 | spring-beans 5.3.18 | CVE-2022-22965 (Spring4Shell) | CRITIQUE |

## Catégorie D — 6 alertes DAST ZAP → NOTIFY

Fichier : `controller/ApiController.java` — détectées par OWASP ZAP à l'exécution

| # | Faille | Endpoint | Détection ZAP |
|---|--------|----------|---------------|
| D1 | XSS réfléchi | `/api/greet?name=` | Reflected XSS |
| D2 | Redirection ouverte | `/api/redirect?url=` | Open Redirect |
| D3 | Fuite de stack trace | `/api/debug?path=` | Information Disclosure |
| D4 | Cookie sans HttpOnly/Secure | `/api/set-cookie` | Cookie flags |
| D5 | Injection de commande | `/api/ping?host=` | Remote OS Command |
| D6 | Path traversal | `/api/read?file=` | Path Traversal |

Note : ZAP remonte aussi les **headers de sécurité absents** (CSP, X-Frame-Options…)
sur toutes les routes → alertes supplémentaires normales.

## Catégorie E — 6 CODE SMELLS SonarQube → NOTIFY

Fichier : `util/CodeSmells.java`

| # | Smell | Règle |
|---|-------|-------|
| E1 | Champ public non final | S1104 |
| E2 | Nombre magique | S109 |
| E3 | Complexité cognitive élevée | S3776 |
| E4 | Variable inutilisée | S1481 |
| E5 | Méthode vide | S1186 |
| E6 | Duplication de littéral | S1192 |

## Catégorie F — Image Docker → BLOCK + NOTIFY

Fichier : `Dockerfile` — détectées par Trivy

| # | Faille | Type | Décision |
|---|--------|------|----------|
| F1 | Image openjdk:8 obsolète | CVE OS multiples | BLOCK |
| F2 | Conteneur en root | misconfig | NOTIFY |
| F3 | Pas de HEALTHCHECK / USER | misconfig | NOTIFY |

---

## Récapitulatif

- **12 findings AUTO_FIX** (6 BLOCKER + 6 BUG) → la plateforme doit générer des patchs
- **6 findings BLOCK** (CVE dépendances) + image → la plateforme doit bloquer
- **12 findings NOTIFY** (6 ZAP + 6 smells) → la plateforme doit alerter

Total : **30 anomalies** couvrant les 4 scanners et les 3 comportements.

---

## Procédure de validation

1. **Figer la baseline** : premier scan manuel, noter les compteurs réels
   (le décompte SonarQube dépend de ta version et de ton quality profile).
2. **Lancer le pipeline** complet sur `devsecops-testbed`.
3. **Observer les décisions** de WF1 : BLOCK / AUTO_FIX / NOTIFY.
4. **Vérifier les AUTO_FIX** : PR générées, puis re-scan (les compteurs baissent).
5. **Vérifier les BLOCK** : le pipeline signale et bloque sur CVE critiques.
6. **Vérifier les NOTIFY** : les alertes ZAP et smells remontent au rapport.
7. **Remplir la grille** et calculer le taux de conformité par catégorie.

## Grille de résultats

| Catégorie | Attendu | Détecté | Traité correctement | Taux |
|-----------|---------|---------|---------------------|------|
| A - BLOCKER | 6 AUTO_FIX | | | /6 |
| B - BUG | 6 AUTO_FIX | | | /6 |
| C - CVE | 6 BLOCK | | | /6 |
| D - ZAP | 6 NOTIFY | | | /6 |
| E - Smells | 6 NOTIFY | | | /6 |
| F - Image | BLOCK+NOTIFY | | | /3 |

**Taux global = ___ / 30**

---

## Comment lancer l'application seule (test manuel)

```
mvn clean package -DskipTests
java -jar target/devsecops-testbed-1.0.0.jar
```

Endpoints à tester :
- `GET  http://localhost:8080/api/health` → {"status":"UP"}
- `GET  http://localhost:8080/api/tasks` → liste des tâches
- `GET  http://localhost:8080/api/greet?name=<script>alert(1)</script>` → XSS
- `GET  http://localhost:8080/api/users/search?username=' OR '1'='1` → SQL injection

---

## Phrase pour le jury

« Pour valider la plateforme, j'utilise une application réellement fonctionnelle
dans laquelle j'ai injecté 30 anomalies connues, réparties par catégorie et par
scanner. Chaque anomalie a un comportement attendu défini à l'avance : correction
automatique, blocage, ou notification. Je mesure ainsi un taux de conformité
objectif, catégorie par catégorie, plutôt qu'une simple démonstration. »
