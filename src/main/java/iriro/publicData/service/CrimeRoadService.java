package iriro.publicData.service;

import iriro.publicData.repository.CrimeRoadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Transactional
public class CrimeRoadService {
    private final CrimeRoadRepository cr;

    // 범죄자도로명 조회
    // 범죄자도로명 저장
}
