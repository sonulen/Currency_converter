# Конвертер валюты

![](https://github.com/sonulen/Currency_converter/workflows/Android%20CI/badge.svg)

Берет данные из [api](https://fixer.io/), парсит и обрабатывает.

Библиотеки:

* okHTTP3 - в качестве http клиента
* GSON - для парсинга JSON файла

# Описание работы

Пока данных из api не полученно отображается экран загрузки:

<p align="center">
  <img height="700px" src=pics/load.jpg/>
</p>

Как только данные были полученны выводится UI:

<p align="center">
  <img height="800px" src=pics/converter.jpg/>
</p>
