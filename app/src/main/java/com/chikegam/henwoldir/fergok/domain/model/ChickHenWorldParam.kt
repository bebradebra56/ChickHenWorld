package com.chikegam.henwoldir.fergok.domain.model

import com.google.gson.annotations.SerializedName


private const val CHICK_HEN_WORLD_A = "com.chikegam.henwoldir"
private const val CHICK_HEN_WORLD_B = "chickhenworld-b9222"
data class ChickHenWorldParam (
    @SerializedName("af_id")
    val chickHenWorldAfId: String,
    @SerializedName("bundle_id")
    val chickHenWorldBundleId: String = CHICK_HEN_WORLD_A,
    @SerializedName("os")
    val chickHenWorldOs: String = "Android",
    @SerializedName("store_id")
    val chickHenWorldStoreId: String = CHICK_HEN_WORLD_A,
    @SerializedName("locale")
    val chickHenWorldLocale: String,
    @SerializedName("push_token")
    val chickHenWorldPushToken: String,
    @SerializedName("firebase_project_id")
    val chickHenWorldFirebaseProjectId: String = CHICK_HEN_WORLD_B,

    )