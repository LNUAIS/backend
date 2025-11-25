package com.backend.lnuais_backend.config;

import com.backend.lnuais_backend.model.Event;
import com.backend.lnuais_backend.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(EventRepository eventRepository) {
        return args -> {
            // Only add data if the table is empty
            if (eventRepository.count() == 0) {
                System.out.println("🌱 Seeding Events Database...");

                Event e1 = new Event(
                    "AI Workshop: Neural Networks",
                    "A beginner-friendly introduction to how Neural Networks work. Laptop required.",
                    "Building M, Room 204",
                    LocalDateTime.now().plusDays(2).withHour(14).withMinute(0), // 2 days from now, 2:00 PM
                    LocalDateTime.now().plusDays(2).withHour(16).withMinute(0)
                );

                Event e2 = new Event(
                    "Guest Lecture: AI in Healthcare",
                    "Dr. Smith discusses the impact of AI on modern diagnostics.",
                    "Online (Zoom)",
                    LocalDateTime.now().plusDays(5).withHour(10).withMinute(0),
                    LocalDateTime.now().plusDays(5).withHour(11).withMinute(30)
                );

                eventRepository.save(e1);
                eventRepository.save(e2);
                
                System.out.println("✅ Events added!");
            }
        };
    }
}