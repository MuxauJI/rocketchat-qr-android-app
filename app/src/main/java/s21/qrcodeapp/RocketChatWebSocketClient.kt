package s21.qrcodeapp

import okhttp3.internal.notifyAll
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI

class RocketChatWebSocketClient(
    webSocketUri: String,
    private var token: String,
    private var botUserName: String
) : WebSocketClient(URI(webSocketUri)) {

    var responseString: String? = null
    var finished: Boolean = false
        private set

    var onOpenListener: (() -> Unit)? = null
    var onMessageListener: ((String) -> Unit)? = null

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("Connected to Rocket.Chat")
        connectByToken(token)
    }

    override fun onMessage(message: String?) {
        println("Received message: $message")
        val jsonMessage = JSONObject(message ?: "")
        val msgType = jsonMessage.getString("msg")

        when (msgType) {
            "result" -> {
                val id = jsonMessage.getString("id")
                if (id == "42") {
                    if (jsonMessage.has("error")) {
                        responseString = "Login failed: " + jsonMessage.getJSONObject("error").getString("message")
                        closeConnection()
                    } else {
                        openDirectMessage(botUserName)
                    }
                } else if (id == "unique_create_dm_id") {
                    if (jsonMessage.has("error")) {
                        responseString = "Failed to create direct message: " + jsonMessage.getJSONObject("error").getString("message")
                        closeConnection()
                    } else {
                        val result = jsonMessage.getJSONObject("result")
                        val roomId = result.optString("rid")
                        println("Room ID for direct message with $botUserName: $roomId")
                        subscribeToRoomMessages(roomId)
                        sendCommand(roomId, ENTER_COMMAND)
                    }
                }
            }
            "changed" -> {
                if (jsonMessage.optString("collection") == "stream-room-messages") {
                    val fields = jsonMessage.optJSONObject("fields")
                    fields?.let {
                        val args = it.optJSONArray("args")?.optJSONObject(0)
                        args?.let { arg ->
                            val receivedMessageText = arg.optString("msg")
                            if (receivedMessageText.contains(EXPECTED_MSG)) {
                                // Извлечение base64-кода из строки
                                val base64Regex = "data:image/png;base64,([^\"]+)".toRegex()
                                val matchResult = base64Regex.find(receivedMessageText)
                                matchResult?.let {
                                    responseString = "data:image/png;base64," + it.groupValues[1]
                                    println("Received base64 image data")
                                }
                                closeConnection()
                            }
                        }
                    }
                }
            }
            "ping" -> {
                println("Received ping")
                send(JSONObject().put("msg", "pong").toString())
                println("Sent pong")
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        synchronized(this) {
            finished = true
            notifyAll()
        }
    }

    override fun onError(ex: Exception?) {
        responseString = "Error: " + ex?.message
        println("Error: " + ex?.message)
        closeConnection()
    }

    private fun connectByToken(token: String) {
        val connectMessage = JSONObject().apply {
            put("msg", "connect")
            put("version", "1")
            put("support", JSONArray(listOf("1", "pre2", "pre1")))
        }
        send(connectMessage.toString())

        val loginMessage = JSONObject().apply {
            put("msg", "method")
            put("method", "login")
            put("id", "42")
            put("params", JSONArray().put(JSONObject().put("resume", token)))
        }
        send(loginMessage.toString())
    }

    private fun subscribeToRoomMessages(roomId: String) {
        println("Subscribing to room: $roomId")
        val subscribeMessage = JSONObject().apply {
            put("msg", "sub")
            put("id", "unique_subscription_id")
            put("name", "stream-room-messages")
            put("params", JSONArray(listOf(roomId, true)))
        }
        send(subscribeMessage.toString())
    }

    private fun openDirectMessage(username: String) {
        val createDMMessage = JSONObject().apply {
            put("msg", "method")
            put("method", "createDirectMessage")
            put("id", "unique_create_dm_id")
            put("params", JSONArray().put(username))
        }
        send(createDMMessage.toString())
    }

    private fun sendCommand(roomId: String, command: String) {
        println("Sending command: $command")
        val commandObject = JSONObject().apply {
            put("msg", "method")
            put("method", "slashCommand")
            put("id", "unique_command_id")
            put("params", JSONArray().put(JSONObject().apply {
                put("cmd", command.substring(1))
                put("params", "")
                put("msg", JSONObject().put("rid", roomId))
            }))
        }
        send(commandObject.toString())
    }

    private fun closeConnection() {
        if (!finished) {
            println("Connection already closed")
            close()
        }
    }

    companion object {
        private const val TAG = "RocketChatWebSocketClient"
        private const val ENTER_COMMAND = "/enter"
        private const val EXPECTED_MSG = "The QR code is valid for 30 seconds after creation"
    }
}
