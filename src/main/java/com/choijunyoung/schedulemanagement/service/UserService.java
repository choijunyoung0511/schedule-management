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

        // 생성자의 이름은 반드시 클래스 이름(UserService)과 같아야 한다.
        // 생성자는 반환 타입(void, int 등)이 없다.

        // (UserRepository userRepository)
        // → 자료형 : UserRepository
        // → 변수 이름 : userRepository
        // 즉, UserRepository 객체를 하나 전달받겠다는 의미이다.

        this.userRepository = userRepository;
        //this는 현재생성되거나 실행중인 객체를 가르키는 참조 변수임
        // 전달받은 객체를 저장하는곳

        // this.userRepository
        // → UserService 클래스의 필드(위에서 선언한 변수)

        // userRepository
        // → 생성자의 매개변수(외부에서 전달받은 객체)

        // = 는 오른쪽의 값을 왼쪽 변수에 저장한다.

        // 즉,
        // 생성자로 전달받은 UserRepository 객체를
        // UserService 객체의 userRepository 필드에 저장하는 코드이다.

        // Spring은 UserService 객체를 만들 때
        // UserRepository 객체를 자동으로 전달해 준다.
        // 이것을 생성자 의존성 주입(Constructor Injection)이라고 한다.

    }
        public User save(User user){
            return userRepository.save(user);
        }
    }

