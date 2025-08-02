package com.lateralus.keycloak.ships.jpa;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SHIP")
@NamedQueries({
    @NamedQuery(name = "findShipsByRealm", query = "SELECT s FROM ShipEntity s WHERE s.realmId = :realmId"),
    @NamedQuery(name = "findShipsByOrganization", query = "SELECT s FROM ShipEntity s WHERE s.realmId = :realmId AND s.organizationId = :organizationId"),
    @NamedQuery(name = "findShipById", query = "SELECT s FROM ShipEntity s WHERE s.id = :id"),
    @NamedQuery(name = "deleteShipById", query = "DELETE FROM ShipEntity s WHERE s.id = :id")
})
public class ShipEntity {
    
    @Id
    @Column(name = "ID", length = 36)
    private String id;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "IMO", length = 20)
    private String imo;
    
    @Column(name = "TYPE", length = 50)
    private String type;
    
    @Column(name = "ORGANIZATION_ID", length = 36)
    private String organizationId;
    
    @Column(name = "REALM_ID", length = 36, nullable = false)
    private String realmId;
    
    @Column(name = "SHIP_EQUIPMENT", columnDefinition = "TEXT")
    private String shipEquipment; // JSON
    
    @Column(name = "CREW_AI_MODE", length = 20)
    private String crewAiMode;
    
    @Column(name = "CREATED_AT")
    private Long createdAt;
    
    @Column(name = "UPDATED_AT")
    private Long updatedAt;
    
    @ManyToMany(mappedBy = "ships", fetch = FetchType.LAZY)
    private Set<UserShipEntity> users = new HashSet<>();
    
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
    
    public Set<UserShipEntity> getUsers() {
        return users;
    }
    
    public void setUsers(Set<UserShipEntity> users) {
        this.users = users;
    }
    
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