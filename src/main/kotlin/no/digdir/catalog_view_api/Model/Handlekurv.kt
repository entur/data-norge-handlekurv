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
    )
}
