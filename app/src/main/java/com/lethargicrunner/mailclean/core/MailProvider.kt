package com.lethargicrunner.mailclean.core

/**
 * Interface commune à tous les fournisseurs (Gmail API, Microsoft Graph, Yahoo IMAP plus tard).
 * Un adaptateur par fournisseur, comme CloudClean.
 */
interface MailProvider {
    val account: MailAccount

    suspend fun listFolders(): List<MailFolder>

    suspend fun listRecentMessages(folderId: String? = null, limit: Int = 50): List<MailMessage>

    suspend fun createFolder(name: String): MailFolder

    suspend fun moveMessage(messageId: String, toFolderId: String)

    suspend fun deleteOrArchiveMessage(messageId: String)
}
