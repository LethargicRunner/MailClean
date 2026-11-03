package com.lethargicrunner.mailclean.core

/**
 * Extraction des infos de désinscription d'un mail, à partir de l'en-tête
 * List-Unsubscribe (format RFC 8058 : "<https://...>, <mailto:...>").
 */
object UnsubscribeDetector {

    data class UnsubscribeInfo(val httpUrl: String?, val mailto: String?)

    fun parseHeader(headerValue: String?): UnsubscribeInfo {
        if (headerValue.isNullOrBlank()) return UnsubscribeInfo(null, null)
        var http: String? = null
        var mailto: String? = null
        // Les éléments sont entre chevrons, séparés par des virgules.
        Regex("<([^>]+)>").findAll(headerValue).forEach { m ->
            val v = m.groupValues[1].trim()
            when {
                v.startsWith("mailto:", ignoreCase = true) -> if (mailto == null) mailto = v
                v.startsWith("http://", ignoreCase = true) || v.startsWith("https://", ignoreCase = true) ->
                    if (http == null) http = v
            }
        }
        if (http == null && mailto == null) {
            // En-tête sans chevrons : valeur brute.
            val v = headerValue.trim()
            if (v.startsWith("mailto:", ignoreCase = true)) mailto = v
            else if (v.startsWith("http", ignoreCase = true)) http = v
        }
        return UnsubscribeInfo(http, mailto)
    }

    /** Heuristiques de détection d'une liste de diffusion (en complément de l'en-tête). */
    fun looksLikeMailingList(message: MailMessage): Boolean {
        val subject = message.subject.lowercase()
        return listOf("unsubscribe", "désinscription", "newsletter", "se désabonner").any { subject.contains(it) }
    }

    fun buildSuggestion(message: MailMessage): Suggestion.Unsubscribe? {
        val info = parseHeader(message.listUnsubscribe)
        if (info.httpUrl == null && info.mailto == null) return null
        return Suggestion.Unsubscribe(
            sender = message.from,
            httpUrl = info.httpUrl,
            mailto = info.mailto,
        )
    }
}
