package com.backend.lnuais_backend.controller;

import com.backend.lnuais_backend.model.Event;
import com.backend.lnuais_backend.services.EventService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping("/{eventId}/register/{userId}")
    public String register(@PathVariable Long eventId, @PathVariable Long userId) {
        eventService.registerUserForEvent(userId, eventId);
        return "Registered successfully";
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public String unregister(@PathVariable Long eventId, @PathVariable Long userId) {
        eventService.unregisterUserFromEvent(userId, eventId);
        return "Unregistered successfully";
    }
    
    @GetMapping("/{eventId}/is_registered/{userId}")
    public boolean checkRegistration(@PathVariable Long eventId, @PathVariable Long userId) {
        return eventService.isUserRegistered(userId, eventId);
    }
}