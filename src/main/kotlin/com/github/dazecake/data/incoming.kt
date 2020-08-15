package com.github.dazecake.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Incoming

@Serializable
@SerialName("onmsg")
data class MC_onmsg(
    val target: String,
    val text: String
) : Incoming()

@Serializable
@SerialName("onjoin")
data class MC_onjoin(
    val target: String,
    val text: String
) : Incoming()

@Serializable
@SerialName("onleft")
data class MC_onleft(
    val target: String,
    val text: String
) : Incoming()

@Serializable
@SerialName("onCMD")
data class MC_onCMD(
    val target: String,
    val text: String
) : Incoming()

@Serializable
@SerialName("runcmd")
data class MC_cmd_return(
    val Auth: String = "",
    val text: String = ""
) : Incoming()

@Serializable
@SerialName("onservercrash")
data class MC_OnServerCrash(
    val reason: String = ""
) : Incoming()
