package no.digdir.catalog_view_api.controller

import no.digdir.catalog_view_api.Model.Handlekurv
import no.digdir.catalog_view_api.client.FelleskatalogClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(value = ["/handlekurv"], produces = ["application/json"])
class AccessApplication(
    val felleskatalogClient: FelleskatalogClient,
) {
    @GetMapping("/{type}/{id}")
    fun test(
        @PathVariable type: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Handlekurv> {
        val metadata =
            felleskatalogClient.getMetadata(type, id)
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(metadata.toHandlekurv(id))
    }
}
