package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "comment_entity-graph")
    List<Comment> findByItem_Id(Long itemId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "comment_entity-graph")
    List<Comment> findByItem_IdIn(List<Long> itemIds);

}
