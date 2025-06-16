package com.aecode.webcoursesback.services;

import java.util.List;

import com.aecode.webcoursesback.entities.Tag;

public interface ICourseTagService {
    public void insert(Tag tag);

    List<Tag> list();

    public void delete(int tagId);

    public Tag listById(int tagId);
}
