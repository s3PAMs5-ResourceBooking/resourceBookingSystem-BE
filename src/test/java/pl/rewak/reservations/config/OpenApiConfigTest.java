package pl.rewak.reservations.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void definesKeycloakAuthorizationCodeFlow() {
        OpenAPI openApi = new OpenApiConfig()
                .reservationsOpenApi("http://localhost:8180/realms/reservations");

        SecurityScheme scheme = openApi.getComponents().getSecuritySchemes().get(OpenApiConfig.SECURITY_SCHEME);
        OAuthFlow flow = scheme.getFlows().getAuthorizationCode();

        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.OAUTH2);
        assertThat(flow.getAuthorizationUrl())
                .isEqualTo("http://localhost:8180/realms/reservations/protocol/openid-connect/auth");
        assertThat(flow.getTokenUrl())
                .isEqualTo("http://localhost:8180/realms/reservations/protocol/openid-connect/token");
        assertThat(flow.getScopes()).containsKeys("openid", "profile", "email");
        assertThat(openApi.getSecurity()).anySatisfy(req -> assertThat(req).containsKey(OpenApiConfig.SECURITY_SCHEME));
    }
}
