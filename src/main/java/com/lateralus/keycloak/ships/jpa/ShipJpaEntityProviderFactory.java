package com.lateralus.keycloak.ships.jpa;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.Config.Scope;

public class ShipJpaEntityProviderFactory implements JpaEntityProviderFactory {
    
    public static final String ID = "ship-jpa-entity-provider";
    
    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new ShipJpaEntityProvider();
    }
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    public void init(Scope config) {
        // Initialize if needed
    }
    
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Post-initialization if needed
    }
    
    @Override
    public void close() {
        // Cleanup if needed
    }
}