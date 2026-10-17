package pl.rewak.reservations.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Specyfikacja OpenAPI z logowaniem przez Keycloak (Authorization Code + PKCE, klient {@code swagger-ui}).
 */
@Configuration
public class OpenApiConfig {

    static final String SECURITY_SCHEME = "keycloak";

    @Bean
    OpenAPI reservationsOpenApi(@Value("${app.keycloak.issuer-uri}") String issuerUri) {
        String oidc = issuerUri + "/protocol/openid-connect";
        OAuthFlow authorizationCode = new OAuthFlow()
                .authorizationUrl(oidc + "/auth")
                .tokenUrl(oidc + "/token")
                .refreshUrl(oidc + "/token")
                .scopes(new Scopes()
                        .addString("openid", "OpenID Connect")
                        .addString("profile", "Profil użytkownika")
                        .addString("email", "Adres e-mail"));

        return new OpenAPI()
                .info(new Info()
                        .title("Resource Booking System API")
                        .description("REST API systemu rezerwacji zasobów")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(new OAuthFlows().authorizationCode(authorizationCode))))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
    }
}
