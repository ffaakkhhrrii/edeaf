package com.example.edeaf.model

data class HistoryResponse(
    val codeRoom: Int? = null,
    val endTime: String? = null,
    val historyText: String? = null,
    val liveId: String? = null,
    val participantId: Map<String, Participant>? = null,
    val startTime: String? = null,
    val title: String? = null,
    val userId: String? = null
)

data class Participant(
    val joinTime: String? = null,
    val participantId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val questions: List<String>? = null
)