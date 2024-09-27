package no.digdir.catalog_view_api.Model

import java.util.UUID

data class DatasetMetadata(
    val contactPoint: List<ContactPoint>,
    val title: Title,
    val publisher: Publisher,
    val admsIdentifier: String?,
    val accessRights: AccessRights?,
    val description: Description?,
) {
    data class ContactPoint(
        val email: String,
    )

    data class Title(
        val nb: String?,
        val en: String?,
    )

    data class Description(
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

    fun toHandlekurv(
        type: String,
        uuid: UUID,
    ): Handlekurv =
        Handlekurv(
            orgnr = publisher.id,
            dataDef =
                Handlekurv.DataDef(
                    eksternId = admsIdentifier,
                    katalogId = uuid.toString(),
                    orgnr = publisher.id,
                    navn = title.nb ?: title.en ?: "",
                    resourceType = toResourceType(type),
                ),
            hintIsPublic = accessRights?.code == AccessRight.PUBLIC,
        )
}

enum class AccessRight {
    PUBLIC,
    RESTRICTED,
    NON_PUBLIC,
}
