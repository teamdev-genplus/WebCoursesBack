package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.entities.StudyPlan;

import java.util.List;

public interface IStudyPlanService {
    public void insert(StudyPlan studyPlan);
    List<StudyPlan> list();
    public void delete(int studyPlanId);
    public StudyPlan listId(int studyPlanId);
}
