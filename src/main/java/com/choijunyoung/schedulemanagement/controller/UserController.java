package com.choijunyoung.schedulemanagement.controller;


import com.choijunyoung.schedulemanagement.dto.UserCreateRequest;
import com.choijunyoung.schedulemanagement.entity.User;
import com.choijunyoung.schedulemanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//객체를 전달하는 코드이기 때문에 json또는 xml형식으로 직렬화하여 클라이언트 에게 전당


@RequestMapping("/users")
    //users 이 요청이 들어오면 이 컨트롤러로 받겠다.

public class UserController {
    private final UserService userService;
    //이 UserService객체는 스프링이 딱 한번 생성한 객체임
    //객체의 흐름은 UserService객체 생성 -> UserController 생성자에게 전달


    @Autowired
    // 생성자 주입

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    // post 요청이 들어오면 아래 메서드를 실행해라
    public User createUser(@Valid @RequestBody UserCreateRequest request){
        //RequsetBody는 브라우저가 보낸 json을 UserCreteRequest 객체로 바꿔준다
        //스프링이 json에 들어있는값을 request안에 넣어준다.(안에는 회원가입 요청데이터가 들어가있음)
        //DTO -> Entity 로 변환하면서  새로운 Entity 객체를 생성하는 코드
        //@Valid가 dto검사후 조건 통과시 서비스 호출,즉 검사규칙을 실행시키는 스위치
        User user = new User(
        // request안에 있던값을 꺼내서 User객체에 넣음 DB에 저장할 사용자 정보
                request.getUsername(),
                request.getPassword(),
                request.getName(),
                request.getRole()
        );
        return userService.save(user);
        //유저서비스에게 save()좀 실행해달라고 부탁



    }

}
