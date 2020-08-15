package com.github.dazecake.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Outgoing

@Serializable
@SerialName("runcmd")
data class RunCmd(
    var passwd: String,
    val cmd: String
) : Outgoing()

@Serializable
@SerialName("sendtext")
data class SendText(
    var passwd: String,
    val text: String
) : Outgoing()