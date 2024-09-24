package no.digdir.catalog_view_api.client

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.apache.hc.client5.http.fluent.Form
import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.client5.http.fluent.Response
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.message.BasicClassicHttpResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.security.KeyStore
import java.security.PrivateKey
import java.time.Clock
import java.util.Date
import java.util.UUID

@Service
class GetTokenCilent(
    @Value("classpath:certificate.p12")
    val ressurs: Resource,
) {
    fun test() {
        // Variable som kommer fra integrasjonen

        // Variable som kommer fra integrasjonen
        val keyId = "Ez5Guxd5I6" // Key id (kid)
        val integrasjonsid = "5c5e9335-731c-4061-b8af-848b4f17777a"
        val scope = "altinn:brokerservice.read"

        // Variable som avhengiger av miljø
        val maskinportenAudience = "https://test.maskinporten.no/"
        val maskinportenTokenUrl = "https://test.maskinporten.no/token"

        // Variable som avhenger av APIet du skal autentisere mot
        val targetApiAudience = "https://tt02.altinn.no/maskinporten-api/" // Sjekk API-tilbyder om de spesifiserer en verdi for denne

        // Variable som er tilpasset din keystore hvor du har lagt privatnøkkelen
        val keyStoreType = "PKCS12"
        val keystorepassword = "hallo"
        // val pathToKeystore = "/certificate.p12"
        val aliasToPrivatekey = "certificate"
        val aliasPassword = "hallo"

        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(ressurs.inputStream, keystorepassword.toCharArray())

        val jwtHeader =
            JWSHeader
                .Builder(JWSAlgorithm.RS256)
                .keyID(keyId)
                .build()

        val claims =
            JWTClaimsSet
                .Builder()
                .audience(maskinportenAudience)
                .issuer(integrasjonsid)
                .claim("scope", scope)
                .claim("resource", targetApiAudience)
                .jwtID(UUID.randomUUID().toString()) // Must be unique for each grant
                .issueTime(Date(Clock.systemUTC().millis())) // Use UTC time
                .expirationTime(Date(Clock.systemUTC().millis() + 120000)) // Expiration time is 120 sec
                .build()

        val privateKey = keyStore.getKey(aliasToPrivatekey, aliasPassword.toCharArray()) as PrivateKey
        val signer: JWSSigner = RSASSASigner(privateKey)
        val signedJWT = SignedJWT(jwtHeader, claims)
        signedJWT.sign(signer)

        val jwt = signedJWT.serialize()

        val body =
            Form
                .form()
                .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .add("assertion", jwt)
                .build()
        try {
            val response: Response =
                Request
                    .post(maskinportenTokenUrl)
                    .addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString())
                    .bodyForm(body)
                    .execute()

            val e: HttpEntity = (response.returnResponse() as BasicClassicHttpResponse).getEntity()
            val result = EntityUtils.toString(e)
            println(result)

            // Use access_token in result as authentication header to the service you wish to connect to
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
