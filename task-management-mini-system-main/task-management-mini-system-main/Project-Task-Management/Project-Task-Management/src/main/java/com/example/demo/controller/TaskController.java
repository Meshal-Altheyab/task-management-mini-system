package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Tasks;
import com.example.demo.model.TaskDto;
import com.example.demo.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public Tasks createTask(@RequestBody TaskDto dto) { 
        return taskService.createTask(dto);
    }

    @GetMapping("/my")
    public List<Tasks> getMyTasks() {
        return taskService.getMyTasks();
    }
    
    // فقط اليوزر اللي عنده دور أدمن يقدر يشوف كل التاسكات	
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<Tasks> getAllTasks() {
        return taskService.getAllTasks();
    }
    


    @PutMapping("/{id}")
    public Tasks updateTask(@PathVariable Integer id,
                           @RequestBody TaskDto dto) {
        return taskService.updateTask(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return "Task deleted";
    }
}
