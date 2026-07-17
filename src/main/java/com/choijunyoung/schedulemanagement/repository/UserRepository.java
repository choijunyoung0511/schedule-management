package com.choijunyoung.schedulemanagement.repository;

import com.choijunyoung.schedulemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //자바에서는 매서드(기능)은 객체를 통해서 실행됨
    //Spring이 미리 만들어둔 Repository 인터페이스를 사용하는것임
    //첫번쨰 인자로는 어떤 엔터티를 관리할지, 두번쨰 인자로는 엔터티의 PK타입
    //이 인터페이스(설계도)만  만들어도 기본적인 DB기능을 사용 가능
}
