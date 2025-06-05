package com.semi.ecoinsight.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semi.ecoinsight.member.model.dto.MemberDTO;
import com.semi.ecoinsight.member.model.dto.UpdatePasswordDTO;
import com.semi.ecoinsight.member.model.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 메서드
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<?> signUp(@RequestBody @Valid MemberDTO member){
        memberService.signUp(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordDTO passwordEntity){
        memberService.updatePassword(passwordEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
