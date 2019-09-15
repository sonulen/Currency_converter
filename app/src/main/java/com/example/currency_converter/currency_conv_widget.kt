package com.example.currency_converter

import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import android.widget.ArrayAdapter

// Енам о том кто источник изменений данных
enum class Master {
    NO_ONE,
    CONVERT_FROM,
    CONVERT_TO
}

// Виджет пересчета валюты. Реализует:
// - Запрос из API данных о валюте
// - Пересчет кол-ва валют
// - Обновление UI для корректного отображения валюты
// Жестко привязан к MainActivity. Интересно как можно было сделать лучше.
class Currency_converter_widget(instance:MainActivity) {
    // Передаю сюда сам инстанс активити, чтобы менять на ней текст
    // Не понятно на сколько это корректно, но пока так
    var instance:MainActivity

    // Ниже данные для расчётов
    var count_from:Double = 0.0 // Кол-во ед. валют ИЗ которой конвертируем
    var count_to: Double = 0.0 // Кол-во ед. валют В которую конвертируем

    var currency_from: String = "USD" // По-умолчанию валюта ИЗ - Доллар
    var currency_to: String = "RUB" // По умолчанию валют В - Рубль
    var coeff: Double = 0.0 // Изначально кэф 0.

    // Ниже данные от API
    // Мапа с значениями ВАЛЮТА : КОЭФФЦИЕНт
    lateinit var rates: Map<String, Double>

    // Http клиент
    private val client = OkHttpClient()

    // Дефолт конструктор. Сохраним инстанс активити и вызовем запрос из апи.
    // Пока не будет ответа на активити будет лоад бар и строка "Обновление данных"
    // Сделал коряво через relative layout (потому что с института помню его)
    // Иначе не знал как наложить элементы друг на друга.
    // Пока ответа нет будет крутится лоад бар и все остальные элементы будут невидимы.
    // Как только получим ответ уберается Relative layout и видны становятся все элементы
    init{
        this.instance = instance
        run("http://data.fixer.io/api/latest?access_key=4353a124be0c1f20ccc43039c18c3d38")
    }

    // Асинхронный запрос апи
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
                // Ответ получен
                success_load(response)
            }
        })
    }

    // Что то обновило кол-во валюты ИЗ которой конвертируем
    // Необходимо пересчитать валюту В которую конвертируем и обновить editText
    fun update_convert_from_count(new_count:Double) {
        count_from = new_count
        count_to = new_count / coeff
        // Обновим EditText кол-во валюты В которую переводим
        instance.eT_convert_to.setText(count_to.toString())
    }

    // Что то обновило кол-во валюты В которую конвертируем
    // Необходимо пересчитать валюту ИЗ которой конвертируем и обновить editText
    fun update_convert_to_count(new_count:Double) {
        count_to = new_count
        count_from = new_count * coeff
        // Обновим EditText кол-во валюты ИЗ которой переводим
        instance.eT_convert_from.setText(count_from.toString())
    }

    // Обновилась валюта ИЗ которой конвертируем
    fun update_currency_from(currency:String) {
        // Обновилась валюта. Надо пересчитать коэффициент
        // И обновить кол-во валюты В которую конверируем для этого
        // У нас есть метод обновления КОЛ-ВА валюты ИЗ который пересчитывает кол-во валюты В
        // Звучит странно но вроде логично. Если изменяется кол-во исходной валюты то обновляем
        // Результирующее кол-во
        currency_from = currency
        update_coeff()
        update_convert_from_count(count_from)
    }

    // Обновилась валюта В которую конвертируем
    fun update_currency_to(currency:String) {
        // Обновилась валюта. Надо пересчитать коэффициент
        // И обновить кол-во валюты ИЗ которой конверируем для этого
        // Подробнее описание см. update_currency_from
        currency_to = currency
        update_coeff()
        update_convert_to_count(count_to)
    }

    // Пересчитаем коэфициент соотношения валют
    fun update_coeff() {
        coeff = rates.getValue(currency_from) / rates.getValue(currency_to)
    }

    // Когда прийдет ответ от API вызовется эта функция
    fun success_load(response: Response) {
        val gson = Gson()
        // response.body может быть использован только один раз
        // Распарсим строку из body согласно классу ResponseFixerIo
        rates = gson.fromJson(response.body?.string(), ResponseFixerIo::class.java).rates

        // Обновлять UI можно только в потоке UI, а мы сейчас в потоке который запрос делал
        instance.runOnUiThread(Runnable {
            // Та махинация с layout чтобы убрать прогресс бар и показать элементы
            instance.layout_with_progress_bar.visibility = View.INVISIBLE
            instance.layout_main.visibility = View.VISIBLE

            // Create an ArrayAdapter using the string array and a default spinner layout
            // Наш список лежит в ключах мапы rates
            val adapter = ArrayAdapter<String>(
                instance.baseContext, android.R.layout.simple_spinner_item, ArrayList(rates.keys)
            )

            // И загрузим их в два спиннера
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            instance.currency_from.adapter = adapter
            instance.currency_to.adapter = adapter

            // Установим по дефолту из USD в RUB
            instance.currency_from.setSelection(adapter.getPosition("USD"))
            instance.currency_to.setSelection(adapter.getPosition("RUB"))
            // Обновим коэффициенты, везде в кол-во сейчас 0
            update_coeff()
            update_convert_from_count(count_from)
        })

    }

}