package com.softserveinc.ita.homeproject.homeservice.service.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.samskivert.mustache.Mustache;
import com.softserveinc.ita.homeproject.homedata.repository.UserRepository;
import com.softserveinc.ita.homeproject.homeservice.dto.InvitationDto;
import com.softserveinc.ita.homeproject.homeservice.dto.MailDto;
import com.softserveinc.ita.homeproject.homeservice.exception.InvitationException;
import com.softserveinc.ita.homeproject.homeservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final UserRepository userRepository;

    public static final Path REGISTRATION_TEMPLATE_PATH =
        Path.of("home-data/src/main/resources/template/invitation-to-registration.mustache");

    public static final Path COOPERATION_TEMPLATE_PATH =
        Path.of("home-data/src/main/resources/template/invitation-to-cooperation.mustache");

    private static final Path APARTMENT_TEMPLATE_PATH =
            Path.of("home-data/src/main/resources/template/invitation-to-apartment.mustache");

    @Override
    public String createMessageTextFromTemplate(MailDto mailDto) {
        var text = "";
        try (Reader reader = new StringReader(Files.readString(getInvitationTemplate(mailDto)))) {
            text = Mustache.compiler()
                .compile(reader)
                .execute(mailDto);
        } catch (IOException e) {
            throw new InvitationException(Arrays.toString(e.getStackTrace()));
        }
        return text;
    }

    private Path getInvitationTemplate(MailDto mailDto) {
        Path path;
        if(userRepository.findByEmail(mailDto.getEmail()).isEmpty()) {
            mailDto.setLink(createLink());
            return REGISTRATION_TEMPLATE_PATH;
        }
        switch (mailDto.getType().toString()) {
            case "cooperation":
                path = COOPERATION_TEMPLATE_PATH;
                mailDto.setLink("invitationToCooperationLink");
                break;
            case "apartment":
                path = APARTMENT_TEMPLATE_PATH;
                mailDto.setLink("invitationToApartmentLink");
                break;
            default:
                throw new InvitationException("Wrong invitation type.");
        }
        mailDto.setLink(createLink());
        return path;
    }

    protected String createLink(){
        return "http://localhost:8080/api/0?token=";
    }
}
