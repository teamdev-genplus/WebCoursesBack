package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.StudyPlan;
import com.aecode.webcoursesback.repositories.IStudyPlanRepo;
import com.aecode.webcoursesback.services.IStudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyPlanServiceImp implements IStudyPlanService {

    @Autowired
    private IStudyPlanRepo spR;
    @Override
    public void insert(StudyPlan studyPlan) {
        spR.save(studyPlan);
    }

    @Override
    public List<StudyPlan> list() {
        return spR.findAll();
    }

    @Override
    public void delete(int studyPlanId) {
        spR.deleteById(studyPlanId);
    }

    @Override
    public StudyPlan listId(int studyPlanId) {
        return spR.findById(studyPlanId).orElse(new StudyPlan());
    }
}
