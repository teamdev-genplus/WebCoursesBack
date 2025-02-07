package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.FreqQuest;

import java.util.List;

public interface IFreqQuestService {

    public void insert(FreqQuest freqQuest);

    List<FreqQuest> list();

    public void delete(int freqQuestId);

    public FreqQuest listId(int freqQuestId);
}
