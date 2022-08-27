package com.blog.blog.service;

import com.blog.blog.Model.User;
import com.blog.blog.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional= userRepository.findByUsername(username);

        User user=userOptional
                .orElseThrow(()->new UsernameNotFoundException("No user "+
                "Found with username:"+username));

        //Using above user object,we creates another object,its like wrapper with same name User(this class is provided by springframework,which implements userDetails interface
        //Here we are mapping our User details to springframework work class and lastly we are providing authorities called Simple Granted Authorities for role called User
        return new org.springframework.security.core.
                userdetails.User(user.getUsername(),
                user.getPassword(),user.isEnabled(),
                true,true,
                true,
        getAuthorities("USER"));

    }
    private Collection<? extends GrantedAuthority> getAuthorities(String role)
    {
        return Collections.singletonList( new SimpleGrantedAuthority(role));
    }
}
