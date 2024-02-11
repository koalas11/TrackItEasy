package com.kotlinenjoyers.trackiteasy.repository.linkingaccounts

import android.util.Log
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.handler.Constants
import com.kotlinenjoyers.trackiteasy.parceldatastore.Account
import com.kotlinenjoyers.trackiteasy.parceldatastore.Cookie
import com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.accounts.EndingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AmazonRepository: ILinkingAccountsRepository {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("AmazonRepository", coroutineContext.toString())
        Log.e("AmazonRepository", throwable.toString())
        Log.e("AmazonRepository", throwable.stackTraceToString())
    }

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun addLinkedAccount(scope: CoroutineScope, container: AppContainer, topLevelDomain: String, email: String, cookies: String, endStateCallback: (EndingState) -> Unit) {
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("AmazonRepository", coroutineContext.toString())
            Log.e("AmazonRepository", throwable.toString())
            Log.e("AmazonRepository", throwable.stackTraceToString())
            endStateCallback(EndingState.Error)
        }
        scope.launch(dispatcher + exceptionHandler) {
            container.accountsDataStore.updateData { protoTestBox ->
                val account = Account.newBuilder()
                    .setIdParserRetriever(Constants.AMAZON_ID)
                    .setInfo(email)
                    .setExtraInfo(topLevelDomain)
                    .setActive(true)

                cookies.split(";").forEach {
                    val cookie = Cookie.newBuilder()
                    val name = it.substringBefore("=").trim()
                    cookie.setName(name)
                    cookie.setValue(it.substringAfter("=").trim())
                    if (name == "csm-hit" || name == "csd-key")
                        cookie.setDomain("www.amazon.$topLevelDomain")
                    else
                        cookie.setDomain(".amazon.$topLevelDomain")

                    account.addCookies(
                        cookie.build()
                    )
                }

                val builder = protoTestBox.toBuilder()

                val oldIndex =
                    protoTestBox.accountList.indexOfFirst { it.info == email && it.idParserRetriever == Constants.AMAZON_ID }

                if (oldIndex != -1)
                    builder.removeAccount(oldIndex)

                builder.addAccount(account)

                builder.build()
            }
            endStateCallback(EndingState.Success)
        }
    }

    fun removeAccount(scope: CoroutineScope, container: AppContainer, account: Account) {
        scope.launch(dispatcher + exceptionHandler) {
            container.parcelsRepository.deleteByIdAndExtraInfo(
                Constants.AMAZON_ID,
                account.info,
            )

            container.accountsDataStore.updateData { accounts ->
                val builder = accounts.toBuilder()
                accounts.accountList.forEachIndexed { index, accountStorage ->
                    if (account == accountStorage) {
                        builder.removeAccount(index)
                        return@forEachIndexed
                    }
                }
                builder.build()
            }
        }
    }
}