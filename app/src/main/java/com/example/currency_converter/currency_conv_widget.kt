package com.example.currency_converter

import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import android.widget.ArrayAdapter
import android.R
import android.widget.Spinner


enum class Master {
    NO_ONE,
    CONVERT_FROM,
    CONVERT_TO,
    CURRENCY_FROM,
    CURRENCY_TO,
}


class Currency_converter_widget(instance:MainActivity) {
    var instance:MainActivity
    var count_from:Double? = 0.0
    var count_to: Double = 0.0
    var rates: Map<String, Double>? = null

    private val client = OkHttpClient()

    init{
        this.instance = instance
        run("http://data.fixer.io/api/latest?access_key=4353a124be0c1f20ccc43039c18c3d38")
    }

    fun run(url: String) {
        var request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Попробуем еще раз через 1 сек.
                Thread.sleep(1000)
                run("http://data.fixer.io/api/latest?access_key=4353a124be0c1f20ccc43039c18c3d38")
            }
            override fun onResponse(call: Call, response: Response) {
                success_load(response)
            }
        })
    }

    fun update_convert_from_count(new_count:Double) {
        count_from = new_count
        count_to = new_count * 0
    }

    fun update_convert_to_count(new_count:Double) {
        count_to = new_count
        count_from = new_count / 0
    }

    fun success_load(response: Response) {
        val gson = Gson()
        // response.body может быть использован только один раз
        rates = gson.fromJson(response.body?.string(), ResponseFixerIo::class.java).rates

        instance.runOnUiThread(Runnable {
            instance.layout_with_progress_bar.visibility = View.INVISIBLE
            instance.layout_main.visibility = View.VISIBLE


            // Create an ArrayAdapter using the string array and a default spinner layout
            val adapter = ArrayAdapter<String>(
                instance, android.R.layout.simple_spinner_item, ArrayList(rates!!.keys)
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            instance.spinner3.adapter = adapter
            instance.spinner4.adapter = adapter
        })

    }


}