package kr.co.lion.modigm.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.lion.modigm.R
import kr.co.lion.modigm.databinding.FragmentBottomNaviBinding
import kr.co.lion.modigm.ui.chat.ChatFragment
import kr.co.lion.modigm.ui.like.LikeFragment
import kr.co.lion.modigm.ui.profile.ProfileFragment


class BottomNaviFragment : Fragment() {

    private lateinit var binding: FragmentBottomNaviBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 바인딩
        binding = FragmentBottomNaviBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    fun initView(){

        // 최초 화면이 null 이라면 스터디 목록을 띄운다.
        if(childFragmentManager.findFragmentById(R.id.bottomNaviContainer) == null){
            childFragmentManager.beginTransaction()
                .replace(R.id.bottomNaviContainer, StudyFragment())
                .commit()
        }

        // 바인딩
        with(binding){

            // 바텀 내비게이션
            with(bottomNavigationView){

                // 아이템 클릭 시 전환 설정
                setOnItemSelectedListener { item ->
                    when(item.itemId) {

                        // 스터디 클릭 시
                        R.id.bottomNaviStudy -> {
                            childFragmentManager.beginTransaction()
                                .replace(R.id.bottomNaviContainer, StudyFragment())
                                .commit()
                        }

                        // 찜 클릭 시
                        R.id.bottomNaviHeart -> {
                            childFragmentManager.beginTransaction()
                                .replace(R.id.bottomNaviContainer, LikeFragment())
                                .commit()
                        }

                        // 채팅 클릭 시
                        R.id.bottomNaviChat -> {
                            childFragmentManager.beginTransaction()
                                .replace(R.id.bottomNaviContainer, ChatFragment())
                                .commit()
                        }

                        // 마이 클릭 시
                        R.id.bottomNaviMy -> {
                            childFragmentManager.beginTransaction()
                                .replace(R.id.bottomNaviContainer, ProfileFragment())
                                .commit()
                        }

                    }
                    true
                }
            }
        }
    }
}