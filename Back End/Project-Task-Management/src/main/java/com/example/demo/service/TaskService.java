package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Tasks;
import com.example.demo.model.TaskDto;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.repo.TaskRepo;
import com.example.demo.repo.UserRepo;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private UserRepo userRepo;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //    	// يجيب لنا ال الصلاحيه اللي حطيناه في الفلتير بعد ما تحقق من التوكن

        // يجيب اسم المستخدم من الصلاحيه	
        String username = auth.getName(); 
        // يجيب اليوزر من قاعدة البيانات بناء على اسم المستخدم	
        User user = userRepo.findByUsername(username);
        // التحقق من وجود المستخدم	
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }
    
    public Tasks createTask(TaskDto dto) {

        User user = getCurrentUser();

        Tasks task = new Tasks();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());

        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        } else {
            task.setStatus(TaskStatus.NEW);
        }

        task.setCreated_at(LocalDateTime.now());
        task.setUser(user); 

        return taskRepo.save(task);
    }
    
    // جلب المهام الخاصة بالمستخدم الحالي	
    public List<Tasks> getMyTasks() {

        User user = getCurrentUser();
        return taskRepo.findByUser(user);
    }
    
    // جلب كل المهام (للمسؤول فقط)	
    public List<Tasks> getAllTasks() {

        return taskRepo.findAll();
    }
    
    
    public Tasks updateTask(Integer id, TaskDto dto) {

        Tasks task = taskRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // التحقق من أن المستخدم الحالي هو مالك المهمة أو مسؤول	
        User currentUser = getCurrentUser();
        
        
        boolean isOwner = task.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Not allowed to update this task");
        }
        
        
        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }

        return taskRepo.save(task);
    }

    public void deleteTask(Integer id) {

        Tasks task = taskRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User currentUser = getCurrentUser();

        boolean isOwner = task.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Not allowed to delete this task");
        }

        taskRepo.delete(task);
    }
}
