package kr.co.lion.modigm.repository

import kr.co.lion.modigm.db.study.RemoteStudyDataSource

class StudyRepository {
    private val studyDataSource = RemoteStudyDataSource()

    // 전체 스터디 목록을 가져온다.
    suspend fun getStudyAllData() = studyDataSource.getStudyAllData()

    // 전체 스터디 목록 중 모집중인 스터디만 가져온다.
    suspend fun getStudyStateTrueData() = studyDataSource.getStudyStateTrueData()

    // 내 스터디 목록을 가져온다. (홈화면 내 스터디 접근 시)
    suspend fun getStudyMyData() = studyDataSource.getStudyMyData()
}