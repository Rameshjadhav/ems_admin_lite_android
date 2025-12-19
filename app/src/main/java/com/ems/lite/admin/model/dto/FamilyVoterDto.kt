package com.ems.lite.admin.model.dto

import androidx.room.Embedded
import com.ems.lite.admin.model.table.Voter

class FamilyVoterDto {
    @Embedded
    lateinit var voter: Voter

    var boothName: String? = null
}