package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import ru.bookingsystem.entity.CompanyJoinRequest;

import java.util.List;

public interface CompanyJoinRequestService {

    List<CompanyJoinRequest> getAllById(String name);

    CompanyJoinRequest joinRequest(Authentication authentication, Long id);

    void approve(Authentication authentication, Long id);

    void reject(Authentication authentication, Long id);
}
