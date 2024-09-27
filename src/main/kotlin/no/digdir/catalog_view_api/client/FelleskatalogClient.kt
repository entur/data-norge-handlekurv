package no.digdir.catalog_view_api.client

import no.digdir.catalog_view_api.controller.DatasetMetadata
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Component
class FelleskatalogClient {
    val webClient =
        WebClient.create("https://resource.api.fellesdatakatalog.digdir.no")

    fun getMetadata(
        type: String,
        id: UUID,
    ): DatasetMetadata? =
        webClient
            .get()
            .uri("/$type/$id")
            .retrieve()
            .bodyToMono(DatasetMetadata::class.java)
            .block()
}
