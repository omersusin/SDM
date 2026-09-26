package grab.bit.util

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

object HttpUrlUtils {
    fun createURL(url: String): HttpUrl {
        return url.toHttpUrl()
    }

    fun isValidUrl(link: String): Boolean {
        return runCatching {
            val url = createURL(link)
            // okhttp already restricts to http/https; be explicit: the engine
            // cannot fetch anything else (no ftp/file support), so refuse early.
            if (url.scheme != "http" && url.scheme != "https") {
                return false
            }
            // Refuse cloud metadata endpoints (169.254.0.0/16): SSRF via a
            // crafted link must not reach instance credentials. LAN ranges
            // (192.168/10/172.16) stay allowed for NAS use.
            !isLinkLocalMetadataHost(url.host)
        }.getOrDefault(false)
    }

    fun isLinkLocalMetadataHost(host: String): Boolean {
        val h = host.trimEnd('.').lowercase()
        if (h == "metadata.google.internal" || h.endsWith(".metadata.google.internal")) {
            return true
        }
        val parts = h.split(".")
        if (parts.size == 4) {
            val nums = parts.map { it.toIntOrNull() }
            if (nums.all { it != null }) {
                return nums[0] == 169 && nums[1] == 254
            }
        }
        return false
    }

    fun extractNameFromLink(link: String): String? {
        fun extractNameFromQuery(url: HttpUrl): String? {
            val fileNameQuery = url.queryParameterNames.firstOrNull {
                it.equals("filename", ignoreCase = true)
            } ?: return null
            return url.queryParameter(fileNameQuery)
                ?.takeIf { it.isNotBlank() }
        }

        fun extractNameFromLastPath(url: HttpUrl): String? {
            return url.pathSegments
                .lastOrNull { it.isNotBlank() }
                ?.let {
                    runCatching {
                        FilenameDecoder.decode(it, Charsets.UTF_8)
                    }.getOrNull()
                }
        }

        return runCatching {
            createURL(link)
        }.map { url ->
            extractNameFromQuery(url)
                ?: extractNameFromLastPath(url)
                ?: url.host.replace('.', '_')
        }.getOrNull()
    }

    fun getHost(url: String): String? {
        return kotlin.runCatching {
            createURL(url).host
        }.getOrNull()
    }

}
