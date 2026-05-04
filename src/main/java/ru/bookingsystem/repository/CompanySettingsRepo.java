package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.CompanySettings;

public interface CompanySettingsRepo extends JpaRepository<CompanySettings, Long> {
    CompanySettings findCompanySettingsByCompanyId(Long companyId);
}
