package com.project.icey.app.repository;

import com.project.icey.app.domain.Card;
import com.project.icey.app.domain.Letter;
import com.project.icey.app.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {

}