package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bookingsystem.entity.InviteLink;
import ru.bookingsystem.service.interfaces.InviteLinkService;

@Tag(name = "invite_link_methods", description = "operations with invite link")
@RestController
@RequestMapping("/api/invite")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InviteLinkController {

    private final InviteLinkService inviteLinkService;

    @GetMapping("/getInviteLink")
    public InviteLink getInviteLink(Authentication authentication){

        return inviteLinkService.getInviteLink(authentication.getName());
    }
}
