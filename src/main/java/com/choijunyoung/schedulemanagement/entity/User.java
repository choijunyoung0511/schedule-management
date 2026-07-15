package com.choijunyoung.schedulemanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {
    @Id //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) //이 어노테이션이 붙으면 id를 DB가 자동으로 생성해준다
    private Long id; // Long vs long  : Long은 null이 들어가지만 long은 0이 들어간다 그래서 보통 신규 회원은 null로 표현후 생성하면 id가 1로 증가


    @Column(nullable = false,unique = true,length = 30)
    private String username;

    @Column(nullable = false)
    private String password;
    //암호화 하면 길이가 가변적이라서 length를 안준거임


    @Column(nullable = false, length = 20)
    private String name;
    // 이름은 중복될수 있기떄문에 unique를 안주는게 맞음

    @Enumerated(EnumType.STRING)
    //Enum을 사용하면 허용된 값만 사용할수 있어서 실수가 줄어든다
    //만약에 String이 아니라 ORDINAL로 저장하면 0과 1이 저장됨
    private Role role;

}
