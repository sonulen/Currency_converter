package com.example.currency_converter

// Класс для GSON указывающий как парсить JSON ответ в HTTP.BODY
class ResponseFixerIo {
    internal var success: String? = null
    internal var timestamp: Int? = null
    internal var base: String? = null
    internal var date: String? = null
    lateinit var rates: Map<String, Double>
}

