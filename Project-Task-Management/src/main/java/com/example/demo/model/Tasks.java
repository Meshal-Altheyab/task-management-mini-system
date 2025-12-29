package com.example.demo.model;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data  // يسوي قيت و سيت لكل الحقول	
@NoArgsConstructor //  يسوي كونسيركتور فاضي  
@AllArgsConstructor	// يسوي كونسيركتور بكل الحقول
@Table(name = "Task")
public class Tasks {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private String description;
	
	@Enumerated(EnumType.STRING) // يخزن قيمة ينم كنص في قاعدة البيانات
	private TaskStatus status; 
	private LocalDateTime created_at; 
	
	
	@ManyToOne // علاقة كثير الى واحد بين التاسك و اليوزر
	@JoinColumn(name = "user_id") // اسم العمود في جدول التاسك اللي يشير الى اليوزر
	private User  user; // كل التاسك مرتبط بيوزر واحد	
	

}
