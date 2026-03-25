package iriro.community.service;

import iriro.community.dto.UserDto;
import iriro.community.entity.UserEntity;
import iriro.community.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    // 비크립트(암호화) 객체 생성
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. 회원가입
    public boolean join(UserDto joinDto){
        // dto --> entity 변환
        UserEntity saveEntity = joinDto.toEntity();
        // **** 최종 저장하기 전에 입력받은 비밀번호를 암호화 ****  passwordEncoder : 분쇄기
        String pwd = passwordEncoder.encode(saveEntity.getPwToken());
        saveEntity.setPwToken(pwd);
        UserEntity savedEntity = userRepository.save(saveEntity);
        if(savedEntity.getUserId() > 0){return true;}
        return false;
    }

    // 2. 로그인
    public boolean login(UserDto loginDto){
        // 1] JPA으로 아이디로 엔티티찾기 , SQL로 아이디/비밀번호 일치 여부로 판단 불가능
        Optional<UserEntity> optionalUser = userRepository.findByEmail(loginDto.getEmail());
        // 2] 만약에 조회된 엔티티가 존재하면
        if(optionalUser.isPresent()){
            // 엔티티 꺼내기
            UserEntity userEntity = optionalUser.get();
            // 비크립트 암호화로 평문과 암호화문 비교, passwordEncoder.matches( 평문 , 암호문 );
            boolean result = passwordEncoder.matches(loginDto.getPwToken(), userEntity.getPwToken());
            if( result == true ){ return true; } // 로그인 성공
            else{ return false; } // 로그인 실패(패스워드 다를 때)
        }
        // 3] 없으면 로그인 실패(아이디 없을 때)
        return false;

    }

}
