package iriro.saferoute.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RouteRatingRequestDto {
    // 추후에 후기에 대한 내용들이 추가 될 수 있으므로 Dto로 저장
    @NotNull(message = "logId는 필수입니다.")
    private Long logId;

    @NotNull(message = "rating은 필수입니다.")
    @Min(value = 0, message = "평점은 0 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하여야 합니다.")
    private Integer rating;
}
