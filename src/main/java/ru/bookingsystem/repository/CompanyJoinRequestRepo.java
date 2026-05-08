package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.CompanyJoinRequest;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.RequestStatus;

import java.util.Arrays;
import java.util.List;

public interface CompanyJoinRequestRepo extends JpaRepository<CompanyJoinRequest, Long> {

    List<CompanyJoinRequest> findByUserIdAndCompanyId(Long userId, Long companyId);

    void deleteAllByUserId(Long id);

    List<CompanyJoinRequest> findAllByCompanyId(Long id);

    List<CompanyJoinRequest> findAllByCompanyIdAndStatus(Long id, RequestStatus status);
}
