package org.example.doctorai.controller;

import lombok.AllArgsConstructor;
import org.example.doctorai.model.entity.Chat;
import org.example.doctorai.model.entity.Message;
import org.example.doctorai.model.enums.Doctor;
import org.example.doctorai.model.request.MessageRequest;
import org.example.doctorai.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/create-сhat")
    public ResponseEntity<UUID> createChat(@RequestParam Doctor doctor, @RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createChat(doctor, userId));
    }

    @PostMapping
    public ResponseEntity<Message> doctor(@RequestBody MessageRequest rawBody) {
        return ResponseEntity.ok(doctorService.doctor(rawBody));
    }

    @GetMapping
    public ResponseEntity<List<Chat>> getChatsDoctor(@RequestParam UUID userId, @RequestParam Doctor doctor) {
        return ResponseEntity.ok(doctorService.getChatsDoctor(userId, doctor));
    }
}
