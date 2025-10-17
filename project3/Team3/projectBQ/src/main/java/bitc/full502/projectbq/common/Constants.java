package bitc.full502.projectbq.common;

public class Constants {

    // user 테이블
    public static final String USER_STATUS_ACTIVE = "ACTIVE"; // 활성화됨
    public static final String USER_STATUS_DELETED = "DELETED"; // 삭제됨

    // role 테이블
    public static final String ROLE_NAME_USER = "USER"; // 일반 사용자
    public static final String ROLE_NAME_MANAGER = "MANAGER"; // 담당자
    public static final String ROLE_NAME_ADMIN = "ADMIN"; // 책임자

    // 권한
    public static final String PERMISSION_STOCK_IN = "STOCK_IN";
    public static final String PERMISSION_UPDATE_MIN_STOCK = "UPDATE_MIN_STOCK";
    public static final String PERMISSION_ADD_ITEM = "ADD_ITEM";
    public static final String PERMISSION_APPROVE_ITEM = "APPROVE_ITEM";
    public static final String PERMISSION_UPDATE_USER_INFO = "UPDATE_USER_INFO";

    // stock_log 테이블
    public static final String STOCK_LOG_TYPE_IN = "IN"; // 입고
    public static final String STOCK_LOG_TYPE_OUT = "OUT"; // 출고

    // item 테이블
    public static final String ITEM_STATUS_PENDING = "PENDING"; // 처리중
    public static final String ITEM_STATUS_REJECTED = "REJECTED"; // 반려됨
    public static final String ITEM_STATUS_ACTIVE = "ACTIVE"; // 활성화
    public static final String ITEM_STATUS_INACTIVE = "INACTIVE"; // 비활성화

    // min_stock 테이블
    public static final String MIN_STOCK_STATUS_OK = "OK"; // 정상
    public static final String MIN_STOCK_STATUS_LOW = "LOW"; // 미만
    public static final String MIN_STOCK_STATUS_PENDING = "PENDING"; // 처리중

    // order_request 테이블
    public static final String REQUEST_STATUS_REQUESTED = "REQUESTED"; // 요청됨
    public static final String REQUEST_STATUS_APPROVED = "APPROVED"; // 승인됨
    public static final String REQUEST_STATUS_REJECTED  = "REJECTED"; //반려됨

    // 직원&비품별 사용현황
    public static final String STATS_TYPE_USER = "USER";
    public static final String STATS_TYPE_ITEM = "ITEM";


    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
}
