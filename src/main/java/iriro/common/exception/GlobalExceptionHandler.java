package iriro.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // DTO 검증 실패 시 던지는 예외
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 전역 예외 처리기
@Slf4j // 로그 객체를 자동으로 만들어 줌.
public class GlobalExceptionHandler {

    // 외부 TmapAPI 호출 실패 == 경로 조회 실패
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiException(ExternalApiException e) {

        log.error("외부 API 호출 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body("경로 조회에 실패했습니다.");
    }

    // 로그 저장 실패
    @ExceptionHandler(LogSaveException.class)
    public ResponseEntity<String> handleLogSaveException(LogSaveException e) {

        log.error("로그 저장 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("로그 저장에 실패했습니다.");
    }

    // 이메일을 못찾았을 때 예외처리
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<String> handleEmailNotFoundException(EmailNotFoundException e){

        log.error("이메일 못 찾음: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("해당 이메일이 없습니다.");
    }
    // DTO 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        log.error("유효성 검증 실패: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }

    // 에상치 못한 예외처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("서버 내부 예외 발생", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류가 발생했습니다.");
    }
}
