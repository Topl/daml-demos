package co.topl.latticedamldemo.controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.topl.latticedamldemo.model.Member;
import co.topl.latticedamldemo.model.MembersRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {

    @Autowired
    private MembersRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public MemberController(MembersRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/members")
    Collection<Member> members() {
        return (Collection<Member>) memberRepository.findAll();
    }

    @PostMapping("/members")
    ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        String thePassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(thePassword);
        Member result = memberRepository.save(member);
        return ResponseEntity.ok().body(result);
    }

}