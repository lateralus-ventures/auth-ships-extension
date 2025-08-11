package com.lateralus.keycloak.ships;

import java.util.Map;

public class ShipRepresentation {
    private String id;
    private String name;
    private String description;
    private String imo;
    private String type;
    private String organizationId;
    private String realmId;
    private String externalId;
    private Map<String, Object> shipEquipment;
    private String crewAiMode;
    private Long createdAt;
    private Long updatedAt;
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImo() {
        return imo;
    }
    
    public void setImo(String imo) {
        this.imo = imo;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getRealmId() {
        return realmId;
    }
    
    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public Map<String, Object> getShipEquipment() {
        return shipEquipment;
    }
    
    public void setShipEquipment(Map<String, Object> shipEquipment) {
        this.shipEquipment = shipEquipment;
    }
    
    public String getCrewAiMode() {
        return crewAiMode;
    }
    
    public void setCrewAiMode(String crewAiMode) {
        this.crewAiMode = crewAiMode;
    }
    
    public Long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}