package com.lethargicrunner.mailclean.core

/** Unité de base = le compte (même philosophie que CloudClean), jamais l'e-mail brut. */
data class MailAccount(
    val accountId: String,   // ex. "gmail-1234567890"
    val provider: Provider,
    val displayName: String,
) {
    enum class Provider { GMAIL, OUTLOOK, YAHOO }
}

data class MailFolder(
    val id: String,
    val name: String,
)

data class MailMessage(
    val accountId: String,
    val id: String,
    val folderId: String?,
    val from: String,
    val subject: String,
    val receivedAt: Long,
    val snippet: String = "",
    /** Valeur brute de l'en-tête List-Unsubscribe, si présente. */
    val listUnsubscribe: String? = null,
)

/**
 * Catégorie logique de l'app (créée par l'IA, nommée en français).
 * Vit en stockage local ; le dossier/label IMAP côté fournisseur n'est créé qu'à la validation.
 */
data class Category(
    val id: String,
    val name: String,
    /** accountId -> id du dossier/label créé sur ce compte (vide tant que non validé). */
    val folderIdByAccount: Map<String, String> = emptyMap(),
)

/** Suggestion d'action soumise à la validation de Mat (mode suggestions V0.1). */
sealed class Suggestion {
    data class Move(
        val messages: List<MailMessage>,
        val category: Category,
    ) : Suggestion()

    /** Irréversible côté expéditeur : toujours validée manuellement. */
    data class Unsubscribe(
        val sender: String,
        val httpUrl: String?,
        val mailto: String?,
    ) : Suggestion()
}

/** Entrée du journal d'actions (annulation possible pour les déplacements). */
data class ActionLogEntry(
    val id: String,
    val timestamp: Long,
    val type: String,          // "move" | "unsubscribe"
    val accountId: String,
    val messageId: String?,
    val detail: String,
    val reversible: Boolean,
)
