package bitc.full502.lostandfound.service;

public interface FcmService {

    void sendNotification(String senderId, String receiverId, String body, Long roomIdx) throws Exception;
}
