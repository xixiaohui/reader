package com.xixiaohui.reader


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.jaeger.library.OnSelectListener
import com.jaeger.library.SelectableTextHelper
import com.xixiaohui.reader.databinding.ActivityContentBinding
import com.xixiaohui.reader.utils.Poetry


class ContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentBinding
    private lateinit var poetry: Poetry

    private lateinit var mTvTest: TextView
    private lateinit var mSelectableTextHelper: SelectableTextHelper


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_content)

        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var data: Poetry = intent.getSerializableExtra("poetry") as Poetry
        poetry = data

        binding.pTitle.text = poetry.title


        binding.pAuthor.text = poetry.author + "·" + poetry.dynasty
        binding.pText.text = poetry.text

        mTvTest = binding.pText

        mSelectableTextHelper = SelectableTextHelper.Builder(mTvTest)
            .setSelectedColor(
                resources.getColor(
                    R.color.yellow,
                    null
                )
            )
            .setCursorHandleSizeInDp(20f)
            .setCursorHandleColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
            .build()

        mSelectableTextHelper.setSelectListener(object : OnSelectListener {
            override fun onTextSelected(content: CharSequence?) {
                Log.i(this.toString(),content.toString())
            }
        })

    }


}