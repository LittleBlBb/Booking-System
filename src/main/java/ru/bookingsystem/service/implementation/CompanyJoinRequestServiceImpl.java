package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.CompanyJoinRequest;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.RequestStatus;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.AlreadyInCompanyException;
import ru.bookingsystem.exception.NoPermissionException;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.CompanyJoinRequestRepo;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.interfaces.CompanyJoinRequestService;
import ru.bookingsystem.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyJoinRequestServiceImpl implements CompanyJoinRequestService {

    private final CompanyJoinRequestRepo companyJoinRequestRepo;
    private final UserRepo userRepo;
    private final CompanyRepo companyRepo;

    @Override
    public List<CompanyJoinRequest> getAllById(String name) {

        User user = userRepo.findByUsername(name).orElseThrow(() ->
                new NotFoundException("User " + name + " not found"));

        if (!user.getRole().equals(Role.ADMIN)){
            throw new NoPermissionException();
        }

        Company company = companyRepo.findById(user.getCompany().getId()).orElseThrow(() ->
                new NotFoundException("Company with id " + user.getCompany().getId() + " not found"));

        return companyJoinRequestRepo.findAllByCompany(company);
    }

    @Override
    @Transactional
    public CompanyJoinRequest joinRequest(Authentication authentication, Long id) {

        User user = userRepo.findByUsername(authentication.getName()).orElseThrow(() ->
                new NotFoundException("User " + authentication.getName() + " not found"));

        if (user.getCompany() != null) {
            throw new AlreadyInCompanyException("user with id " + user.getId() + " already in company");
        }

        Company company = companyRepo.findById(id).orElseThrow(()->
                new NotFoundException("company with id " + id + " not found"));

        List<CompanyJoinRequest> requests = companyJoinRequestRepo.findByUserAndCompany(user, company);
        for (CompanyJoinRequest request : requests){
            if (request.getStatus().equals(RequestStatus.PENDING)) throw new AlreadyInCompanyException("You have already sent a request to this company");
        }

        CompanyJoinRequest request = new CompanyJoinRequest();
        request.setUser(user);
        request.setCompany(company);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return companyJoinRequestRepo.save(request);
    }

    @Override
    @Transactional
    public void approve(Authentication authentication, Long id) {

        User admin = userRepo.findByUsername(authentication.getName()).orElseThrow(() ->
                new NotFoundException("User " + authentication.getName()  + " not found"));

        if (!admin.getRole().equals(Role.ADMIN)) throw new NoPermissionException();

        CompanyJoinRequest request = companyJoinRequestRepo.findById(id).orElseThrow(() ->
                new NotFoundException("join request with " + id  + " not found"));

        User user = userRepo.findById(request.getUser().getId()).orElseThrow(() ->
                new NotFoundException("User " + request.getUser().getId()  + " not found"));

        if (user.getCompany() != null) throw new AlreadyInCompanyException("this user already in company");

        if (admin.getCompany().getId() != request.getCompany().getId()) throw new NoPermissionException();

        request.setStatus(RequestStatus.APPROVED);

        user.setCompany(request.getCompany());

        userRepo.save(user);

        companyJoinRequestRepo.save(request);
    }

    @Override
    @Transactional
    public void reject(Authentication authentication, Long id) {

        User admin = userRepo.findByUsername(authentication.getName()).orElseThrow(() ->
                new NotFoundException("User " + authentication.getName()  + " not found"));

        if (!admin.getRole().equals(Role.ADMIN)) throw new NoPermissionException();

        CompanyJoinRequest request = companyJoinRequestRepo.findById(id).orElseThrow(() ->
                new NotFoundException("join request with " + id  + " not found"));

        if (admin.getCompany().getId() != request.getCompany().getId()) throw new NoPermissionException();

        request.setStatus(RequestStatus.REJECTED);

        companyJoinRequestRepo.save(request);
    }
}
