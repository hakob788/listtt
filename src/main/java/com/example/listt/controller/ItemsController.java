package com.example.listt.controller;

import com.example.listt.entity.Category;
import com.example.listt.entity.Comment;
import com.example.listt.entity.Item;
import com.example.listt.repository.CategoryRepository;
import com.example.listt.repository.CommentRepository;
import com.example.listt.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ItemsController {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/items")
    public String items(ModelMap modelMap) {
        List<Item> all = itemRepository.findAll();
        modelMap.addAttribute("items", all);
        return "items";
    }

    @GetMapping("/items/{id}")
    public String singleItemPage(@PathVariable("id") int id, ModelMap modelMap) {
        Optional<Item> byId = itemRepository.findById(id);
        if (byId.isPresent()) {
            Item item = byId.get();
            List<Comment> allByItem = commentRepository.findAllByItem(item);
            modelMap.addAttribute("item", item);
            modelMap.addAttribute("comments", allByItem);
            return "singleItem";
        } else
            return "redirect:/items";
    }

    @GetMapping("/items/add")
    public String itemAddPage(ModelMap modelMap) {
        List<Category> all = categoryRepository.findAll();
        modelMap.addAttribute("categories", all);
        return "addItem";
    }

    @PostMapping("/items/add")
    public String itemAdd(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "redirect:/items";
    }

    @GetMapping("/items/remove")
    public String removeItem(@RequestParam(name = "id") int id) {
        itemRepository.deleteById(id);
        return "redirect:/items";
    }

}