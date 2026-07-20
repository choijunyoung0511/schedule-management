package com.choijunyoung.schedulemanagement.service;

import com.choijunyoung.schedulemanagement.entity.User;
import com.choijunyoung.schedulemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
    // public은 다른 클래스에서도 UserService를 사용할수 있음
public class UserService {
    //class는 객체를 만들기 위한 설계도
    private  final UserRepository userRepository;
    // UserService가 사용할 저장소 변수를 선언한것임,private은 해당 클래스 내부에서만 사용 가능
    // final은 한번 값이 들어오면 바꿀수 없음
    // UserRepository의 타입 객체만 들어갈수 있음 뒤에는 변수 이름


    public UserService(UserRepository userRepository) {
        // 객체들 전달받는곳
        // 생성자(Constructor)
        // UserService 객체가 생성될 때 자동으로 한 번 실행된다.
        // userRepository는 택배기사가 잠깐 들고 있는 컵임

        // 생성자의 이름은 반드시 클래스 이름(UserService)과 같아야 한다.
        // 생성자는 반환 타입(void, int 등)이 없다.

        // (UserRepository userRepository)
        // → 자료형 : UserRepository
        // → 변수 이름 : userRepository
        // 즉, UserRepository 객체를 하나 전달받겠다는 의미이다.

        this.userRepository = userRepository;
        //this는 현재생성되거나 실행중인 객체를 가르키는 참조 변수임
        // 비유를 하자면 컵이라서 생각하자
        // 컵이라는 객체
        // 스프링이 컵이라는 객체를 만듬
        // 서비스에게 이 컵 써 라고 건넨다
        // 서비스는 this.userRepository = userRepository; 로 자기 책상에 컵을 올려두는것임
        // 생성자가 끝나면 택배기사는 사라진다 하지만 책상에 있는 컵은 그대로
        // 그래서 나중에 호출할떄 사용하는게 책상에 올려놓은 필드

    }
        public User save(User user){
            return userRepository.save(user);
        }
        // public이라서 다른 클래스에서 사용할수 있다
        // User객체 반환 회원 한 명 데이터
        // 저장할 객체 생성
        // userRepository는 스프힝이 만들어준 Repository객체다
        // DB와 대화하는객체임 userRepository DB를 조작하는 기능이다(데이터를 저장,조회)
    }

