package grab.bit.downloader.video

data class YtDlpDownloadRequest(
    val url: String,
    val formatId: String?,
    val subtitleLangs: List<String>,
    val outputTemplate: String,
    val extraArgs: String = "",
)

// Pure yt-dlp CLI option builder (Seal recipe). Pairs of option to optional
// value; the android runner only executes.
object YtDlpRequestBuilder {
    fun buildOptions(req: YtDlpDownloadRequest): List<Pair<String, String?>> {
        return buildList {
            req.formatId?.let {
                add("-f" to it)
            }
            if (req.subtitleLangs.isNotEmpty()) {
                add("--write-subs" to null)
                add("--embed-subs" to null)
                add("--sub-langs" to req.subtitleLangs.joinToString(","))
            }
            add("--no-playlist" to null)
            add("-o" to req.outputTemplate)
            addAll(parseExtraArgs(req.extraArgs))
        }
    }

    // Splits raw user input (e.g. "--extractor-args youtube:player_client=android")
    // into option pairs. Blank input adds nothing. Tokens starting with '-'
    // are options; a following non-option token becomes its value. Quoted
    // tokens are always values, so values starting with '-' must be quoted.
    // ponytail: naive whitespace split with quote support, no shell escapes;
    // a full parser if users need nested quoting.
    fun parseExtraArgs(raw: String): List<Pair<String, String?>> {
        val tokens = splitArgs(raw)
        if (tokens.isEmpty()) return emptyList()
        return buildList {
            var i = 0
            while (i < tokens.size) {
                val (token, quoted) = tokens[i]
                if (quoted || !token.startsWith("-") || token == "-") {
                    i++
                    continue
                }
                val next = tokens.getOrNull(i + 1)
                if (next != null && (next.second || !next.first.startsWith("-"))) {
                    add(token to next.first)
                    i += 2
                } else {
                    add(token to null)
                    i++
                }
            }
        }
    }

    private fun splitArgs(raw: String): List<Pair<String, Boolean>> {
        val out = mutableListOf<Pair<String, Boolean>>()
        val current = StringBuilder()
        var quote: Char? = null
        var hasToken = false
        var tokenQuoted = false
        fun flush() {
            if (hasToken) {
                out.add(current.toString() to tokenQuoted)
                current.clear()
                hasToken = false
                tokenQuoted = false
            }
        }
        for (c in raw) {
            when {
                quote != null && c == quote -> quote = null
                quote != null -> {
                    current.append(c)
                    hasToken = true
                }

                c == '"' || c == '\'' -> {
                    quote = c
                    hasToken = true
                    tokenQuoted = true
                }

                c.isWhitespace() -> flush()

                else -> {
                    current.append(c)
                    hasToken = true
                }
            }
        }
        flush()
        return out
    }
}
