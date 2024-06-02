package kr.co.lion.modigm.db.chat

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.lion.modigm.model.ChatMessagesData

class ChatMessagesDataSource {

    private val db = Firebase.firestore
    // 공통으로 쓰이는 collectionReference - ChatMessagesData 로 설정
    private val collectionReference = db.collection("ChatMessagesData")

    // Firestore 실시간 업데이트 리스너
    fun getChatMessagesListener(chatIdx: Int, onUpdate: (List<ChatMessagesData>) -> Unit) {
        collectionReference
            .whereEqualTo("chatIdx", chatIdx)
            .orderBy("chatFullTime", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // 에러 처리
                    return@addSnapshotListener
                }

                val chatMessages = mutableListOf<ChatMessagesData>()
                for (document in value!!) {
                    val chatMessage = document.toObject(ChatMessagesData::class.java)
                    chatMessage?.let{
                        chatMessages.add(it)
                    }
                }
                // 업데이트된 채팅 메시지를 콜백을 통해 전달
                onUpdate(chatMessages)
            }
    }

    // 메세지 전송 db 추가
    suspend fun insertChatMessagesData(chatMessagesData: ChatMessagesData, chatIdx: Int, chatSenderName:String, chatFullTime:Long){
        val coroutine1 = CoroutineScope(Dispatchers.IO).launch {
            val documentId = "${chatIdx}_${chatSenderName}_${chatFullTime}"
            collectionReference.document(documentId).set(chatMessagesData)
        }
        coroutine1.join()
    }

    companion object {

        // 공통으로 쓰이는 collectionReference - ChatMessagesData 로 설정
        private val collectionReference = Firebase.firestore.collection("ChatMessagesData")

        // 현재 채팅방 메세지 데이터 전체 가져오기
        suspend fun getChatMessages(chatIdx: Int): MutableList<ChatMessagesData> {
            var chatMessages = mutableListOf<ChatMessagesData>()

            val coroutine1 = CoroutineScope(Dispatchers.IO).launch {

                // chatIdx로 해당 채팅 방 전체 메세지 내용을 가져오며 작성했었던 시간을 오름차순으로 보여준다.
                val querySnapshot = collectionReference
                    .whereEqualTo("chatIdx", chatIdx)
                    .orderBy("chatFullTime", Query.Direction.ASCENDING)
                    .get()
                    .await()

                for (document in querySnapshot.documents) {
                    val chatMessage = document.toObject(ChatMessagesData::class.java)
                    chatMessage?.let {
                        chatMessages.add(it)
                    }
                }
            }
            coroutine1.join()

            return chatMessages
        }
    } // companion object
}