package com.xixiaohui.reader.utils

import java.io.Serializable


data class Poetry(var title:String):Serializable {

    var author: String = ""
    var dynasty: String =""
    var text: String=""


}