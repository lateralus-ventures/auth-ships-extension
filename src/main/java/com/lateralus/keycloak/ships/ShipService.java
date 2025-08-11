package com.lateralus.keycloak.ships;

import com.lateralus.keycloak.ships.jpa.ShipEntity;
import com.lateralus.keycloak.ships.jpa.UserShipEntity;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;

public class ShipService {
    
    private final KeycloakSession session;
    private final EntityManager em;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public ShipService(KeycloakSession session) {
        this.session = session;
        this.em = session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }
    
    public List<ShipRepresentation> getShips(String realmId, String organizationId) {
        TypedQuery<ShipEntity> query;
        if (organizationId != null) {
            query = em.createNamedQuery("findShipsByOrganization", ShipEntity.class);
            query.setParameter("realmId", realmId);
            query.setParameter("organizationId", organizationId);
        } else {
            query = em.createNamedQuery("findShipsByRealm", ShipEntity.class);
            query.setParameter("realmId", realmId);
        }
        
        return query.getResultList().stream()
            .map(this::toRepresentation)
            .collect(Collectors.toList());
    }
    
    public ShipRepresentation getShip(String shipId) {
        ShipEntity entity = em.find(ShipEntity.class, shipId);
        return entity != null ? toRepresentation(entity) : null;
    }
    
    public ShipRepresentation createShip(ShipRepresentation ship) {
        ShipEntity entity = new ShipEntity();
        entity.setId(UUID.randomUUID().toString());
        updateEntityFromRepresentation(entity, ship);
        
        em.persist(entity);
        em.flush();
        
        return toRepresentation(entity);
    }
    
    public ShipRepresentation updateShip(ShipRepresentation ship) {
        ShipEntity entity = em.find(ShipEntity.class, ship.getId());
        if (entity == null) {
            throw new IllegalArgumentException("Ship not found: " + ship.getId());
        }
        
        updateEntityFromRepresentation(entity, ship);
        em.merge(entity);
        em.flush();
        
        return toRepresentation(entity);
    }
    
    public void deleteShip(String shipId) {
        // First delete all user-ship relationships
        em.createNamedQuery("deleteUserShip")
            .setParameter("shipId", shipId)
            .executeUpdate();
            
        // Then delete the ship
        em.createNamedQuery("deleteShipById")
            .setParameter("id", shipId)
            .executeUpdate();
    }
    
    public List<ShipRepresentation> getUserShips(String userId) {
        TypedQuery<UserShipEntity> query = em.createNamedQuery("findUserShips", UserShipEntity.class);
        query.setParameter("userId", userId);
        
        List<String> shipIds = query.getResultList().stream()
            .map(UserShipEntity::getShipId)
            .collect(Collectors.toList());
            
        if (shipIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        return shipIds.stream()
            .map(this::getShip)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    public void assignUserToShip(String userId, String shipId) {
        // Check if assignment already exists
        List<UserShipEntity> existing = em.createNamedQuery("findUserShips", UserShipEntity.class)
            .setParameter("userId", userId)
            .getResultList();
            
        boolean alreadyAssigned = existing.stream()
            .anyMatch(us -> us.getShipId().equals(shipId));
            
        if (!alreadyAssigned) {
            UserShipEntity userShip = new UserShipEntity();
            userShip.setUserId(userId);
            userShip.setShipId(shipId);
            em.persist(userShip);
            em.flush();
        }
    }
    
    public void removeUserFromShip(String userId, String shipId) {
        em.createNamedQuery("deleteUserShip")
            .setParameter("userId", userId)
            .setParameter("shipId", shipId)
            .executeUpdate();
    }
    
    private ShipRepresentation toRepresentation(ShipEntity entity) {
        ShipRepresentation rep = new ShipRepresentation();
        rep.setId(entity.getId());
        rep.setName(entity.getName());
        rep.setDescription(entity.getDescription());
        rep.setImo(entity.getImo());
        rep.setType(entity.getType());
        rep.setOrganizationId(entity.getOrganizationId());
        rep.setRealmId(entity.getRealmId());
        rep.setExternalId(entity.getExternalId());
        rep.setCrewAiMode(entity.getCrewAiMode());
        rep.setCreatedAt(entity.getCreatedAt());
        rep.setUpdatedAt(entity.getUpdatedAt());
        
        // Parse JSON equipment
        if (entity.getShipEquipment() != null) {
            try {
                Map<String, Object> equipment = objectMapper.readValue(entity.getShipEquipment(), Map.class);
                rep.setShipEquipment(equipment);
            } catch (Exception e) {
                // Log error or handle as needed
            }
        }
        
        return rep;
    }
    
    private void updateEntityFromRepresentation(ShipEntity entity, ShipRepresentation rep) {
        entity.setName(rep.getName());
        entity.setDescription(rep.getDescription());
        entity.setImo(rep.getImo());
        entity.setType(rep.getType());
        entity.setOrganizationId(rep.getOrganizationId());
        entity.setRealmId(rep.getRealmId());
        entity.setExternalId(rep.getExternalId());
        entity.setCrewAiMode(rep.getCrewAiMode());
        
        // Convert equipment to JSON
        if (rep.getShipEquipment() != null) {
            try {
                String equipmentJson = objectMapper.writeValueAsString(rep.getShipEquipment());
                entity.setShipEquipment(equipmentJson);
            } catch (Exception e) {
                // Log error or handle as needed
            }
        }
    }
}