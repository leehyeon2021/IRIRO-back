package iriro.publicData.service;

import iriro.publicData.dto.FacilitySafeDto;
import iriro.publicData.entity.FacilitySafeEntity;
import iriro.publicData.repository.FacilitySafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilitySafeService {
    private final FacilitySafeRepository fr;

    // 조회
    // 1. 경찰서 조회
    public List<FacilitySafeDto> getPoliceStation(){
        return fr.findByFacType("경찰서")
                .stream()
                .map(FacilitySafeEntity::toDto)
                .collect(Collectors.toList());
    }

    // 2. 안심지킴이집 조회
    public List<FacilitySafeDto> getSafeHouse() {
        return fr.findByFacType("안심지킴이집")
                .stream()
                .map(FacilitySafeEntity::toDto)
                .collect(Collectors.toList());
    }

    // 3. 보안등 조회
    public List<FacilitySafeDto> getSafeLight() {
        return fr.findByFacType("보안등")
                .stream()
                .map(FacilitySafeEntity::toDto)
                .collect(Collectors.toList());
    }

    // 4. CCTV 조회
    public List<FacilitySafeDto> getCctv() {
        return fr.findByFacType("CCTV")
                .stream()
                .map(FacilitySafeEntity::toDto)
                .collect(Collectors.toList());
    }

    // 5. 안전벨 조회
    public List<FacilitySafeDto> getSafeBell() {
        return fr.findByFacType("안전벨")
                .stream()
                .map(FacilitySafeEntity::toDto)
                .collect(Collectors.toList());
    }


    // 저장 - 다른 파일 만드는 게 나을 것 같음
    // 1. 경찰서 저장
    // 2. 안심지킴이집 저장
    // 3. 보안등 저장
    // 4. CCTV 저장
    // 5. 안전벨 저장

    // 7. 불러오기 (한 달에 한 번)
}
