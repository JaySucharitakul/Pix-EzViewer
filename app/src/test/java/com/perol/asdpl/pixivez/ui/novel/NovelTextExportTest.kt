package com.perol.asdpl.pixivez.ui.novel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NovelTextExportTest {
    @Test fun exportIncludesMetadataAndTheEntireBody() {
        val raw = "[chapter:第一章]漢字[[rb:漢字>かんじ]][newpage]" + "本文\n".repeat(10000)
        val export = NovelTextExport(123, "作品", "作者", raw)
        val text = export.render()
        assertTrue(text.startsWith("作品\n作者\nhttps://www.pixiv.net/novel/show.php?id=123\n\n"))
        assertTrue(text.contains("漢字(かんじ)"))
        assertTrue(text.endsWith("本文\n".repeat(10000) + "\n"))
        assertFalse(text.contains("[newpage]"))
        assertEquals(text, text.toByteArray(Charsets.UTF_8).toString(Charsets.UTF_8))
    }

    @Test fun fileNamesAreSafeAndDistinctAcrossNovelIds() {
        assertEquals("a_b_c__123.txt", NovelTextExport(123, "a/b:c?", "", "").fileName)
        assertEquals("novel_456.txt", NovelTextExport(456, "...", "", "").fileName)
        assertEquals("novel_789.txt", NovelTextExport(789, "", "", "").fileName)
        val longName = NovelTextExport(1, "あ".repeat(1000), "", "").fileName
        assertTrue(longName.toByteArray(Charsets.UTF_8).size < 255)
    }

    @Test fun plainTextOmitsImagesAndKeepsChapterAndRubyText() {
        assertEquals("\nTitle\nword(reading)link\n\nend", renderNovelText(
            "[chapter:Title][[rb:word>reading]][[jumpuri:link>https://example.com]]" +
                "[pixivimage:1-2][uploadedimage:3][jump:4][newpage]end"
        ))
    }
}
