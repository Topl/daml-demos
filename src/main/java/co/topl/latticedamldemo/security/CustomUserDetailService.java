package co.topl.latticedamldemo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.topl.latticedamldemo.model.Member;
import co.topl.latticedamldemo.model.MembersRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private MembersRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> someMember = memberRepository.findById(username);
        if (someMember.isEmpty()) {
            throw new UsernameNotFoundException("The user does not exist");
        }
        Member member = someMember.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole()));
        User userDetails = new User(username, member.getPassword(), true, true, true, true, authorities);
        return userDetails;
    }

}
