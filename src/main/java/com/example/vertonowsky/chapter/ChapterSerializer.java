package com.example.vertonowsky.chapter;

public class ChapterSerializer {

    public static ChapterDto serialize(Chapter chapter) {
        ChapterDto dto = new ChapterDto();
        dto.setId(chapter.getId());
        dto.setIndex(chapter.getIndex());
        dto.setTitle(chapter.getTitle());
        return dto;
    }

}
