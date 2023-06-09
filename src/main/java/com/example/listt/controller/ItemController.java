package com.example.listt.controller;

import com.example.listt.entity.Category;
import com.example.listt.entity.Comment;
import com.example.listt.entity.Item;
import com.example.listt.repository.CategoryRepository;
import com.example.listt.repository.CommentRepository;
import com.example.listt.repository.ItemRepository;
import com.example.listt.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Value("${listam.upload.image.path}")
    private String imageUploadPath;

    @GetMapping
    public String itemsPage(ModelMap modelMap) {
        List<Item> all = itemRepository.findAll();
        modelMap.addAttribute("items", all);
        return "items";
    }

    @GetMapping("/{id}")
    public String singleItemPage(@PathVariable("id") int id, ModelMap modelMap) {
        Optional<Item> byId = itemRepository.findById(id);
        if (byId.isPresent()) {
            Item item = byId.get();
            List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());
            modelMap.addAttribute("item", item);
            modelMap.addAttribute("comments", comments);
            return "singleItem";
        } else {
            return "redirect:/items";
        }
    }

    @GetMapping("/add")
    public String itemsAddPage(ModelMap modelMap) {
        List<Category> all = categoryRepository.findAll();
        modelMap.addAttribute("categories", all);
        return "addItem";
    }

    @PostMapping("/add")
    public String itemsAdd(@ModelAttribute Item item,
                           @RequestParam("image") MultipartFile multipartFile,
                           @AuthenticationPrincipal CurrentUser currentUser
    ) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
            item.setImgName(fileName);
        }
        item.setUser(currentUser.getUser());
        itemRepository.save(item);
        return "redirect:/items";
    }

    @GetMapping("/remove")
    public String removeCategory(@RequestParam("id") int id) {
        itemRepository.deleteById(id);
        return "redirect:/items";
    }

    @PostMapping("/comment/add")
    public String addComment(@ModelAttribute Comment comment,
                             @AuthenticationPrincipal CurrentUser currentUser) {
        comment.setCommentDate(new Date());
        comment.setUser(currentUser.getUser());
        commentRepository.save(comment);
        return "redirect:/items/" + comment.getItem().getId();
    }
}