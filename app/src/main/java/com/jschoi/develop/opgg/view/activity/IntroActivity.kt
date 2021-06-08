package com.jschoi.develop.opgg.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jschoi.develop.opgg.Config.CHAMPION_LIST_URL
import com.jschoi.develop.opgg.Config.SPELL_INFO_URL
import com.jschoi.develop.opgg.databinding.ActivityIntroBinding
import com.jschoi.develop.opgg.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    companion object {
        val championList = mutableMapOf<String, JSONObject>()
        val spellList = mutableMapOf<String, JSONObject>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(introBinding.root)

        // TODO 코루틴 공부할 것
        GlobalScope.launch(Dispatchers.IO) {
            championInfoJsonParse()
            spellInfoJsonParse()

            Thread.sleep(1000)
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
        }
    }

    private fun championInfoJsonParse() {
        val json = readJsonFromUrl(CHAMPION_LIST_URL)

        val json2 = json.getJSONObject("data")
        json2.keys().forEach {

            val data = json2.getJSONObject(it)
            val key = data.get("key").toString()
            championList[key] = data
        }
    }

    private fun spellInfoJsonParse() {
        val json = readJsonFromUrl(SPELL_INFO_URL)

        val json2 = json.getJSONObject("data")
        json2.keys().forEach {

            val data = json2.getJSONObject(it)
            val key = data.get("key").toString()
            LogUtil.warning(">>>> key : ${key}, >>>>> data : ${data.toString()}")

            spellList[key] = data
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun readJsonFromUrl(url: String): JSONObject {
        val str =
            URL(url).openStream()
        str.use { str ->
            val rd = BufferedReader(
                InputStreamReader(
                    str,
                    Charset.forName("UTF-8")
                )
            )
            val jsonText = readAll(rd);
            val json = JSONObject(jsonText);
            return json
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