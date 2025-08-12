package com.lateralus.keycloak.ships;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.ClientPolicyExecutorConfigurationRepresentation;
import org.keycloak.services.clientpolicy.ClientPolicyContext;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.executor.ClientPolicyExecutorProvider;
import org.keycloak.services.clientpolicy.context.ClientCRUDContext;
import org.keycloak.services.clientpolicy.context.AuthorizationRequestContext;
import org.keycloak.services.ErrorResponseException;
import org.keycloak.protocol.oidc.utils.RedirectUtils;
import org.jboss.logging.Logger;

import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.lang.reflect.Method;

/**
 * Client Policy Executor that validates redirect URIs against regex patterns.
 * This allows for flexible redirect URI validation for dynamic environments
 * like preview deployments while maintaining security.
 */
public class RegexRedirectUriExecutor implements ClientPolicyExecutorProvider<RegexRedirectUriExecutor.Configuration> {

    private static final Logger logger = Logger.getLogger(RegexRedirectUriExecutor.class);
    
    private final KeycloakSession session;
    private Configuration configuration;

    public RegexRedirectUriExecutor(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public String getProviderId() {
        return RegexRedirectUriExecutorFactory.PROVIDER_ID;
    }

    @Override
    public String getName() {
        return "Regex Redirect URI Validator";
    }

    @Override
    public void executeOnEvent(ClientPolicyContext context) throws ClientPolicyException {
        switch (context.getEvent()) {
            case REGISTER:
            case UPDATE:
                executeOnClientCRUD(context);
                break;
            case AUTHORIZATION_REQUEST:
                executeOnAuthorizationRequest(context);
                break;
            default:
                // No action needed for other events
                break;
        }
    }

    private void executeOnClientCRUD(ClientPolicyContext context) throws ClientPolicyException {
        ClientCRUDContext clientContext = (ClientCRUDContext) context;
        ClientModel client = clientContext.getTargetClient();
        
        if (client == null) {
            return;
        }

        validateClientRedirectUris(client);
    }

    private void executeOnAuthorizationRequest(ClientPolicyContext context) throws ClientPolicyException {
        AuthorizationRequestContext authContext = (AuthorizationRequestContext) context;
        
        // Get the client from the context - method name might vary
        ClientModel client = null;
        try {
            // Try common methods to get the client
            if (authContext.getClass().getMethod("getClient") != null) {
                client = (ClientModel) authContext.getClass().getMethod("getClient").invoke(authContext);
            }
        } catch (Exception e) {
            logger.debugf("Could not get client via getClient() method: %s", e.getMessage());
        }
        
        if (client == null) {
            logger.debug("Client is null in authorization request context, skipping validation");
            return;
        }

        // Get redirect_uri from request parameters
        String redirectUri = null;
        try {
            // Try to get request parameters
            Object requestParams = authContext.getClass().getMethod("getRequestParameters").invoke(authContext);
            if (requestParams instanceof jakarta.ws.rs.core.MultivaluedMap) {
                @SuppressWarnings("unchecked")
                jakarta.ws.rs.core.MultivaluedMap<String, String> params = 
                    (jakarta.ws.rs.core.MultivaluedMap<String, String>) requestParams;
                redirectUri = params.getFirst("redirect_uri");
            }
        } catch (Exception e) {
            logger.debugf("Could not get redirect_uri from request parameters: %s", e.getMessage());
        }
        
        if (redirectUri != null && !redirectUri.isEmpty()) {
            logger.debugf("Validating redirect URI from authorization request: %s", redirectUri);
            validateRedirectUri(client, redirectUri);
        } else {
            logger.debug("No redirect_uri found in authorization request");
        }
    }

    private void validateClientRedirectUris(ClientModel client) throws ClientPolicyException {
        Set<String> redirectUris = client.getRedirectUris();
        
        for (String redirectUri : redirectUris) {
            validateRedirectUri(client, redirectUri);
        }
    }

    private void validateRedirectUri(ClientModel client, String redirectUri) throws ClientPolicyException {
        if (configuration == null || configuration.getAllowedPatterns() == null || configuration.getAllowedPatterns().isEmpty()) {
            logger.debugf("No regex patterns configured for client policy executor, allowing redirect URI: %s", redirectUri);
            return;
        }

        List<String> allowedPatterns = configuration.getAllowedPatterns();

        // Convert pattern strings to compiled regex patterns
        List<Pattern> patterns = allowedPatterns.stream()
            .map(patternStr -> {
                try {
                    return Pattern.compile(patternStr);
                } catch (PatternSyntaxException e) {
                    logger.warnf("Invalid regex pattern '%s': %s", patternStr, e.getMessage());
                    return null;
                }
            })
            .filter(pattern -> pattern != null)
            .collect(Collectors.toList());

        // Check if redirect URI matches any allowed pattern
        boolean matches = patterns.stream()
            .anyMatch(pattern -> pattern.matcher(redirectUri).matches());

        if (!matches) {
            logger.warnf("Redirect URI '%s' for client '%s' does not match any allowed regex patterns: %s", 
                redirectUri, client.getClientId(), allowedPatterns);
            
            String errorMessage = String.format(
                "Redirect URI '%s' is not allowed. Must match one of the configured patterns.", 
                redirectUri
            );
            
            throw new ClientPolicyException(errorMessage, "invalid_redirect_uri");
        }

        logger.debugf("Redirect URI '%s' for client '%s' matches allowed patterns", redirectUri, client.getClientId());
    }

    @Override
    public void setupConfiguration(Configuration config) {
        this.configuration = config;
    }

    @Override
    public Class<Configuration> getExecutorConfigurationClass() {
        return Configuration.class;
    }

    /**
     * Configuration class for the Regex Redirect URI Executor
     */
    public static class Configuration extends ClientPolicyExecutorConfigurationRepresentation {
        
        @JsonProperty("allowedPatterns")
        private List<String> allowedPatterns;
        
        @JsonProperty("strictMode")
        private Boolean strictMode = false;

        public List<String> getAllowedPatterns() {
            return allowedPatterns;
        }

        public void setAllowedPatterns(List<String> allowedPatterns) {
            this.allowedPatterns = allowedPatterns;
        }

        public Boolean getStrictMode() {
            return strictMode != null ? strictMode : false;
        }

        public void setStrictMode(Boolean strictMode) {
            this.strictMode = strictMode;
        }
    }

    @Override
    public void close() {
        // No cleanup needed
    }
}