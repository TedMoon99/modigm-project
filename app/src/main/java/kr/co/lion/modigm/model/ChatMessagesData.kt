package kr.co.lion.modigm.model

data class ChatMessagesData(
    val chatIdx : Int, // 채팅방 고유 Index
    val chatSenderId: String, // 채팅 전송자 UID
    val chatSenderName: String, // 채팅 전송자 Name
    val chatMessage: String, // 채팅 메세지 내용
    val chatFullTime : Long, // 메시지 고유 Index
    val chatTime: String, // 채팅 전송한 시간
    var read: Boolean = false // 읽음/안 읽음 상태를 나타내는 플래그
) {
    constructor(): this(0,"", "", "",0L, "")
}