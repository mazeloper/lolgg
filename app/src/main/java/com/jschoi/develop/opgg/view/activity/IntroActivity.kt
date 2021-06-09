package com.jschoi.develop.opgg.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jschoi.develop.opgg.Config.CHAMPION_LIST_URL
import com.jschoi.develop.opgg.Config.DATA_DRAGON_LIST_URL
import com.jschoi.develop.opgg.Config.RUNE_LIST_URL
import com.jschoi.develop.opgg.Config.SPELL_LIST_URL
import com.jschoi.develop.opgg.databinding.ActivityIntroBinding
import com.jschoi.develop.opgg.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset

class IntroActivity : AppCompatActivity() {

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

    private fun dataDragonVersionInfoJsonParse() {
        val json = readJsonArrayFromUrl(DATA_DRAGON_LIST_URL)
        version = json[0].toString()

        DATA_DRAGON_URL = baseUrl.plus(version)
    }

    private fun championInfoJsonParse() {
        val json = readJsonFromUrl(DATA_DRAGON_URL.plus(CHAMPION_LIST_URL))

        val json2 = json.getJSONObject("data")
        json2.keys().forEach {

            val data = json2.getJSONObject(it)
            val key = data.get("key").toString()
            CHAMPION_LIST[key] = data
        }
    }

    private fun spellInfoJsonParse() {
        val json = readJsonFromUrl(DATA_DRAGON_URL.plus(SPELL_LIST_URL))

        val json2 = json.getJSONObject("data")
        json2.keys().forEach {

            val data = json2.getJSONObject(it)
            val key = data.get("key").toString()

            SPELL_LIST[key] = data
        }
    }

    // TODO 룬이미지 JSON
    private fun runesReforgedJsonParse() {
        val json = readJsonArrayFromUrl(DATA_DRAGON_URL.plus(RUNE_LIST_URL))
        for (i in 0 until json.length()) {
            val json2 = json.getJSONObject(i)
            json2.keys().forEach {
                if (it != "slots") {
                    simpleRunMap[it] = json2.getString(it)
                }
            }
            val key = json2.get("id").toString()
            val data = json2.getJSONArray("slots")

            RUNES_LIST[key] = data
        }
        RUNES_SIMPLE_INFO
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