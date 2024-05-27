package kr.co.lion.modigm.ui.profile.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lion.modigm.R
import kr.co.lion.modigm.databinding.RowLinkBinding
import kr.co.lion.modigm.databinding.RowPartStudyBinding

class PartStudyViewHolder(
    private val context: Context,
    private val rowPartStudyBinding: RowPartStudyBinding,
    private val rowClickListener: (String) -> Unit, ): RecyclerView.ViewHolder(rowPartStudyBinding.root) {

    // 구성요소 세팅
    fun bind(data: String, rowClickListener: (String) -> Unit) { // String 말고 모델이어야함
        rowPartStudyBinding.apply {
            // 썸네일
            imageRowPartStudy.setImageResource(R.drawable.image_loading_gray)
            // 스터디 제목
            textViewRowPartStudy.text = "[Kotlin] 파이쌤과 함께하는 python 30일 완성 스터디"

            // 항목에 대한 설정
            root.apply {
                // 항목 클릭 시 클릭되는 범위 설정
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                // 클릭 리스너 설정: 스터디 고유번호 전달
                setOnClickListener {
                    rowClickListener.invoke("https://github.com/orgs/APP-Android2/projects/25/views/1")
                }
            }
        }
    }
}