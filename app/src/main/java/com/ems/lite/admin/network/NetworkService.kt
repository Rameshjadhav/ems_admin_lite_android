package com.ems.lite.admin.network

import android.content.Context
import com.ems.lite.admin.app.MyApplication
import com.ems.lite.admin.model.request.*
import com.ems.lite.admin.model.response.*
import com.ems.lite.admin.model.response.VoterListResponse
import com.ems.lite.admin.report.model.api.request.ReportCountRequest
import com.ems.lite.admin.report.model.api.response.ReportCountResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class NetworkService @JvmOverloads constructor(
    context: Context?, baseUrl: String = domainName
) {
    lateinit var api: NetworkAPI
        private set
    private lateinit var okHttpClient: OkHttpClient
    private var mContext: Context? = null

    @JvmOverloads
    fun createNetworkService(context: Context?, baseUrl: String = domainName) {
        mContext = context
        okHttpClient = MyApplication.getOkHttpClientFactory().getOkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(okHttpClient)
            .build()
        api = retrofit.create(NetworkAPI::class.java)
    }

    /**
     * all the Service calls to use for the retrofit requests.
     */
    interface NetworkAPI {
        @Headers("$HEADER_BEAR: false")
        @GET(Url.API + "insertData.php?action=getappsetting")
        fun getAppSetting(): Call<AppSettingResponse>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "insertData.php?action=AdminLoginv2")
        suspend fun login(@Body request: LoginRequest?): Response<LoginResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "insertData.php?action=getAdminStatus")
        suspend fun getMe(@Body request: UserRequest?): Response<LoginResponse?>

        @POST(Url.API + "insertData.php?action=getVillageMasters")
        fun getVillageMasterList(@Body request: VillageListRequest): Call<VillageListResponse>

        @POST(Url.API + "insertData.php?action=getBoothMasters")
        fun getBoothMasterList(): Call<BoothListResponse>

        @POST(Url.API + "insertData.php?action=getCastMasters")
        fun getCastList(): Call<CastListResponse>

        @POST(Url.API + "insertData.php?action=getProfessionMasters")
        fun getProfessionList(): Call<ProfessionListResponse>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "insertData.php?action=getDesignationMasters")
        fun getDesignationMasters(): Call<DesignationListResponse>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "insertData.php?action=getReligionMasters")
        fun getReligionMasters(): Call<ReligionListResponse>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "insertData.php?action=getVoterAdminMaster")
        fun getVoterMasterList(@Query("offset") offset: Long): Call<VoterListResponse>

        @POST(Url.API + "insertData.php?action=getMastersv2")
        fun getUpdatedVoterList(@Body request: UpdatedVoterListRequest): Call<VoterListResponse>

        @POST(Url.API + "insertData.php")
        fun saveVoterList(
            @Query("action") action: String,
            @Body reqLogin: SaveVoterListRequest
        ): Call<VoterListResponse>

        @POST(Url.API + "insertData.php?action=saveVoter")
        fun saveVoter(@Body reqLogin: SaveVoterRequest): Call<VoterListResponse>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "insertData.php?action=getUserList")
        suspend fun getUserList(@Body request: SearchUserListRequest?): Response<UserListResponse?>

        @POST(Url.API + "insertData.php?action=UpdateUser")
        fun updateUser(
            @Body request: UpdateUserRequest
        ): Call<CommonResponse>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "insertData.php?action=getVoterListOnlineAdmin")
        suspend fun getVoterList(@Body request: VoterListRequest?): Response<VoterListResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "insertData.php?action=getReportCount")
        suspend fun getReportCount(@Body request: ReportCountRequest?): Response<ReportCountResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "insertData.php?action=getRelativeList")
        suspend fun getRelativeList(@Body request: RelativeListRequest?): Response<RelativeListResponse?>

        @Headers("$HEADER_BEAR: true")
        @GET(Url.API + "insertData.php?action=getRelativeTalukaMaster")
        suspend fun getRelativeTalukaMaster(): Response<TalukaListResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "insertData.php?action=saveRelatives")
        suspend fun saveRelative(@Body request: SaveRelativeRequest?): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "insertData.php?action=getFamilyHeadList")
        suspend fun getRelativeCountList(@Body request: com.ems.lite.admin.model.request.RelativeCountListRequest?): Response<RelativeCountListResponse?>
    }

    companion object {

        const val HEADER_USER_AGENT = "User-Agent"

        /**
         * For Header Request Key
         */
        const val HEADER_BEAR = "HeaderBear"
        const val HEADER_BASIC = "HeaderBasic"
        const val HEADER_OPTION = "HeaderOption"
        const val HEADER_APP_VERSION = "app_version"
        const val HEADER_APP_VERSION_NAME = "X-App-Version-Name"
        const val HEADER_API_VERSION = "X-Api-Version-Code"

        /**
         * For Param Key
         */
        const val HEADER_AUTHORIZATION = "Authorization"
        private val domainName: String
            get() = Url.HOST
    }

    init {
        createNetworkService(context, baseUrl)
    }
}