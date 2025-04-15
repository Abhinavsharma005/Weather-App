package com.example.weatherapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import pl.droidsonroids.gif.GifDrawable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// bfbd71cd280f75c31cefe4f32f0f1bf2 bhopal api
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)  //R.layout.activity_main
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        fetchWeatherData("Bhopal")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "bfbd71cd280f75c31cefe4f32f0f1bf2","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunSet.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.cityName.text = "$cityName"

                    //Log.d("TAG", "onResponse: $temperature")
                    changeImagesAccordingToWeatherConditions(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeImagesAccordingToWeatherConditions(conditions: String) {
        when (conditions){
            "Clouds" ->{
                binding.root.setBackgroundResource(R.drawable.cloudy)
                binding.lottieAnimationView.setImageResource(R.drawable.clouds_unscreen)
                binding.lottieAnimationView.post {
                    val gifDrawable = binding.lottieAnimationView.drawable as? GifDrawable
                    gifDrawable?.start()
                }

            }
            "Clear", "Sunny","Clear Sky" ->{
                binding.root.setBackgroundResource(R.drawable.clear_sky_2)
                binding.lottieAnimationView.setImageResource(R.drawable.sun_unseen)
                binding.lottieAnimationView.post {
                    val gifDrawable = binding.lottieAnimationView.drawable as? GifDrawable
                    gifDrawable?.start()
                }
            }
            "Rain", "Shower", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_bg)
                binding.lottieAnimationView.setImageResource(R.drawable.rain_unscreen)
                binding.lottieAnimationView.post {
                    val gifDrawable = binding.lottieAnimationView.drawable as? GifDrawable
                    gifDrawable?.start()
                }
            }
            "Snow" ->{
                binding.root.setBackgroundResource(R.drawable.snow_bg)
                binding.lottieAnimationView.setImageResource(R.drawable.snow_unscreen)
                binding.lottieAnimationView.post {
                    val gifDrawable = binding.lottieAnimationView.drawable as? GifDrawable
                    gifDrawable?.start()
                }
            }
            "Thunderstorm" ->{
                binding.root.setBackgroundResource(R.drawable.lighting)
                binding.lottieAnimationView.setImageResource(R.drawable.storm_unscreen)
                binding.lottieAnimationView.post {
                    val gifDrawable = binding.lottieAnimationView.drawable as? GifDrawable
                    gifDrawable?.start()
                }
            }


        }

    }


    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
