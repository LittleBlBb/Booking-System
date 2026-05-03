package ru.bookingsystem.service.interfaces;

import jakarta.transaction.Transactional;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.InviteLink;

public interface InviteLinkService {

    InviteLink getInviteLink(String name);

    @Transactional
    InviteLink generateLink(Company company);
}
