package no.digdir.catalog_view_api.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@RestController
@RequestMapping(value = ["/access-application"], produces = ["application/json"])
class AccessApplication {
    val webClient =
        WebClient.create("https://resource.api.fellesdatakatalog.digdir.no")

    @GetMapping("/{type}/{id}")
    fun test(
        @PathVariable id: UUID,
        @PathVariable type: String,
    ): ResponseEntity<Handlekurv> {
        val test =
            webClient
                .get()
                .uri("/$type/$id")
                .retrieve()
                .bodyToMono(Metadata::class.java)
                .block()

        return ResponseEntity.ok(test.toHandlekurv(id))
    }
}

enum class AccessRight {
    PUBLIC,
    RESTRICTED,
    NON_PUBLIC,
}

data class Metadata(
    val contactPoint: List<ContactPoint>,
    val title: Title,
    val publisher: Publisher,
    val admsIdentifier: String?,
    val accessRights: AccessRights?,
) {
    data class ContactPoint(
        val email: String,
    )

    data class Title(
        val nb: String?,
        val en: String?,
    )

    data class Publisher(
        // samme som orgnr
        val id: String,
    )

    data class AccessRights(
        val code: AccessRight,
    )

    fun toHandlekurv(uuid: UUID): Handlekurv =
        Handlekurv(
            orgnr = publisher.id,
            dataDef =
                Handlekurv.DataDef(
                    eksternId = admsIdentifier,
                    katalogId = uuid.toString(),
                    orgnr = publisher.id,
                    navn = title.nb ?: title.en ?: "",
                ),
            hintIsPublic = accessRights?.code == AccessRight.PUBLIC,
        )
}

data class Handlekurv(
    val orgnr: String,
    val dataDef: DataDef,
    val hintIsPublic: Boolean,
) {
    val system: String = "datanorge"

    data class DataDef(
        val eksternId: String?,
        val katalogId: String,
        val orgnr: String,
        val navn: String,
    )
}
