import android.app.Application
import com.jschoi.develop.opgg.util.LogUtil
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset
//
//class App : Application() {
//
//    companion object {
//        @Volatile
//        private var instance: Application? = null
//
//        @JvmStatic
//        fun getInstance(): Application =
//            instance ?: synchronized(this) {
//                instance ?: Application().also {
//                    instance = it
//                }
//            }
//
//        val championList = mutableMapOf<String, Any>()
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        Thread {
//            val json =
//                readJsonFromUrl("http://ddragon.leagueoflegends.com/cdn/11.11.1/data/en_US/champion.json")
//
//            val json2 = json.getJSONObject("data")
//            json2.keys().forEach {
//
//                val data = json2.getJSONObject(it)
//                val key = data.get("key").toString()
//                championList[key] = data
//            }
//            LogUtil.warning("${championList["41"]}")
//        }.start()
//    }
//
//    @Throws(IOException::class, JSONException::class)
//    private fun readJsonFromUrl(url: String): JSONObject {
//        val str =
//            URL(url).openStream()
//        str.use { str ->
//            val rd = BufferedReader(
//                InputStreamReader(
//                    str,
//                    Charset.forName("UTF-8")
//                )
//            )
//            val jsonText = readAll(rd);
//            val json = JSONObject(jsonText);
//            return json
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun readAll(rd: Reader): String {
//        val sb = StringBuilder()
//        var cp: Int
//        while (rd.read().also { cp = it } != -1) {
//            sb.append(cp.toChar())
//        }
//        return sb.toString()
//    }
//
//    fun getChampionListData(): MutableMap<String, Any> {
//        return championList
//    }
// }