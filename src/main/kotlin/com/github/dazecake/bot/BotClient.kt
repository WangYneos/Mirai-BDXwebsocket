package com.github.dazecake.bot

import com.github.dazecake.BDXWebSocketPlugin
import com.github.dazecake.data.*
import com.github.dazecake.util.Template
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugins.ToBeRemoved

@ToBeRemoved
@KtorExperimentalAPI
@ImplicitReflectionSerializer
@OptIn(UnstableDefault::class)
object BotClient {

    suspend fun onReceive(pkg: Incoming) {
        when (pkg) {
            is MC_onmsg -> onMemberMessage(pkg)
            is MC_onjoin -> onMemberJoin(pkg)
            is MC_onleft -> onMemberLeave(pkg)
            is MC_onCMD -> onMemberCmd(pkg)
            is MC_cmd_return -> onCmdResp(pkg)
            is MC_OnServerCrash -> onServerCrash(pkg)
        }
    }

    public suspend fun notifyConnect() {
        pushMessage(Template.connectMsg)
    }
    var LastTimestamp : Long = 0
    public suspend fun notifyDrop() {
        LastTimestamp = System.currentTimeMillis()
        val NowTimestamp = System.currentTimeMillis()
        if((NowTimestamp - LastTimestamp)>10000)
            pushMessage(Template.dropMsg)
        BDXWebSocketPlugin.launchWebsocket()
    }

    public suspend fun notifyClose() {
        LastTimestamp = System.currentTimeMillis()
        val NowTimestamp = System.currentTimeMillis()
        if((NowTimestamp - LastTimestamp)>10000)
            pushMessage(Template.closeMsg)
        BDXWebSocketPlugin.launchWebsocket()
    }

    private suspend fun onMemberMessage(pkg: MC_onmsg) {
        // filter mc chat msg
        if(Template.prefixMc != "NONE"){
            if (pkg.text.startsWith(Template.prefixMc)) {
                pushMessage(Template.BDXTemplate.onMsg(pkg.target, pkg.text.removePrefix(Template.prefix)))
            }
        }else {
            pushMessage(Template.BDXTemplate.onMsg(pkg.target, pkg.text))
        }
    }

    private suspend fun onMemberJoin(pkg: MC_onjoin) {
        pushMessage(Template.BDXTemplate.onJoin(pkg.target))
    }

    private suspend fun onMemberLeave(pkg: MC_onleft) {
        pushMessage(Template.BDXTemplate.onLeave(pkg.target))
    }

    private suspend fun onMemberCmd(pkg: MC_onCMD) {
        pushMessage(Template.BDXTemplate.onCMD(pkg.target,pkg.text))
    }

    private suspend fun onCmdResp(pkg: MC_cmd_return) {
        pushMessage(pkg.text)
    }

    private suspend fun onServerCrash(pkg: MC_OnServerCrash) {
        pushMessage(pkg.reason)
    }


    private suspend fun pushMessage(msg: String) {
        if(msg.isEmpty()) return

        val bdx = BDXWebSocketPlugin
        bdx.bots.forEach {
            getBotOrNull(it)?.apply {
                pushFriendMessage(msg, bdx.pushFriend)
                pushGroupMessage(msg, bdx.pushGroup)
            }
        }
    }

    private suspend fun Bot.pushFriendMessage(msg: String, friends: List<Long>) {
        friends.forEach {
            try {
                this.getFriend(it).sendMessage(msg)
            } catch (e: NoSuchElementException) {
                BDXWebSocketPlugin.logger.info("Bot($id) 没有好友 $it")
            }
        }
    }

    private suspend fun Bot.pushGroupMessage(msg: String, groups: List<Long>) {
        groups.forEach {
            try {
                this.getGroup(it).sendMessage(msg)
            } catch (e: NoSuchElementException) {
                BDXWebSocketPlugin.logger.info("Bot($id) 没有群 $it")
            }
        }
    }

    private fun getBotOrNull(id: Long): Bot? = Bot.getInstanceOrNull(id)
}