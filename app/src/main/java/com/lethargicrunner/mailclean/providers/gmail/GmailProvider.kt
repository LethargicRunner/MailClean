package com.lethargicrunner.mailclean.providers.gmail

import com.lethargicrunner.mailclean.core.MailFolder
import com.lethargicrunner.mailclean.core.MailMessage
import com.lethargicrunner.mailclean.core.MailProvider
import com.lethargicrunner.mailclean.core.MailAccount
import com.lethargicrunner.mailclean.http.HttpJsonClient
import org.json.JSONObject

/**
 * Adaptateur Gmail via l'API Gmail v1 (choix : APIs natives).
 * TODO (Mat) : projet Google Cloud -> activer Gmail API -> écran de consentement External/Testing
 * -> client OAuth Web avec redirect mailclean://oauth/gmail -> renseigner CLIENT_ID ci-dessous.
 * ⚠️ En mode Testing, les refresh tokens Google expirent après 7 jours.
 */
class GmailProvider(
    override val account: MailAccount,
    private val accessToken: String,
) : MailProvider {

    private val api = "https://gmail.googleapis.com/gmail/v1/users/me"

    override suspend fun listFolders(): List<MailFolder> {
        val res = HttpJsonClient.get("$api/labels", accessToken)
        require(res.code == 200) { "Gmail labels HTTP ${" + "res.code}" }
        val labels = JSONObject(res.body).getJSONArray("labels")
        return (0 until labels.length()).map { i ->
            val l = labels.getJSONObject(i)
            MailFolder(id = l.getString("id"), name = l.getString("name"))
        }
    }

    override suspend fun listRecentMessages(folderId: String?, limit: Int): List<MailMessage> {
        val q = if (folderId != null) "labelId=$folderId" else "maxResults=$limit"
        val list = HttpJsonClient.get("$api/messages?$q", accessToken)
        require(list.code == 200) { "Gmail messages HTTP ${" + "list.code}" }
        // TODO V0.2 : récupération des en-têtes (List-Unsubscribe) par message (format=metadata).
        return emptyList()
    }

    override suspend fun createFolder(name: String): MailFolder {
        val res = HttpJsonClient.post("$api/labels", """{"name":"$name","labelListVisibility":"labelShow","messageListVisibility":"show"}""", accessToken)
        require(res.code == 200) { "Gmail createLabel HTTP ${" + "res.code}" }
        val l = JSONObject(res.body)
        return MailFolder(id = l.getString("id"), name = l.getString("name"))
    }

    override suspend fun moveMessage(messageId: String, toFolderId: String) {
        val res = HttpJsonClient.post("$api/messages/$messageId/modify", """{"addLabelIds":["$toFolderId"]}""", accessToken)
        require(res.code == 200) { "Gmail modify HTTP ${" + "res.code}" }
    }

    override suspend fun deleteOrArchiveMessage(messageId: String) {
        // Archive = retirer INBOX.
        val res = HttpJsonClient.post("$api/messages/$messageId/modify", """{"removeLabelIds":["INBOX"]}""", accessToken)
        require(res.code == 200) { "Gmail archive HTTP ${" + "res.code}" }
    }

    companion object {
        const val CLIENT_ID = "TODO_INSERER_CLIENT_ID_GOOGLE"
        const val OAUTH_REDIRECT = "mailclean://oauth/gmail"
    }
}
