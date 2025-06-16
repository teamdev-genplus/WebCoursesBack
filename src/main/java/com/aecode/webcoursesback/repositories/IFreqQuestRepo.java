package com.aecode.webcoursesback.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aecode.webcoursesback.entities.FreqQuest;

@Repository
public interface IFreqQuestRepo extends JpaRepository<FreqQuest, Long> {

}
