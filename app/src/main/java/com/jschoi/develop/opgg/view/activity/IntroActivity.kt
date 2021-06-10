package com.jschoi.develop.opgg.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jschoi.develop.opgg.Config.CHAMPION_LIST_URL
import com.jschoi.develop.opgg.Config.DATA_DRAGON_LIST_URL
import com.jschoi.develop.opgg.Config.SPELL_LIST_URL
import com.jschoi.develop.opgg.databinding.ActivityIntroBinding
import com.jschoi.develop.opgg.dto.RuneDetailInfoDTO
import com.jschoi.develop.opgg.network.RetrofitClient
import com.jschoi.develop.opgg.network.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset


class IntroActivity : AppCompatActivity() {


    private lateinit var retrofit: Retrofit
    private lateinit var riotApiService: RetrofitService

    private lateinit var introBinding: ActivityIntroBinding

    private lateinit var version: String
    private val baseUrl = "http://ddragon.leagueoflegends.com/cdn/"
    val simpleRunMap = mutableMapOf<String, String>()

    companion object {
        private lateinit var DATA_DRAGON_URL: String
        val CHAMPION_LIST = mutableMapOf<String, JSONObject>()
        val SPELL_LIST = mutableMapOf<String, JSONObject>()
        val RUNES_LIST = mutableMapOf<String, JSONArray>()
        val RUNES_SIMPLE_INFO = mutableListOf<Map<String, String>>()

        // TODO
        val RUNES_DATA = mutableMapOf<Int, RuneDetailInfoDTO>()

        fun getCurrentVersionUrl() = DATA_DRAGON_URL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(introBinding.root)


        // TODO 코루틴 공부할 것
        GlobalScope.launch(Dispatchers.IO) {
            dataDragonVersionInfoJsonParse() // 데이터 버전 리스트
            championInfoJsonParse()     // 챔피언 리스트
            spellInfoJsonParse()        // 스펠 리스트
            runesReforgedJsonParse()    // 룬 리스트

            Thread.sleep(1000)
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
        }
    }

    /**
     * Data Dragon Version Check
     */
    private fun dataDragonVersionInfoJsonParse() {
        val json = readJsonArrayFromUrl(DATA_DRAGON_LIST_URL)
        version = json[0].toString()

        DATA_DRAGON_URL = baseUrl.plus(version)

        retrofit = RetrofitClient.setIntroBaseURL("${DATA_DRAGON_URL}/").getInstance()
        riotApiService = retrofit.create(RetrofitService::class.java)
    }

    /**
     * Champion Info List
     */
    private fun championInfoJsonParse() {
        val json = readJsonFromUrl(DATA_DRAGON_URL.plus(CHAMPION_LIST_URL))

        val json2 = json.getJSONObject("data")
        json2.keys().forEach {

            val data = json2.getJSONObject(it)
            val key = data.get("key").toString()
            CHAMPION_LIST[key] = data
        }
    }

    /**
     * Spell Info List
     */
    private fun spellInfoJsonParse() {
        val json = readJsonFromUrl(DATA_DRAGON_URL.plus(SPELL_LIST_URL))

        val json2 = json.getJSONObject("data")
        json2.keys().forEach {

            val data = json2.getJSONObject(it)
            val key = data.get("key").toString()

            SPELL_LIST[key] = data
        }
    }

    /**
     * Runes Info List
     */
    private fun runesReforgedJsonParse() {
        riotApiService.reqRunInfo().enqueue(object : Callback<List<RuneDetailInfoDTO>> {
            override fun onResponse(
                call: Call<List<RuneDetailInfoDTO>>,
                response: Response<List<RuneDetailInfoDTO>>
            ) {
                if (response.isSuccessful.not()) return
                response.body()?.let {
                    for (data in it) {
                        RUNES_DATA[data.id] = data
                    }
                }
            }

            override fun onFailure(call: Call<List<RuneDetailInfoDTO>>, t: Throwable) {
            }
        })
    }

    @Throws(IOException::class, JSONException::class)
    private fun readJsonFromUrl(url: String): JSONObject {
        var str =
            URL(url).openStream()
        str.use { str ->
            val rd = BufferedReader(
                InputStreamReader(
                    str,
                    Charset.forName("UTF-8")
                )
            )
            var jsonText = readAll(rd)
            return JSONObject(jsonText)
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun readJsonArrayFromUrl(url: String): JSONArray {
        var str =
            URL(url).openStream()
        str.use { str ->
            val rd = BufferedReader(
                InputStreamReader(
                    str,
                    Charset.forName("UTF-8")
                )
            )
            var jsonText = readAll(rd)

            return JSONArray(jsonText)
        }
    }

    @Throws(IOException::class)
    private fun readAll(rd: Reader): String {
        val sb = StringBuilder()
        var cp: Int
        while (rd.read().also { cp = it } != -1) {
            sb.append(cp.toChar())
        }
        return sb.toString()
    }

}