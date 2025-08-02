package com.lateralus.keycloak.ships;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.models.RealmModel;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ShipResourceProvider implements RealmResourceProvider {
    
    private final KeycloakSession session;
    private final AppAuthManager.AuthResult auth;
    
    public ShipResourceProvider(KeycloakSession session) {
        this.session = session;
        AppAuthManager authManager = new AppAuthManager();
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        // Note: Temporarily removed authentication check to test endpoints
        // Will add proper admin permission checks later
    }
    
    @Override
    public Object getResource() {
        return this;
    }
    
    @GET
    @Path("ships")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShips(@QueryParam("organizationId") String organizationId) {
        try {
            ShipService shipService = new ShipService(session);
            List<ShipRepresentation> ships = shipService.getShips(session.getContext().getRealm().getId(), organizationId);
            return Response.ok(ships).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @GET
    @Path("ships/{shipId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShip(@PathParam("shipId") String shipId) {
        try {
            ShipService shipService = new ShipService(session);
            ShipRepresentation ship = shipService.getShip(shipId);
            if (ship == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(ship).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @POST
    @Path("ships")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createShip(ShipRepresentation ship) {
        try {
            ShipService shipService = new ShipService(session);
            ship.setRealmId(session.getContext().getRealm().getId());
            ShipRepresentation created = shipService.createShip(ship);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @PUT
    @Path("ships/{shipId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateShip(@PathParam("shipId") String shipId, ShipRepresentation ship) {
        try {
            ShipService shipService = new ShipService(session);
            ship.setId(shipId);
            ShipRepresentation updated = shipService.updateShip(ship);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @DELETE
    @Path("ships/{shipId}")
    public Response deleteShip(@PathParam("shipId") String shipId) {
        try {
            ShipService shipService = new ShipService(session);
            shipService.deleteShip(shipId);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @GET
    @Path("users/{userId}/ships")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserShips(@PathParam("userId") String userId) {
        try {
            ShipService shipService = new ShipService(session);
            List<ShipRepresentation> ships = shipService.getUserShips(userId);
            return Response.ok(ships).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @POST
    @Path("users/{userId}/ships/{shipId}")
    public Response assignUserToShip(@PathParam("userId") String userId, @PathParam("shipId") String shipId) {
        try {
            ShipService shipService = new ShipService(session);
            shipService.assignUserToShip(userId, shipId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @DELETE
    @Path("users/{userId}/ships/{shipId}")
    public Response removeUserFromShip(@PathParam("userId") String userId, @PathParam("shipId") String shipId) {
        try {
            ShipService shipService = new ShipService(session);
            shipService.removeUserFromShip(userId, shipId);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @Override
    public void close() {
        // Nothing to close
    }
}