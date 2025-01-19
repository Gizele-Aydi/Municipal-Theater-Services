package org.example.municipaltheater.services.AuthenticationServices;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.security.services.UserDetailsImpl;
import org.example.municipaltheater.utils.DefinedExceptions.ONotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RegisteredUsersRepository UserRepo;

    @Autowired
    public UserDetailsServiceImpl(RegisteredUsersRepository userRepo) {
        this.UserRepo = userRepo;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RegisteredUser user = UserRepo.findByUsername(username).orElseThrow(() -> new ONotFoundException("This username wasn't found: " + username));
        return UserDetailsImpl.build(user);
    }
}
