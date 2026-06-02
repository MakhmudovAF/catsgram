package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.enums.SortOrder;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(@RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(defaultValue = "desc") String sort,
                                    @RequestParam(defaultValue = "0") Long from) {
        SortOrder sortOrder = SortOrder.from(sort);
        return postService.findAll(size, sortOrder, from);
    }

    @GetMapping("/{id}")
    public Post findPostById(@PathVariable Long id) {
        return postService.findPostById(id).orElseThrow(() -> new ConditionsNotMetException(
                String.format("Пост с id = %d не найден", id)
        ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}