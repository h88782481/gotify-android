package com.github.gotify.messages.provider

import com.github.gotify.api.Api
import com.github.gotify.api.ApiException
import com.github.gotify.api.Callback
import com.github.gotify.client.api.MessageApi
import com.github.gotify.client.model.Message
import com.github.gotify.client.model.PagedMessages
import org.tinylog.kotlin.Logger

internal class MessageRequester(private val messageApi: MessageApi) {
    fun loadMore(state: MessageState): PagedMessages? {
        return try {
            Logger.info("从${state.appId}加载更多消息")
            if (MessageState.ALL_MESSAGES == state.appId) {
                Api.execute(messageApi.getMessages(LIMIT, state.nextSince))
            } else {
                Api.execute(messageApi.getAppMessages(state.appId, LIMIT, state.nextSince))
            }
        } catch (apiException: ApiException) {
            Logger.error(apiException, "请求消息失败")
            null
        }
    }

    fun asyncRemoveMessage(message: Message) {
        Logger.info("R删除id为${message.id}的消息")
        messageApi.deleteMessage(message.id).enqueue(Callback.call())
    }

    fun deleteAll(appId: Long): Boolean {
        return try {
            Logger.info("删除${appId}的所有消息")
            if (MessageState.ALL_MESSAGES == appId) {
                Api.execute(messageApi.deleteMessages())
            } else {
                Api.execute(messageApi.deleteAppMessages(appId))
            }
            true
        } catch (e: ApiException) {
            Logger.error(e, "无法删除消息")
            false
        }
    }

    companion object {
        private const val LIMIT = 100
    }
}
