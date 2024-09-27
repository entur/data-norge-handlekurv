package no.digdir.catalog_view_api.Model

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
        val resourceType: ResourceType,
    )
}

enum class ResourceType {
    DATASET,
    DATA_SERVICE,
    ;

    fun toPathName(): String =
        when (this) {
            DATASET -> "datasets"
            DATA_SERVICE -> "data-services"
        }
}

fun toResourceType(pathName: String): ResourceType =
    when (pathName) {
        "datasets" -> ResourceType.DATASET
        "data-services" -> ResourceType.DATA_SERVICE
        else -> throw IllegalArgumentException("Unknown resource type: $pathName")
    }
