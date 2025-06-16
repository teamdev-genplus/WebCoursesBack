package com.aecode.webcoursesback.servicesimplement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aecode.webcoursesback.entities.FreqQuest;
import com.aecode.webcoursesback.repositories.IFreqQuestRepo;
import com.aecode.webcoursesback.services.IFreqQuestService;

@Service
public class FreqQuestServiceImp implements IFreqQuestService {

    @Autowired
    private IFreqQuestRepo fqRepo;

    @Override
    public void insert(FreqQuest freqQuest) {
        fqRepo.save(freqQuest);
    }

    @Override
    public List<FreqQuest> list() {
        return fqRepo.findAll();
    }

    @Override
    public void delete(Long freqQuestId) {
        fqRepo.deleteById(freqQuestId);
    }

    @Override
    public FreqQuest listId(Long freqQuestId) {
        return fqRepo.findById(freqQuestId).orElse(new FreqQuest());
    }

}
