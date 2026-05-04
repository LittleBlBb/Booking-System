package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.InviteLink;

public interface InviteLinkRepo extends JpaRepository<InviteLink, Long> {

    InviteLink findByCompanyId(Long id);
}
