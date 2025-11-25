package com.backend.lnuais_backend.services;

import com.backend.lnuais_backend.model.Event;
import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.repository.EventRepository;
import com.backend.lnuais_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Seed some dummy events if empty (Optional helper)
    public void seedEvents() {
        if (eventRepository.count() == 0) {
            // You can add logic here to create default events
        }
    }

    public void registerUserForEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException("Event not found"));

        user.getRegisteredEvents().add(event);
        event.getAttendees().add(user);
        
        userRepository.save(user);
        eventRepository.save(event);
    }

    public void unregisterUserFromEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException("Event not found"));

        user.getRegisteredEvents().remove(event);
        event.getAttendees().remove(user);

        userRepository.save(user);
        eventRepository.save(event);
    }
    
    // Check if user is registered
    public boolean isUserRegistered(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        // We iterate to check IDs because Java Set comparison can be tricky with Hibernate proxies
        return user.getRegisteredEvents().stream().anyMatch(e -> e.getId().equals(eventId));
    }
}