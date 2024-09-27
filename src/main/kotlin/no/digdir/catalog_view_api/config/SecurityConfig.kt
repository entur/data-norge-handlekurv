package no.digdir.catalog_view_api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
open class SecurityConfig {
    @Bean
    open fun filterChain(
        http: HttpSecurity,
        applicationProperties: ApplicationProperties,
    ): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize(HttpMethod.GET, "/**", permitAll)
                authorize(HttpMethod.POST, "/**", permitAll)
            }
            csrf {
                disable()
            }
        }
        return http.build()
    }
}
