package com.example.edeaf.data.model

data class LiveTrans(
    var liveId: String? = null,
    var userId: String? = null,
    var title: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var historyText: String? = null,
    var codeRoom: Int? = null,
    var participantId: String? = null
)