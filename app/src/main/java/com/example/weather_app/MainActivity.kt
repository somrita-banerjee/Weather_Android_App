package com.example.weather_app

import android.os.Bundle
import android.os.*
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutionException

class MainActivity : AppCompatActivity() {

    private lateinit var cityName: TextView
    private lateinit var search: Button
    private lateinit var show: TextView
    private var url: String? = null

    inner class GetWeather : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String?): String? {
            val result = StringBuilder()
            try {
                val url = URL(urls[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connect()

                val inputStream: InputStream = urlConnection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    result.append(line).append("\n")
                }
                return result.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                var weatherInfo = jsonObject.getString("main")
                weatherInfo = weatherInfo.replace("temp", "Temperature")
                weatherInfo = weatherInfo.replace("feels_like", "Feels Like")
                weatherInfo = weatherInfo.replace("temp_max", "Temperature Max")
                weatherInfo = weatherInfo.replace("temp_min", "Temperature Min")
                weatherInfo = weatherInfo.replace("pressure", "Pressure")
                weatherInfo = weatherInfo.replace("humidity", "Humidity")
                weatherInfo = weatherInfo.replace("{", "")
                weatherInfo = weatherInfo.replace("}", "")
                weatherInfo = weatherInfo.replace(",", "\n")
                weatherInfo = weatherInfo.replace(":", " : ")
                show.text = weatherInfo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cityName = findViewById(R.id.cityName)
        search = findViewById(R.id.search)
        show = findViewById(R.id.weather_details)

        search.setOnClickListener {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            val city = cityName.text.toString()
            try {
                if (city.isNotEmpty()) {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=5b31d5be094d1d75d8dee2e42ff9cfb0"
                } else {
                    Toast.makeText(this@MainActivity, "Enter City", Toast.LENGTH_SHORT).show()
                }
                val task = GetWeather()
                val temp = task.execute(url).get()
                if (temp == null) {
                    show.text = "Cannot find weather"
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()

            }            }


    }
}