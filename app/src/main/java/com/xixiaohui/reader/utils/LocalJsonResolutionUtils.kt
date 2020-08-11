package com.xixiaohui.reader.utils

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.gson.Gson
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder


open class LocalJsonResolutionUtils(name: String) {
    val firstProperty = "First property: $name".also(::println)

    init {
        println("First initializer block that prints ${name}")
    }

    val secondProperty = "Second property: ${name.length}".also(::println)

    init {
        println("Second initializer block that prints ${name.length}")
    }

    constructor(name: String, age: Int) : this(name) {
        println("$name is ${age} years old.")
    }

    companion object {
        fun getJson(context: Context, fileName: String): String {
            var stringBuilder: StringBuilder = StringBuilder()
            //获取assets 资源管理器
            val assetManager: AssetManager = context.assets

            //使用IO流读取json文件内容
            try {
                var bufferReader: BufferedReader =
                    BufferedReader(InputStreamReader(assetManager.open(fileName), "utf-8"))

                var line: String?=""

                while (line != null) {
                    line = bufferReader.readLine()
                    if (line != null){
                        stringBuilder.append(line)
                    }

//                    if (line !=null){
//                        Log.w("LocalJson",line)
//                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return stringBuilder.toString()
        }

        fun <T> jsonToObject(json: String,type: Class<T> ): T{
            val gson = Gson()
            return gson.fromJson(json,type)
        }
    }

}

class Derived(name: String) : LocalJsonResolutionUtils(name) {

}

fun <T> asList(vararg ts: T): List<T> {
    val result = ArrayList<T>()
    for (t in ts) {
        result.add(t)
    }
    return result
}

fun chunkedTest() {
    var numbers = (0..13).toList()
    println(numbers.chunked(3))
}


fun main() {

//    val a: Int = 10000

//    val ob = LocalJsonResolutionUtils("Jack",15)


//    println(ob.toString())
//    var res = asList(1,2,3,"how","are","you.")
//    res.forEach{
//        println(it)
//    }
//    chunkedTest()

//    throw Exception("hi there.")


}

