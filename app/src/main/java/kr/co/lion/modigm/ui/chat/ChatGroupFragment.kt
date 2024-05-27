package kr.co.lion.modigm.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.modigm.databinding.FragmentChatGroupBinding
import kr.co.lion.modigm.model.ChatRoomData
import kr.co.lion.modigm.ui.MainActivity
import kr.co.lion.modigm.ui.chat.adapter.ChatRoomAdapter
import kr.co.lion.modigm.ui.chat.dao.ChatRoomDao
import kr.co.lion.modigm.ui.chat.vm.ChatViewModel

class ChatGroupFragment : Fragment() {

    lateinit var fragmentChatGroupBinding: FragmentChatGroupBinding
    lateinit var mainActivity: MainActivity

    // 내가 속한 그룹 채팅 방들을 담고 있을 리스트
    var chatRoomDataList = mutableListOf<ChatRoomData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentChatGroupBinding = FragmentChatGroupBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val chatViewModel = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)
        chatViewModel.updateChatRoomData.observe(viewLifecycleOwner) {
            gettingGroupChatRoomData()
        }

        // Recycler 뷰
        setupRecyclerView()

        // 내가 속한 그룹 채팅 방(RecyclerView)
        gettingGroupChatRoomData()

        return fragmentChatGroupBinding.root
    }

    override fun onResume() {
        super.onResume()
        // 프래그먼트가 다시 활성화될 때 데이터 갱신
        Log.d("test1234", "ChatGroupFragment - onResume")
        // gettingGroupChatRoomData()
    }

    // RecyclerView 초기화
    private fun setupRecyclerView() {
        // 대화방 목록 RecyclerView 설정
        fragmentChatGroupBinding.recyclerViewChatGroup.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ChatRoomAdapter(chatRoomDataList, { roomItem ->
                // 대화방 선택 시 동작
                Log.d("test1234", "${roomItem.chatIdx}번 ${roomItem.chatTitle}에 입장")
            }, mainActivity)
        }
    }

    // 내가 속한 모든 그룹 채팅 방을 가져와 화면의 RecyclerView를 갱신한다.
    fun gettingGroupChatRoomData() {
        CoroutineScope(Dispatchers.Main).launch {
            // 대화방 목록 데이터 가져오기
            val newChatRoomDataList = ChatRoomDao.getGroupChatRooms()

            // 새로운 목록으로 업데이트
            chatRoomDataList.clear()
            chatRoomDataList.addAll(newChatRoomDataList)

            // RecyclerView 갱신
            activity?.runOnUiThread {
                fragmentChatGroupBinding.recyclerViewChatGroup.adapter?.notifyDataSetChanged()
            }
        }
    }
}