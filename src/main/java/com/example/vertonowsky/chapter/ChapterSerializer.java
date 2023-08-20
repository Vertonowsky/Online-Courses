package com.example.vertonowsky.chapter;

import com.example.vertonowsky.topic.TopicSerializer;
import org.hibernate.Hibernate;

public class ChapterSerializer {

    public static ChapterDto serialize(Chapter chapter) {
        ChapterDto dto = new ChapterDto();
        dto.setId(chapter.getId());
        dto.setIndex(chapter.getIndex());
        dto.setTitle(chapter.getTitle());

        if (Hibernate.isInitialized(chapter.getTopics()) && chapter.getTopics() != null)
            dto.setTopics(chapter.getTopics().stream().map(TopicSerializer::serialize).toList());

        return dto;
    }

}
