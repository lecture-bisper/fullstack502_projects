package bitc.full502.lostandfound.controller;

import bitc.full502.lostandfound.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<String> saveFcmToken(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                               @RequestParam String fcmToken, @RequestParam String deviceId) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userToken = authorizationHeader.substring(7);
        return ResponseEntity.ok(tokenService.saveFcmToken(userToken, fcmToken, deviceId));
    }
}
