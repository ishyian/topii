package com.topiichat.app.core.util

import eu.siacs.conversations.entities.Account
import eu.siacs.conversations.services.XmppConnectionService
import eu.siacs.conversations.utils.CryptoHelper
import eu.siacs.conversations.xmpp.Jid
import timber.log.Timber
import java.security.SecureRandom

object AccountUtil {

    fun createMockAccount(xmppConnectionService: XmppConnectionService): Account? {
        try {
            val username = "igorSample"
            val domain = "conversations.im"
            val fixedUsername: Boolean = true
            val jid: Jid = Jid.ofLocalAndDomainEscaped(username, domain) //Create jId
            return if (jid.escapedLocal != jid.local || username.length < 3) {
                Timber.d("Username is invalid")
                null
            } else {
                var account = xmppConnectionService.findAccountByJid(jid)
                if (account == null) {
                    account = Account(jid, CryptoHelper.createPassword(SecureRandom()))
                    account.setOption(Account.OPTION_REGISTER, true)
                    account.setOption(Account.OPTION_DISABLED, true)
                    account.setOption(Account.OPTION_MAGIC_CREATE, true)
                    account.setOption(Account.OPTION_FIXED_USERNAME, fixedUsername)
                    xmppConnectionService.createAccount(account)
                }
                account
            }
        } catch (ex: Exception) {
            return null
        }
    }
}