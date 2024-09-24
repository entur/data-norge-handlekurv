package no.digdir.catalog_view_api.controller

import no.digdir.catalog_view_api.client.GetTokenCilent
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(value = ["/access-application"], produces = ["application/json"])
class AccessApplication(
    val getTokenCilent: GetTokenCilent,
) {
    @GetMapping("/{id}")
    fun test(
        @PathVariable id: UUID,
    ): ResponseEntity<String> {
        // Få maskinporten token med scope altinn:serviceowner/instances.write

        // Byttes inn til Altinn token

        // opprette en ny instans i altinn. POST {basePath}/create
        // Base path: {org}/{appname}/instances
        // digdir/tilgangssoknad/instances

        // På altinnsiden må vi opprette en instans som peker

        getTokenCilent.test()

        return ResponseEntity.ok(id.toString())
    }
}
