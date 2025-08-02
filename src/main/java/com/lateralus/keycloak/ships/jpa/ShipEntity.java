package com.lateralus.keycloak.ships.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "ship")
@NamedQueries({
    @NamedQuery(name = "findShipsByRealm", query = "SELECT s FROM ShipEntity s WHERE s.realmId = :realmId"),
    @NamedQuery(name = "findShipsByOrganization", query = "SELECT s FROM ShipEntity s WHERE s.realmId = :realmId AND s.organizationId = :organizationId"),
    @NamedQuery(name = "findShipById", query = "SELECT s FROM ShipEntity s WHERE s.id = :id"),
    @NamedQuery(name = "deleteShipById", query = "DELETE FROM ShipEntity s WHERE s.id = :id")
})
public class ShipEntity {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "imo", length = 20)
    private String imo;
    
    @Column(name = "type", length = 50)
    private String type;
    
    @Column(name = "organization_id", length = 36)
    private String organizationId;
    
    @Column(name = "realm_id", length = 36, nullable = false)
    private String realmId;
    
    @Column(name = "ship_equipment", columnDefinition = "TEXT")
    private String shipEquipment; // JSON
    
    @Column(name = "crew_ai_mode", length = 20)
    private String crewAiMode;
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    // Remove the incorrect ManyToMany relationship
    // UserShipEntity should be managed separately as it's a join table entity
    
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
    
    public String getShipEquipment() {
        return shipEquipment;
    }
    
    public void setShipEquipment(String shipEquipment) {
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
    
    // Removed getUsers/setUsers methods as we manage relationships through UserShipEntity directly
    
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = System.currentTimeMillis();
        }
        updatedAt = System.currentTimeMillis();
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}