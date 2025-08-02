# Keycloak Ships Extension

This extension adds ship management capabilities to Keycloak, allowing organizations to manage ships and user-ship relationships.

## Features

- REST API for ship management
- Custom database tables for ships
- User-ship relationship mapping
- Integration with Keycloak organizations

## Building

```bash
mvn clean package
```

## Installation

The JAR file can be deployed to Keycloak by placing it in the `providers` directory.

For Kubernetes deployments, the JAR can be downloaded during container startup:

```yaml
curl -L -f -o /opt/bitnami/keycloak/providers/keycloak-ships-extension-1.0.0.jar \
  "https://github.com/lateralus-ventures/auth-ships-extension/releases/download/v1.0.0/keycloak-ships-extension-1.0.0.jar"
```

## API Endpoints

### List Ships
```
GET /realms/{realm}/ships
```

### Get Ship by ID
```
GET /realms/{realm}/ships/{shipId}
```

### Create Ship
```
POST /realms/{realm}/ships
```

### Update Ship
```
PUT /realms/{realm}/ships/{shipId}
```

### Delete Ship
```
DELETE /realms/{realm}/ships/{shipId}
```

### Get User Ships
```
GET /realms/{realm}/users/{userId}/ships
```

### Assign User to Ship
```
POST /realms/{realm}/users/{userId}/ships/{shipId}
```

### Remove User from Ship
```
DELETE /realms/{realm}/users/{userId}/ships/{shipId}
```