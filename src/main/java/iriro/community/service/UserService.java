package iriro.community.service;

import iriro.community.dto.UserDto;
import iriro.community.entity.UserEntity;
import iriro.community.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    // 비크립트(암호화) 객체 생성
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. 회원가입
    public boolean join(UserDto userDto){
        // dto --> entity 변환
        UserEntity saveEntity = userDto.toEntity();
        // **** 최종 저장하기 전에 입력받은 비밀번호를 암호화 ****  passwordEncoder : 분쇄기
        String pwd = passwordEncoder.encode(saveEntity.getPwToken());
        saveEntity.setPwToken(pwd);
        UserEntity savedEntity = userRepository.save(saveEntity);
        if(savedEntity.getUserId() > 0){return true;}
        return false;
    }

}
