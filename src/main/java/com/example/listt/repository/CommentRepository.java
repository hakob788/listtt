package com.example.listt.repository;

import com.example.listt.entity.Comment;
import com.example.listt.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findAllByItem(Item item);

}