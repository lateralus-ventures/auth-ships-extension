package com.lateralus.keycloak.ships.jpa;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_ship")
@IdClass(UserShipEntity.UserShipId.class)
@NamedQueries({
    @NamedQuery(name = "findUserShips", query = "SELECT us FROM UserShipEntity us WHERE us.userId = :userId"),
    @NamedQuery(name = "findShipUsers", query = "SELECT us FROM UserShipEntity us WHERE us.shipId = :shipId"),
    @NamedQuery(name = "deleteUserShip", query = "DELETE FROM UserShipEntity us WHERE us.userId = :userId AND us.shipId = :shipId")
})
public class UserShipEntity {
    
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;
    
    @Id
    @Column(name = "ship_id", length = 36)
    private String shipId;
    
    @Column(name = "assigned_at")
    private Long assignedAt;
    
    @ManyToMany
    @JoinTable(
        name = "user_ship",
        joinColumns = @JoinColumn(name = "ship_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<ShipEntity> ships = new HashSet<>();
    
    public static class UserShipId implements Serializable {
        private String userId;
        private String shipId;
        
        public UserShipId() {}
        
        public UserShipId(String userId, String shipId) {
            this.userId = userId;
            this.shipId = shipId;
        }
        
        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserShipId that = (UserShipId) o;
            return userId.equals(that.userId) && shipId.equals(that.shipId);
        }
        
        @Override
        public int hashCode() {
            return userId.hashCode() * 31 + shipId.hashCode();
        }
    }
    
    // Getters and setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getShipId() {
        return shipId;
    }
    
    public void setShipId(String shipId) {
        this.shipId = shipId;
    }
    
    public Long getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(Long assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public Set<ShipEntity> getShips() {
        return ships;
    }
    
    public void setShips(Set<ShipEntity> ships) {
        this.ships = ships;
    }
    
    @PrePersist
    public void prePersist() {
        if (assignedAt == null) {
            assignedAt = System.currentTimeMillis();
        }
    }
}