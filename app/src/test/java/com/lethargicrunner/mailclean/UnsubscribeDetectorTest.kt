package com.lethargicrunner.mailclean

import com.lethargicrunner.mailclean.core.MailMessage
import com.lethargicrunner.mailclean.core.UnsubscribeDetector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UnsubscribeDetectorTest {

    private fun msg(subject: String = "", header: String? = null) = MailMessage(
        accountId = "gmail-1", id = "m1", folderId = null,
        from = "promo@shop.example", subject = subject,
        receivedAt = 0L, listUnsubscribe = header,
    )

    @Test
    fun enteteNulNeDonneAucuneSuggestion() {
        assertNull(UnsubscribeDetector.buildSuggestion(msg(header = null)))
        assertNull(UnsubscribeDetector.buildSuggestion(msg(header = "")))
    }

    @Test
    fun formatStandardAvecUrlEtMailto() {
        val s = UnsubscribeDetector.buildSuggestion(
            msg(header = "<https://shop.example/u/123>, <mailto:leave-123@shop.example>")
        )!!
        assertEquals("https://shop.example/u/123", s.httpUrl)
        assertEquals("mailto:leave-123@shop.example", s.mailto)
    }

    @Test
    fun mailtoSeul() {
        val s = UnsubscribeDetector.buildSuggestion(
            msg(header = "<mailto:unsus@shop.example?subject=bye>")
        )!!
        assertNull(s.httpUrl)
        assertEquals("mailto:unsus@shop.example?subject=bye", s.mailto)
    }

    @Test
    fun valeurBruteSansChevrons() {
        val info = UnsubscribeDetector.parseHeader("https://x.example/out")
        assertEquals("https://x.example/out", info.httpUrl)
        assertNull(info.mailto)
    }

    @Test
    fun heuristiqueSujetNewsletter() {
        assertTrue(UnsubscribeDetector.looksLikeMailingList(msg(subject = "Notre newsletter de novembre")))
        assertFalse(UnsubscribeDetector.looksLikeMailingList(msg(subject = "Facture novembre")))
    }
}
