package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.CompanyDTO;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.InviteLink;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.NoPermissionException;
import ru.bookingsystem.service.interfaces.CompanyService;
import ru.bookingsystem.service.interfaces.InviteLinkService;
import ru.bookingsystem.repository.InviteLinkRepo;
import ru.bookingsystem.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteLinkServiceImpl implements InviteLinkService {

    private final InviteLinkRepo inviteLinkRepo;
    private final UserService userService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public InviteLink getInviteLink(String name) {

        User user = userService.findByUsername(name);

        if (user.getRole().equals(Role.USER)) throw new NoPermissionException();

        Company company = companyService.findById(user.getCompany().getId());

        InviteLink link = inviteLinkRepo.findByCompanyId(company.getId());

        if (link == null) {
            link = generateLink(company);
        }

        return link;

    }

    @Override
    public InviteLink generateLink(Company company){

        InviteLink link = new InviteLink();
        link.setToken(String.valueOf(UUID.randomUUID()));
        link.setCompany(company);
        link.setExpiresAt(LocalDateTime.now().plusDays(3));

        return link;
    }
}
