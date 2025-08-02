package com.lateralus.keycloak.ships.jpa;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import java.util.Arrays;
import java.util.List;

public class ShipJpaEntityProvider implements JpaEntityProvider {
    
    @Override
    public List<Class<?>> getEntities() {
        return Arrays.asList(ShipEntity.class, UserShipEntity.class);
    }
    
    @Override
    public String getChangelogLocation() {
        return "META-INF/ship-changelog.xml";
    }
    
    @Override
    public String getFactoryId() {
        return ShipJpaEntityProviderFactory.ID;
    }
    
    @Override
    public void close() {
        // Nothing to close
    }
}