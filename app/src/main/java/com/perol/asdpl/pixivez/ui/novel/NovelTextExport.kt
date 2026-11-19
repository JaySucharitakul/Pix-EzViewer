package com.perol.asdpl.pixivez.ui.novel

/** A single-novel snapshot; TXT exports intentionally omit illustrations. */
data class NovelTextExport(
    val id: Int,
    val title: String,
    val author: String,
    val rawText: String,
) {
    val fileName: String
        get() {
            val safeTitle = title.map { c ->
                if (c.isISOControl() || c in "\\/:*?\"<>|") '_' else c
            }.joinToString("").trim().trim('.').take(60)
            return if (safeTitle.isBlank()) "novel_$id.txt" else "${safeTitle}_$id.txt"
        }

    fun render(): String = buildString {
        appendLine(title.ifBlank { "novel_$id" })
        appendLine(author)
        appendLine("https://www.pixiv.net/novel/show.php?id=$id")
        appendLine()
        append(renderNovelText(rawText))
        appendLine()
    }
}

// pixiv 正文自有标记 → 纯文本(导出/纯文字场景用;阅读页走 NovelMarkup 图文管线)
fun renderNovelText(raw: String): String =
    raw
        .replace(Regex("""\[newpage\]"""), "\n\n")
        .replace(Regex("""\[chapter:(.*?)\]"""), "\n$1\n")
        .replace(Regex("""\[\[rb:(.*?)>(.*?)\]\]"""), "$1($2)")
        .replace(Regex("""\[\[jumpuri:(.*?)>.*?\]\]"""), "$1")
        .replace(Regex("""\[pixivimage:[^\]]*\]"""), "")
        .replace(Regex("""\[uploadedimage:[^\]]*\]"""), "")
        .replace(Regex("""\[jump:[^\]]*\]"""), "")
