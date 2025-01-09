package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.Tool;
import com.aecode.webcoursesback.repositories.IToolRepo;
import com.aecode.webcoursesback.services.IToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolServiceImp implements IToolService {

    @Autowired
    private IToolRepo tR;

    @Override
    public void insert(Tool tool) {
        tR.save(tool);
    }

    @Override
    public List<Tool> list() {
        return tR.findAll();
    }

    @Override
    public void delete(int toolId) {
        tR.deleteById(toolId);
    }

    @Override
    public Tool listId(int toolId) {
        return tR.findById(toolId).orElse(new Tool());
    }
}
