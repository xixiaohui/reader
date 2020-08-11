package com.xixiaohui.reader.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.xixiaohui.reader.MainActivity
import com.xixiaohui.reader.PoetryAdapter


@RequiresApi(Build.VERSION_CODES.P)
class DBHelper(
    val context: Context
    ) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        const val DATABASE_VERSION = 9
        const val DATABASE_NAME = "Poetry.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${PoetryDataBase.PoeryEntry.TABLE_NAME} (" +
                    "${PoetryDataBase.PoeryEntry.COLUMN_NAME_TITLE} TEXT," +
                    "${PoetryDataBase.PoeryEntry.COLUMN_NAME_AUTHOR} TEXT," +
                    "${PoetryDataBase.PoeryEntry.COLUMN_NAME_DYNAMIC} TEXT," +
                    "${PoetryDataBase.PoeryEntry.COLUMN_NAME_TEXT} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${PoetryDataBase.PoeryEntry.TABLE_NAME}"
    }


    override fun onCreate(db: SQLiteDatabase) {
        Log.i("DBHelper","----------------->start")
        db.execSQL(SQL_CREATE_ENTRIES)
        Log.i("DBHelper","----------------->end")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db,oldVersion,newVersion)
    }

    /**
     * 插入数据
     */
    fun insertPoetry(title:String,author:String,dynasty:String,text:String):Boolean{

        val db = this.writableDatabase
        val contentValues: ContentValues = ContentValues()
        contentValues.put("title",title)
        contentValues.put("author",author)
        contentValues.put("dynasty",dynasty)
        contentValues.put("text",text)
        db.insert(PoetryDataBase.PoeryEntry.TABLE_NAME,null,contentValues)
        return true
    }

    /**
     * 读取数据
     */
    fun getAllData():MutableList<Poetry>{
        var poetries = mutableListOf<Poetry>()

        val db = this.readableDatabase
        var res:Cursor = db.rawQuery("select * from poetry",null)
        res.moveToFirst()
        while (res.isAfterLast() == false){
            val poetry:Poetry = Poetry("")
            val title = res.getString(res.getColumnIndex(PoetryDataBase.PoeryEntry.COLUMN_NAME_TITLE))
            val author = res.getString(res.getColumnIndex(PoetryDataBase.PoeryEntry.COLUMN_NAME_AUTHOR))
            val dynasty = res.getString(res.getColumnIndex(PoetryDataBase.PoeryEntry.COLUMN_NAME_DYNAMIC))
            val text = res.getString(res.getColumnIndex(PoetryDataBase.PoeryEntry.COLUMN_NAME_TEXT))

            poetry.title = title
            poetry.author = author
            poetry.dynasty = dynasty
            poetry.text = text

            poetries.add(poetry)
            res.moveToNext()
        }
        return poetries
    }

    /**
     * 读取all_one.json里的所有古诗
     * 写入到SQLite数据库
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun readAllOneJsonFileToDatabase(fileName:String = "all_one_b.json"){
        var poetriesString =
            LocalJsonResolutionUtils.getJson(context = context, fileName = fileName)

        var poetriesStringTemp = poetriesString.replace("}{", "}7777777{")
        var poetriesArray = poetriesStringTemp.split("7777777")

        poetriesArray.forEach{
            var poe = LocalJsonResolutionUtils.jsonToObject(it, Poetry::class.java)
            this.insertPoetry(poe.title,poe.author,poe.dynasty,poe.text)
        }
        Log.i("readAllOneJsonFile","------------------------->end")
    }

    fun getCount():Int{
        val db = this.readableDatabase
        val resultSet: Cursor =
            db.rawQuery("Select * from poetry", null)
        return resultSet.count
    }

    class MyTask(var recyclerView: RecyclerView, var dbHelper:DBHelper, var allPoetries:MutableList<Poetry>):AsyncTask<String,Void,MutableList<Poetry>>(){
        //子线程中执行
        override fun doInBackground(vararg fileName: String) :MutableList<Poetry>{

            dbHelper.readAllOneJsonFileToDatabase()

            return dbHelper.getAllData()
        }

        //主线程中执行
        override fun onPreExecute() {
            super.onPreExecute()
        }

        //doInBackground中返回的数据结果用于更新UI
        override fun onPostExecute(result: MutableList<Poetry>) {
            super.onPostExecute(result)
            allPoetries = result
            val adapter = PoetryAdapter()
            adapter.data = allPoetries
            recyclerView.adapter = adapter
        }

        //主线程中执行
        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }
    }
}

object PoetryDataBase{

    private const val SQL_CREATE_ENTRIES = ""

    object PoeryEntry{
        const val TABLE_NAME = "poetry"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_AUTHOR = "author"
        const val COLUMN_NAME_DYNAMIC = "dynasty"
        const val COLUMN_NAME_TEXT = "text"
    }
}

