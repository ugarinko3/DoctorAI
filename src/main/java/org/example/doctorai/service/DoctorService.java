package org.example.doctorai.service;


import lombok.AllArgsConstructor;
import org.example.doctorai.exception.ChatNotFoundException;
import org.example.doctorai.model.entity.Chat;
import org.example.doctorai.model.entity.Message;
import org.example.doctorai.model.enums.Doctor;
import org.example.doctorai.model.request.AnswerGigaChatMessageRequest;
import org.example.doctorai.model.request.MessageRequest;
import org.example.doctorai.repository.ChatRepository;
import org.example.doctorai.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DoctorService {

    private final GigaChatService gigaChatService;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public UUID createChat(Doctor doctor, UUID userId) {
        Chat chat = new Chat();
        chat.setDoctor(doctor);
        chat.getMessages().add(createMessage(doctor.getMessage()));
        chat.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));
        chatRepository.save(chat);
        return chat.getChatId();
    }

    private Message createMessage(String systemMessage) {
        Message message = new Message();
        message.setRole("system");
        message.setContent(systemMessage);
        return message;
    }

    @Transactional
    public Message doctor(MessageRequest rawBody) {
        Chat chat = chatRepository.findById(rawBody.getIdChat())
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        chat.getMessages().add(rawBody.getMessage());
        chat.getMessages().add(gigaChatService.requestDoctorOrthopedist(chat));
        chatRepository.save(chat);
        return chat.getMessages().get(chat.getMessages().size() - 1);
    }

    @Transactional
    public List<Chat> getChatsDoctor(UUID userId, Doctor doctor) {
        return chatRepository.findAllByUserIdAndDoctor(userId, doctor);
    }
}
