package iriro.publicData.service;

import iriro.publicData.repository.FacilitySafeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service @Transactional
public class FacilitySafeService {
    private FacilitySafeRepository fr;

    // 조회
    // 1. 경찰서 조회
    // 2. 안심지킴이집 조회
    // 3. 보안등 조회
    // 4. CCTV 조회
    // 5. 안전벨 조회

    // 저장
    // 1. 경찰서 저장
    // 2. 안심지킴이집 저장
    // 3. 보안등 저장
    // 4. CCTV 저장
    // 5. 안전벨 저장

    // 7. 불러오기 (한 달에 한 번)
}
