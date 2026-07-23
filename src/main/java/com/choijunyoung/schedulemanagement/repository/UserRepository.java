package com.choijunyoung.schedulemanagement.repository;

import com.choijunyoung.schedulemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //자바에서는 매서드(기능)은 객체를 통해서 실행됨
    //Spring이 미리 만들어둔 Repository 인터페이스를 사용하는것임
    //첫번쨰 인자로는 어떤 엔터티를 관리할지, 두번쨰 인자로는 엔터티의 PK타입
    //이 인터페이스(설계도)만  만들어도 기본적인 DB기능을 사용 가능
    //스프링이 레포지토리 객체를 만들어서 user서비스에 넣어줌
    // 그이유는 유저 service에서 DB작업을 하기위해서
    //이 객체를 계속 만드는것이 아니라 한 번 만든 객체를 필요한 곳에 전달하는것임


    boolean existsByUsername(String username);
    //jpa가 메서드 이름을 읽는것임
    //확인하고 내부에서 sql문 실행
    //아이디 중복검사,username이 같은 회원이 존재하는지 확인
    //있으면 True,없으면 False반환함
}
