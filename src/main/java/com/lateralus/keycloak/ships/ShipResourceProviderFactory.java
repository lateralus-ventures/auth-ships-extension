package com.lateralus.keycloak.ships;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import org.keycloak.Config.Scope;

public class ShipResourceProviderFactory implements RealmResourceProviderFactory {
    
    public static final String ID = "ships";
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new ShipResourceProvider(session);
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