# Tessa Spring Boot Backend

Backend Java Spring Boot compatible avec le client React existant. Il expose les memes routes que le backend Express sous `http://localhost:8000/api`.

## Lancer

```powershell
.\gradlew.bat bootRun
```

Par defaut, le projet utilise H2 en fichier local: `backend-java/data/tessa-db`.

Le client React a deja `VITE_API_BASE_URL="http://localhost:8000/api"`, donc il peut appeler directement ce backend.

## Variables utiles

```properties
PORT=8000
FRONTEND_ORIGIN=http://localhost:3000,http://localhost:5173
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tessa
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
JPA_DDL_AUTO=update
```

Les roles `OWNER`, `ADMIN` et `MEMBER` sont initialises automatiquement au demarrage.
