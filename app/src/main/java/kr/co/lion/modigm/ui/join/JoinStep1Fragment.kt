package kr.co.lion.modigm.ui.join

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.lion.modigm.databinding.FragmentJoinStep1Binding

class JoinStep1Fragment : Fragment() {

    val binding: FragmentJoinStep1Binding by lazy {
        FragmentJoinStep1Binding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    // 입력한 내용 유효성 검사
    fun validate(): Boolean {
        return true
    }

}