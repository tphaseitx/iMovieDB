package com.imawo.imoviedb.Helpers

import com.imawo.imoviedb.Models.ModelFilme

object Globals {
    var clickedItemIndex: Int = 0
    var id: Int = 0
    var jsonIP: String = ""
    var jsonPath: String = ""
    var jsonAPI: String = ""
    var jsonParams: String = ""
    var jsonUrl: String = ""
    var searchString: String = ""
    lateinit var listTopFilmeGetList: MutableList<ModelFilme>
}