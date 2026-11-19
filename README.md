# depistage-app : gestion de campagnes de dépistage

Application web Spring Boot 3 + Thymeleaf (pages rendues côté serveur) et API REST documentée avec Swagger.
**Toutes les données sont fictives.**

## Prérequis
- Java 17 ou plus
- Maven 3.9 ou plus
- (déploiement) un compte sur une plateforme d'hébergement et une base PostgreSQL hébergée

## Lancement local (profil `dev`, base H2 en mémoire)
```bash
mvn spring-boot:run
```
| Adresse | Contenu |
|---|---|
| http://localhost:8080/ | Tableau de bord |
| http://localhost:8080/swagger-ui.html | Documentation de l'API (Swagger UI) |
| http://localhost:8080/h2-console | Console H2 (JDBC URL : `jdbc:h2:mem:depistage`, utilisateur `sa`, mot de passe vide) |
| http://localhost:8080/actuator/health | Point de contrôle de santé |

Au démarrage, la base reçoit : 3 types, 2 campagnes, 5 sites, 4 agents, 12 participants, 20 dépistages (tous les résultats représentés).

## Tests
```bash
mvn test
```
28 tests automatisés : règles des services (doublon, campagne terminée, type non proposé, agent inactif, date hors période,
orientation manquante, suppressions refusées, statistiques sur base vide) et contrôleurs (404, formulaire invalide, redirection
après succès, API 200/201/400/404/409).

## Contrôle des erreurs (profil `dev` uniquement)
- `GET /simuler-erreur` : page d'erreur 500 générique (aucun détail technique).
- `GET /api/simuler-erreur` : JSON d'erreur uniforme `INTERNAL_ERROR`.
- `GET /campagnes/9999` : page 404 personnalisée ; `DELETE /api/depistages/{id}` : 204.

## Profils
| Profil | Base | Particularités |
|---|---|---|
| `dev` (défaut) | H2 en mémoire | console H2, SQL visible, cache Thymeleaf désactivé, schéma recréé à chaque démarrage |
| `prod` | PostgreSQL hébergée | schéma en `update` (jamais recréé), cache activé, SQL masqué, **aucun secret dans le dépôt** |

### Variables d'environnement du profil `prod`
| Variable | Exemple |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://hote:5432/depistage` |
| `DB_USER` | utilisateur dédié à l'application |
| `DB_PASSWORD` | mot de passe |
| `PORT` | fourni par la plateforme (8080 par défaut) |

Essai local du profil `prod` :
```bash
export SPRING_PROFILES_ACTIVE=prod DB_URL=jdbc:postgresql://localhost:5432/depistage DB_USER=depistage DB_PASSWORD=...
mvn spring-boot:run
```

## Déploiement (Render, Railway ou équivalent)
1. Créer une base PostgreSQL hébergée et un utilisateur dédié.
2. Créer un service web relié au dépôt Git ; la plateforme utilise le `Dockerfile` fourni.
3. Renseigner les variables d'environnement ci-dessus.
4. Chemin du contrôle de santé : `/actuator/health`.
5. Déployer, lire les journaux, corriger.
6. Ajouter un participant en ligne, redémarrer le service : la donnée est conservée (les données de démonstration ne se
   rechargent que si la base est vide).

> **Attention au correcteur :** sur un service gratuit, l'application s'endort après quelques minutes d'inactivité.
> La première page demandée peut mettre une minute à s'afficher.

## Adresses en ligne
- Application : *à compléter*
- Swagger UI : *à compléter* (`/swagger-ui.html`)

## Architecture
```
com.depistage
├── entity       six entités JPA, relations LAZY, jointure campagne_type
├── enums        statuts, résultats, fonctions, sexes
├── dto          formulaires (validation) et réponses (libellés lisibles) ; dto.validation : règles croisées
├── mapper       conversions entité ↔ DTO
├── repository   requêtes dérivées et JPQL paramétré (jamais de SQL concaténé)
├── service      TOUTE la logique métier (unicité, règles du dépistage, suppressions protégées, statistiques)
├── controller   pages Thymeleaf ; controller.api : API REST
├── exception    exceptions métier + deux gestionnaires (pages en HTML, API en JSON uniforme)
└── config       données de démonstration, Swagger, messages de validation
```

## Sécurité
Ce projet est un exercice : pas d'authentification, données fictives uniquement. Pour un vrai site de santé il faudrait
une connexion, des rôles, le chiffrement et la protection des données personnelles.
