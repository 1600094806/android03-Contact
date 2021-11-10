package com.ivyclub.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "FriendData")
data class FriendData(
    val phoneNumber: String, // 전화번호, pk
    val name: String, // 이름
    val birthday: String, // 생년월일
    val groupName: String, // 속한 그룹명
    val planList: List<Long>, // 약속 리스트(id)
    val isFavorite: Boolean, // 즐겨찾기 포함 유무
    val extraInfo: Map<String, String>, // 성격
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)

@Entity(tableName = "PlanData")
data class PlanData(
    val participant: List<Long>, // 만나는 사람들(id)
    val date: Date, // datetime
    val title: String = "", // 제목
    val place: String = "", // 장소
    val content: String = "", // 내용
    val color: String, // 고유색, HexCode
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0 // 약속 ID, pk
)

@Entity(tableName = "GroupData")
data class GroupData(
    @PrimaryKey
    val name: String // 이름, pk
)