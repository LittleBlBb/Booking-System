package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import ru.bookingsystem.DTO.CompanyJoinRequestDTO;
import ru.bookingsystem.entity.constant.RequestStatus;

import java.util.List;

public interface CompanyJoinRequestService {

    List<CompanyJoinRequestDTO> getAllById(String name);

    CompanyJoinRequestDTO joinRequest(Authentication authentication, Long id);

    void approve(Authentication authentication, Long id);

    void reject(Authentication authentication, Long id);

    List<CompanyJoinRequestDTO> getAllByIdAndStatus(String name, RequestStatus status);
}
