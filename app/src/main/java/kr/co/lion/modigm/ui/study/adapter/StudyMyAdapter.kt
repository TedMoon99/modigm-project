package kr.co.lion.modigm.ui.study.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lion.modigm.databinding.RowStudyMyBinding
import kr.co.lion.modigm.model.StudyData

class StudyMyAdapter(
    // 내 스터디 리스트
    private var studyList: List<StudyData>,
    // 항목 1개 클릭 리스너
    private val rowClickListener: (Int) -> Unit,
) : RecyclerView.Adapter<StudyMyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StudyMyViewHolder {
        val binding: RowStudyMyBinding =
            RowStudyMyBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return StudyMyViewHolder(
            binding,
            rowClickListener,
        )
    }

    override fun getItemCount(): Int {
        return studyList.size
    }

    override fun onBindViewHolder(holder: StudyMyViewHolder, position: Int) {
        holder.bind(studyList[position],rowClickListener)
    }

    // 목록 새로고침
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<StudyData>) {
        studyList = list
        notifyDataSetChanged()
        Log.d("update adapter", list.toString())
    }
}