package dev.alinco8.xmxw

import com.google.gson.Gson
import dev.alinco8.xmxw.XMXWClient.LOGGER
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import net.minecraft.SharedConstants

internal object UpdateChecker {
    const val MODRINTH_ID = "iv2jCzkP"

    private val client: HttpClient = HttpClient.newHttpClient()
    private val gson: Gson = Gson()

    private fun fetchLatestVersion(): String? {
        val uri = URIBuilder()
            .url("https://api.modrinth.com/v2/project/$MODRINTH_ID/version")
            .query(
                "loaders", listOf(
                    //? if fabric {
                    /*"fabric",
                    *///? } else if neoforge {
                    "neoforge",
                    //? } else if forge {
                    /*"forge",
                    *///? }
                )
            )
            .query(
                "game_versions", listOf(
                    //? if <=1.21.5 {
                    /*SharedConstants.getCurrentVersion().name,
                    *///? } else {
                    SharedConstants.getCurrentVersion().name(),
                    //? }
                )
            )
            .build()
        val request = HttpRequest.newBuilder(uri)
            .GET()
            .build()

        try {
            val response =
                client.send(request, HttpResponse.BodyHandlers.ofString())
            val body = response.body()
            val json = gson.fromJson(body, Array::class.java)
            LOGGER.debug("Modrinth version check response: $body")

            return json.firstOrNull()?.let {
                gson.toJsonTree(it).asJsonObject.get("version_number").asString
            }
        } catch (e: Exception) {
            LOGGER.error("Could not fetch latest version from Modrinth", e)
            return null
        }
    }

    fun checkUpdate(currentVersion: String): String? {
        fetchLatestVersion()?.let { latestVersion ->
            if (currentVersion != latestVersion) {
                LOGGER.info("New version available: $latestVersion (current: $currentVersion)")
                return latestVersion
            }
        }

        LOGGER.debug("No new version available (current: $currentVersion)")

        return null
    }

    class URIBuilder {
        private var url: String? = null
        private val queries = mutableMapOf<String, Any?>()

        fun url(url: String): URIBuilder {
            this.url = url
            return this
        }

        fun query(key: String, value: Any?): URIBuilder {
            queries[key] = value
            return this
        }

        fun build(): URI {
            assert(url != null) { "URL must be set before building URI" }

            val queryString =
                queries.entries.joinToString("&", "?") {
                    "${it.key}=${
                        URLEncoder.encode(
                            gson.toJson(
                                it.value
                            ),
                            StandardCharsets.UTF_8
                        )
                    }"
                }

            return URI.create("${url!!}$queryString")
        }
    }
}
