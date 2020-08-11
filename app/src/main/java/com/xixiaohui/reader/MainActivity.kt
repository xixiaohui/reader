package com.xixiaohui.reader

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Layout
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xixiaohui.reader.databinding.ActivityMainBinding
import com.xixiaohui.reader.utils.DBHelper
import com.xixiaohui.reader.utils.LocalJsonResolutionUtils
import com.xixiaohui.reader.utils.Poetry
import kotlinx.android.synthetic.main.poetry.view.*
import java.util.*

var mFontSize: Float = 20f

class MainActivity : AppCompatActivity() {

    var allPoetries = mutableListOf<Poetry>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var mydatabase: DBHelper


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.w("MainActivity", "start")
//        this.getPoetries()
//        this.setRecycleViewAdapter()

        mydatabase = DBHelper(this)

        if (mydatabase.getCount() <= 0) {

            recyclerView = binding.poetryList
            var linearLayoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = linearLayoutManager

            val myTask = DBHelper.MyTask(recyclerView, mydatabase, this.allPoetries)
            myTask.execute()
        } else {
            this.allPoetries = mydatabase.getAllData()
            this.setRecycleViewAdapter()
        }
        Log.w("MainActivity", "-----init bottom navigation")
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    true
                }
                R.id.page_2 -> {
                    true
                }
                R.id.page_3 -> {
                    true
                }
                R.id.page_4 -> {

                    val intent: Intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val fontSize = prefs.getInt(getString(R.string.font_size_key), 0)
        mFontSize = fontSize.toFloat()
        Log.i("MainActivity", fontSize.toString())
        Log.w("MainActivity", "end")
    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val fontSize = prefs.getInt(getString(R.string.font_size_key), 0)
        mFontSize = fontSize.toFloat()
        Log.i("onResume", fontSize.toString())
    }


    private fun getPoetries() {
        var fileName = "中学古诗.json"
        var poetriesString =
            LocalJsonResolutionUtils.getJson(context = baseContext, fileName = fileName)

        var poetriesStringTemp = poetriesString.replace("}{", "}7777777{")
        var poetriesArray = poetriesStringTemp.split("7777777")

        poetriesArray.forEach {
            var poe = LocalJsonResolutionUtils.jsonToObject(it, Poetry::class.java)
            allPoetries.add(poe)
        }
    }

    fun setRecycleViewAdapter() {
        val adapter = PoetryAdapter()

        adapter.setOnItemClickListener(object:PoetryAdapter.OnItemClickListener{
            override fun onItemClick(
                parent: RecyclerView,
                view: View,
                position: Int,
                data: Poetry
            ) {
//               Toast.makeText(this@MainActivity,data.title,Toast.LENGTH_LONG).show()
                val intent:Intent = Intent(this@MainActivity,ContentActivity::class.java)
                intent.putExtra("poetry",data)
                startActivity(intent)
            }
        })

        recyclerView = binding.poetryList
        recyclerView.adapter = adapter

        var linearLayoutManager = LinearLayoutManager(this)
//        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager
        adapter.data = this.allPoetries
    }

}

class PoetryItemViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout) {

}

class PoetryAdapter() : RecyclerView.Adapter<PoetryItemViewHolder>(),View.OnClickListener{

    private lateinit var recyclerView: RecyclerView

    var data = mutableListOf<Poetry>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoetryItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.poetry, parent, false) as LinearLayout

        view.setOnClickListener(this)
        return PoetryItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PoetryItemViewHolder, position: Int) {
        val item = data[position]

        var title: TextView = holder.linearLayout.getChildAt(0) as TextView
        title.text = item.title.toString()

        var author: TextView = holder.linearLayout.getChildAt(1) as TextView
        author.text = item.author.toString() + " · " + item.dynasty.toString()

        var poetryText: TextView = holder.linearLayout.getChildAt(2) as TextView

        var text: String = item.text.toString()
//        if (text.length > 33) {
//            text = text.substring((0..32))
//            text += "..."
//        }
        poetryText.text = text


        poetryText.textSize = mFontSize
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    interface OnItemClickListener{
        fun onItemClick(parent: RecyclerView,view:View,position: Int,data:Poetry)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }


    override fun onClick(v: View) {
        var position = this.recyclerView.getChildAdapterPosition(v)

        this.onItemClickListener!!.onItemClick(this.recyclerView,v,position, this.data[position])
    }
}