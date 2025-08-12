package com.lateralus.keycloak.ships;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.clientpolicy.executor.ClientPolicyExecutorProvider;
import org.keycloak.services.clientpolicy.executor.ClientPolicyExecutorProviderFactory;
import org.keycloak.Config.Scope;

import java.util.List;

/**
 * Factory for creating RegexRedirectUriExecutor instances.
 * This factory is responsible for creating and managing the lifecycle
 * of the regex redirect URI validator executor.
 */
public class RegexRedirectUriExecutorFactory implements ClientPolicyExecutorProviderFactory {

    public static final String PROVIDER_ID = "regex-redirect-uri";

    @Override
    public ClientPolicyExecutorProvider create(KeycloakSession session) {
        return new RegexRedirectUriExecutor(session);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Validates client redirect URIs against configurable regex patterns for dynamic environments";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of(
            new ProviderConfigProperty(
                "allowedPatterns",
                "Allowed Redirect URI Patterns",
                "List of regex patterns that redirect URIs must match. One pattern per line.",
                ProviderConfigProperty.MULTIVALUED_STRING_TYPE,
                ""
            ),
            new ProviderConfigProperty(
                "strictMode",
                "Strict Mode", 
                "When enabled, requires all redirect URIs to match patterns. When disabled, only validates URIs that don't match Keycloak's default validation.",
                ProviderConfigProperty.BOOLEAN_TYPE,
                "false"
            )
        );
    }

    @Override
    public void init(Scope config) {
        // No initialization needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-initialization needed  
    }

    @Override
    public void close() {
        // No cleanup needed
    }
}