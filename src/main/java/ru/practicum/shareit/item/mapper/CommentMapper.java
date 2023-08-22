package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.DetailedCommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment toModel(CreateCommentDto dto, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static DetailedCommentDto toCommentDetailedDto(Comment comment) {
        DetailedCommentDto dto = new DetailedCommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public static List<DetailedCommentDto> toCommentDetailedDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDetailedDto)
                .collect(Collectors.toList());
    }
}
