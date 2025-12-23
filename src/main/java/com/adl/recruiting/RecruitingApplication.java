package com.adl.recruiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class RecruitingApplication {

    public static void main(String[] args) {
//        var enc = new BCryptPasswordEncoder();
//        System.out.println(enc.encode("director"));
//        System.out.println(enc.encode("teamlead"));
//        System.out.println(enc.encode("pm"));

        SpringApplication.run(RecruitingApplication.class, args);
    }

}
