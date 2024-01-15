package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(Long id, String text, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;

        if (created == null) {
            this.created = LocalDateTime.now();
        } else {
            this.created = created;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm.");
        String formattedTime = this.created.format(formatter);
        this.created = LocalDateTime.parse(formattedTime, formatter);
    }

}
