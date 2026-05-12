package com.hei.school.tessaproject.repository;

import com.hei.school.tessaproject.domain.Account;
import com.hei.school.tessaproject.domain.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByProviderAndProviderIdIgnoreCase(Provider provider, String providerId);
}
