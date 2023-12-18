package com.example.storyapp

import com.example.storyapp.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photo + $i",
                "name $i",
                "description $i",
                i.toDouble(),
                i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}