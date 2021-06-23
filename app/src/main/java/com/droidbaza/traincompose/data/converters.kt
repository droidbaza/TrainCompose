package com.droidbaza.traincompose.data

fun LatestNewsResponse.toData(currentArticles: List<NewsItem> = emptyList()) =
    LatestData(currentArticles + articles.flatten().filter { it.type == "Article" }
        .map { it.toNewItem() }, links.next)

fun Article.toNewItem() = NewsItem(
    title?.value ?: "",
    if (mainResource?.type == "Image") "https://gfx-android.omni.se/images/${mainResource.imageAsset?.id}" else "",
    changes?.modified ?: changes?.published ?: "",
    category?.title ?: "",
    if (mainResource?.type == "Image") mainResource.caption?.value ?: "" else ""
)