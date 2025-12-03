package com.example.demo.repo;
import com.example.demo.model.Tasks;
import com.example.demo.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepo extends JpaRepository<Tasks, Integer> {
	
	List<Tasks> findByUser(User user); // للبحث عن كل التاسكات المرتبطة بيوزر معين	

}
