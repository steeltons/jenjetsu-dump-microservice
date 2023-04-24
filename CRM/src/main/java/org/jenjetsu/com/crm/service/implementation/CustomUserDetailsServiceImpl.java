package org.jenjetsu.com.crm.service.implementation;

import jakarta.persistence.EntityNotFoundException;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.repository.AbonentRepository;
import org.jenjetsu.com.crm.entity.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * <h2>Custom user details service implementation</h2>
 * Created to get Abonents from database
 */
@Service("abonentService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final AbonentRepository abonentRep;
    private final Pattern NUMBER_PATTERN;

    public CustomUserDetailsServiceImpl(AbonentRepository abonentRep) {
        this.abonentRep = abonentRep;
        NUMBER_PATTERN = Pattern.compile("[0-9]{11}");
    }

    /**
     * <h2>Load by username</h2>
     * Get Abonent from database and check password
     * @param username - abonent phone number
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!NUMBER_PATTERN.matcher(username).find()) {
            throw new IllegalArgumentException("Abonent login is not correct");
        }
        Long phoneNumber = Long.parseLong(username);
        if(phoneNumber < 70000000000l || phoneNumber > 89999999999l) {
            throw new IllegalArgumentException("Phone number must be between 70000000000 AND 89999999999");
        }
        Abonent abonent = abonentRep.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Abonent with phone number %d not found", phoneNumber)));
        return new UserDetailsImpl(abonent);
    }
}
