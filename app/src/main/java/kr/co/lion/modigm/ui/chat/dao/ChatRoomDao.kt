package kr.co.lion.modigm.ui.chat.dao

import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.lion.modigm.model.ChatRoomData

class ChatRoomDao {

    companion object{

        // 공통으로 쓰이는 collectionReferenceSequence - Sequence 로 설정
        val collectionReferenceSequence = Firebase.firestore.collection("Sequence")
        // 공통으로 쓰이는 collectionReference - ChatRoomData 로 설정
        val collectionReference = Firebase.firestore.collection("ChatRoomData")

        // 채팅 방 시퀀스 번호를 Get 후 반환함
        suspend fun getChatRoomSequence():Int{

            var chatRoomSequence = -1

            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
                val documentReference = collectionReferenceSequence.document("ChatRoomSequence")
                val documentSnapShot = documentReference.get().await()
                chatRoomSequence = documentSnapShot.getLong("value")?.toInt()!!
            }
            coroutine1.join()

            return chatRoomSequence
        }

        // 채팅 방 시퀀스 값을 변경함 (Update)
        suspend fun updateChatRoomSequence(userSequence:Int){
            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
                val documentReference = collectionReferenceSequence.document("ChatRoomSequence")
                // 저장할 데이터를 담을 HashMap을 만들어준다.
                val map = mutableMapOf<String, Long>()
                map["value"] = userSequence.toLong()
                // 저장
                documentReference.set(map)
            }
            coroutine1.join()
        }

        // 채팅 방을 생성함 (Create)
        suspend fun insertChatRoomData(chatRoomData: ChatRoomData){
            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
                collectionReference.add(chatRoomData)
            }
            coroutine1.join()
        }

        // 리스너를 추가하고 데이터 변경 시에 실행하는 메서드로 변경
        // 채팅방 데이터 가져옴 (Read) groupChat = true -> 그룹 / false -> 1:1
        fun updateChatRoomsListener(userId: String, groupChat: Boolean , onUpdate: (List<ChatRoomData>) -> Unit) {
            collectionReference
                .whereArrayContains("chatMemberList", userId)
                .whereEqualTo("groupChat", groupChat)
                .orderBy("lastChatFullTime", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        // 에러 처리
                        return@addSnapshotListener
                    }
                    val chatRooms = mutableListOf<ChatRoomData>()
                    for (document in value!!) {
                        val chatRoom = document.toObject(ChatRoomData::class.java)
                        chatRoom?.let {
                            chatRooms.add(it)
                        }
                    }
                    // Update 된 채팅 방을 콜백을 통해 전달
                    onUpdate(chatRooms)
                }
        }

        // 리스너를 추가하고 데이터 변경 시에 실행하는 메서드로 변경
        fun updateChatAllRoomsListener(userId: String, onUpdate: (List<ChatRoomData>) -> Unit) {
            collectionReference
                .whereArrayContains("chatMemberList", userId)
                .orderBy("lastChatFullTime", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        // 에러 처리
                        return@addSnapshotListener
                    }
                    val chatRooms = mutableListOf<ChatRoomData>()
                    for (document in value!!) {
                        val chatRoom = document.toObject(ChatRoomData::class.java)
                        chatRoom?.let {
                            chatRooms.add(it)
                        }
                    }
                    // Update 된 채팅 방을 콜백을 통해 전달
                    onUpdate(chatRooms)
                }
        }

        // 해당 채팅방 데이터 [마지막 메세지, 마지막 메세지 시간] 변경함 (Update)
        suspend fun updateChatRoomLastMessageAndTime(chatIdx: Int, chatMessage: String, chatFullTime: Long, chatTime: String): MutableList<ChatRoomData> {
            var chatRooms = mutableListOf<ChatRoomData>()

            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
                // 내 아이디의 chatIdx 와 groupChat이 true인 경우 필터링
                // chatIdx는 UserData가 있다면 그 Data에서 아이디 별 소속해있는 채팅방 int형 list를 가져와야함.
                val querySnapshot = collectionReference
                    .whereEqualTo("chatIdx", chatIdx)
                    .get()
                    .await()

                for (document in querySnapshot.documents) {
                    val documentReference = document.reference
                    documentReference.update(
                        mapOf(
                            "lastChatMessage" to chatMessage,
                            "lastChatFullTime" to chatFullTime,
                            "lastChatTime" to chatTime
                        )
                    ).await()
                }
            }
            coroutine1.join()

            return chatRooms
        }

        // 채팅방 나가기 / chatMemberList 배열에서 내 ID를 제거 (Update)
        suspend fun removeUserFromChatMemberList(chatIdx: Int, userId: String) {
            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
                val querySnapshot = collectionReference
                    .whereEqualTo("chatIdx", chatIdx)
                    .get()
                    .await()

                for (document in querySnapshot.documents) {
                    val documentReference = document.reference
                    documentReference.update("chatMemberList",
                        com.google.firebase.firestore.FieldValue.arrayRemove(userId)
                    ).await()
                }
            }
            coroutine1.join()
        }

        // 로그인 한 사용자 해당 채팅 방 메세지 읽음 처리 (Update)
        suspend fun chatRoomMessageAsRead(chatIdx: Int, loginUserId: String) {
            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
                val chatRoomRef = collectionReference.whereEqualTo("chatIdx", chatIdx)
                val querySnapshot = chatRoomRef.get().await()
                querySnapshot.forEach { document ->
                    val chatRoom = document.toObject(ChatRoomData::class.java)
                    chatRoom?.let {
                        it.unreadMessageCount[loginUserId] = 0
                        document.reference.set(it).await()
                    }
                }
            }
            coroutine1.join()
        }

        // 메세지 전송시 해당 채팅 방 사용자에 안읽은 메세지 카운트 증가
        suspend fun increaseUnreadMessageCount(chatIdx: Int, senderId: String) {
            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
                val chatRoomRef = collectionReference.whereEqualTo("chatIdx", chatIdx)
                val querySnapshot = chatRoomRef.get().await()
                querySnapshot.forEach { document ->
                    val chatRoom = document.toObject(ChatRoomData::class.java)
                    chatRoom?.let {
                        for (participant in it.chatMemberList) {
                            // 현재 입장 여부 확인
//                            if (participant != senderId && it.chatMemberState[participant] == false) {
//                                it.unreadMessageCount[participant] = it.unreadMessageCount.getOrDefault(participant, 0) + 1
//                            }
                            // 로그인 사용자만 빼고 Count 증가
                            if (participant != senderId) {
                                it.unreadMessageCount[participant] = it.unreadMessageCount.getOrDefault(participant, 0) + 1
                            }
                        }
                        document.reference.set(it).await()
                    }
                }
            }
            coroutine1.join()
        }

    }
}