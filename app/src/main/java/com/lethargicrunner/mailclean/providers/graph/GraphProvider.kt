package com.lethargicrunner.mailclean.providers.graph

import com.lethargicrunner.mailclean.core.MailAccount
import com.lethargicrunner.mailclean.core.MailFolder
import com.lethargicrunner.mailclean.core.MailMessage
import com.lethargicrunner.mailclean.core.MailProvider
import com.lethargicrunner.mailclean.http.HttpJsonClient

/**
 * Adaptateur Outlook via Microsoft Graph (choix : APIs natives).
 * TODO (Mat) : enregistrement d'appli Azure avec scopes Mail.ReadWrite + offline_access
 * (redirect mailclean://oauth/graph) -> renseigner CLIENT_ID ci-dessous.
 */
class GraphProvider(
    override val account: MailAccount,
    private val accessToken: String,
) : MailProvider {

    private val api = "https://graph.microsoft.com/v1.0/me"

    override suspend fun listFolders(): List<MailFolder> {
        val res = HttpJsonClient.get("$api/mailFolders", accessToken)
        require(res.code == 200) { "Graph mailFolders HTTP ${" + "res.code}" }
        // TODO V0.2 : parsing JSON (org.json) + pagination @odata.nextLink.
        return emptyList()
    }

    override suspend fun listRecentMessages(folderId: String?, limit: Int): List<MailMessage> {
        // TODO V0.2 : GET /me/messages (+ folderId), en-têtes internetMessageHeader.
        return emptyList()
    }

    override suspend fun createFolder(name: String): MailFolder {
        val res = HttpJsonClient.post("$api/mailFolders", """{"displayName":"$name"}""", accessToken)
        require(res.code == 201) { "Graph createFolder HTTP ${" + "res.code}" }
        // TODO V0.2 : parsing de la réponse.
        return MailFolder(id = "", name = name)
    }

    override suspend fun moveMessage(messageId: String, toFolderId: String) {
        val res = HttpJsonClient.post("$api/messages/$messageId/move", """{"destinationId":"$toFolderId"}""", accessToken)
        require(res.code == 201) { "Graph move HTTP ${" + "res.code}" }
    }

    override suspend fun deleteOrArchiveMessage(messageId: String) {
        val res = HttpJsonClient.post("$api/messages/$messageId/move", """{"destinationId":"archive"}""", accessToken)
        require(res.code == 201) { "Graph archive HTTP ${" + "res.code}" }
    }

    companion object {
        const val CLIENT_ID = "TODO_INSERER_CLIENT_ID_AZURE"
        const val OAUTH_REDIRECT = "mailclean://oauth/graph"
        const val SCOPES = "Mail.ReadWrite offline_access"
    }
}
