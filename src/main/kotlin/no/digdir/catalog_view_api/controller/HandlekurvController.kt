package no.digdir.catalog_view_api.controller

import no.digdir.catalog_view_api.Model.Handlekurv
import no.digdir.catalog_view_api.client.FelleskatalogClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(value = ["/handlekurv"], produces = ["application/json"])
class AccessApplication(
    val felleskatalogClient: FelleskatalogClient,
) {
    @GetMapping("/{type}/{id}")
    fun getHandlekurv(
        @PathVariable type: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Handlekurv> {
        val metadata =
            felleskatalogClient.getMetadata(type, id)
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(metadata.toHandlekurv(type, id))
    }

    @PostMapping("/explanation")
    fun dataDefToExplanation(
        @RequestBody dataDef: Handlekurv.DataDef,
    ): ResponseEntity<String> {
        val metadata =
            felleskatalogClient.getMetadata(dataDef.resourceType.toPathName(), UUID.fromString(dataDef.katalogId))
                ?: return ResponseEntity.notFound().build()

        val markdown =
            """
            # ${metadata.title.nb ?: metadata.title.en ?: ""}
            
            ${metadata.description?.nb ?: metadata.description?.en ?: ""}
            """.trimIndent()

        return ResponseEntity.ok(markdown)
    }
}
