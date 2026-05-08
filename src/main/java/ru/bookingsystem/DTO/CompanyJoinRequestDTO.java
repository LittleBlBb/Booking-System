package ru.bookingsystem.DTO;

import lombok.Data;
import ru.bookingsystem.entity.CompanyJoinRequest;
import ru.bookingsystem.entity.constant.RequestStatus;

import java.time.LocalDateTime;

@Data
public class CompanyJoinRequestDTO {

    private Long id;
    private Long companyId;
    private Long userId;
    private String username;
    private RequestStatus status;
    private LocalDateTime createdAt;

    public CompanyJoinRequestDTO(CompanyJoinRequest request){

        this.id = request.getId();
        this.companyId = request.getCompany().getId();
        this.userId = request.getUser().getId();
        this.username = request.getUser().getUsername();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
    }
}
