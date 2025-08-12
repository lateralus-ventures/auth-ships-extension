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
        
        logger.debug("Executing regex redirect URI validation on authorization request");
        
        // Get redirect_uri directly from the context
        String redirectUri = authContext.getRedirectUri();
        
        if (redirectUri == null || redirectUri.isEmpty()) {
            logger.debug("No redirect_uri found in authorization request, skipping validation");
            return;
        }
        
        logger.infof("Validating redirect URI from authorization request: %s", redirectUri);
        
        // Create a temporary client to pass to validateRedirectUri
        // We use the client ID from the context to identify which client this is for
        ClientModel client = session.getContext().getClient();
        if (client == null) {
            logger.warn("No client found in session context during authorization request");
            return;
        }
        
        logger.infof("Validating redirect URI for client '%s': %s", client.getClientId(), redirectUri);
        validateRedirectUri(client, redirectUri);
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