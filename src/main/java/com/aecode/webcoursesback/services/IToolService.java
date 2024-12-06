package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.Tool;

import java.util.List;

public interface IToolService {
    public void insert(Tool tool);
    List<Tool> list();
    public void delete(int toolId);
    public Tool listId(int toolId);
}
