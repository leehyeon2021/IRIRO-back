package iriro.publicData.service;

import iriro.publicData.dto.FacilitySafeDto;
import iriro.publicData.repository.FacilitySafeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class FacilitySafeService {
    private final FacilitySafeRepository fr;

    // 조회
    // 1. 경찰서 조회
    public List<FacilitySafeDto> getPoliceStation(){
        return fr.findByFacType("경찰서")
                .stream()
                .map(entity ->
                    FacilitySafeDto.builder()
                            .facId(entity.getFacId())
                            .facType(entity.getFacType())
                            .facSgg(entity.getFacSgg())
                            .facName(entity.getFacName())
                            .facAdd(entity.getFacAdd())
                            .facLat(entity.getFacLat())
                            .facLng(entity.getFacLng())
                            .facCount(entity.getFacCount())
                            .facUse(entity.getFacUse())
                            .facTel(entity.getFacTel())
                                    .build())
                .collect(Collectors.toList());
    }

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
