package iriro.publicData.service;

import iriro.publicData.dto.CrimeRoadDto;
import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.repository.CrimeRoadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class CrimeRoadService {
    private final CrimeRoadRepository cr;

    // 범죄자도로명 조회
    public List<CrimeRoadDto> getCrimeRoad(){
        List<CrimeRoadEntity> entityList = cr.findAll();
        List<CrimeRoadDto> list = entityList.stream()
                .map(CrimeRoadEntity::toDto)
                .collect(Collectors.toList());
        return list;
    }

    // 범죄자도로명 저장
}
