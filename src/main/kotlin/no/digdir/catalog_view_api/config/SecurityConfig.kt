package no.digdir.catalog_view_api.config

import com.nimbusds.jose.jwk.RSAKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtIssuerValidator
import org.springframework.security.oauth2.jwt.JwtTimestampValidator
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
open class SecurityConfig(
    @Value("classpath:MaskinportenKey.pem")
    val privateKey: RSAPrivateKey,
    @Value("classpath:MaskinportenPublicKey.pub")
    val publicKey: RSAPublicKey,
) {
    @Bean
    open fun filterChain(
        http: HttpSecurity,
        applicationProperties: ApplicationProperties,
    ): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize(HttpMethod.GET, "/**", permitAll)
            }
            oauth2ResourceServer { jwt { } }
        }
        return http.build()
    }

    @Bean
    open fun jwtDecoder(properties: OAuth2ResourceServerProperties): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withJwkSetUri(properties.jwt.jwkSetUri).build()
        jwtDecoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtTimestampValidator(),
                JwtIssuerValidator(properties.jwt.issuerUri),
            ),
        )
        return jwtDecoder
    }

    @Bean
    open fun webClientReactiveAuthorizationCodeTokenResponseClient(): WebClientReactiveAuthorizationCodeTokenResponseClient {
        val tokenResponseClient = WebClientReactiveAuthorizationCodeTokenResponseClient()

        tokenResponseClient.addParametersConverter(
            NimbusJwtClientAuthenticationParametersConverter { clientRegistration: ClientRegistration ->
                if (clientRegistration.clientAuthenticationMethod.equals(ClientAuthenticationMethod.PRIVATE_KEY_JWT)) {
                    RSAKey
                        .Builder(publicKey)
                        .privateKey(privateKey)
                        .keyID("Ez5Guxd5I6")
                        .build()
                }
                null
            },
        )

        return tokenResponseClient
    }
}
