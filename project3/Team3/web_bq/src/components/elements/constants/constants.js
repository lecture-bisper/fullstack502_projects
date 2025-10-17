export const BASE_URL = "http://localhost:8080";

// -------------------- Category (카테고리 = type) --------------------
export const CATEGORY_OPTIONS = [
    {value: "", name: "", label: "전체"},
    {value: 1, name: "office", label: "사무용품"},
    {value: 2, name: "electronics", label: "전자기기"},
    {value: 3, name: "furniture", label: "가구/사무환경"},
    {value: 4, name: "consumables", label: "소모품"},
    {value: 5, name: "safety", label: "안전/보안"},
    {value: 6, name: "pantry", label: "커피/간식/편의용품"},
    {value: 7, name: "others", label: "기타"},
];

// -------------------- Item Status --------------------
export const ITEM_STATUS_OPTIONS = [
    {value: "", label: "전체"},
    {value: "ACTIVE", label: "활성화"},
    {value: "INACTIVE", label: "비활성화"},
    {value: "PENDING", label: "처리중"},
    {value: "REJECTED", label: "반려됨"},
];

// -------------------- Min Stock Status --------------------
export const MIN_STOCK_STATUS_OPTIONS = [
    {value: "", label: "전체"},
    {value: "OK", label: "정상"},
    {value: "LOW", label: "부족"},
    {value: "PENDING", label: "처리중"},
];

// -------------------- Order Request Status --------------------
export const REQUEST_STATUS_OPTIONS = [
    {value: "", label: "전체"},
    {value: "REQUESTED", label: "요청중"},
    {value: "APPROVED", label: "승인완료"},
    {value: "REJECTED", label: "요청취소(반려)"},
];

// -------------------- Warehouse Status --------------------
export const WAREHOUSE_STATUS_OPTIONS = [
    {value: "", label: "전체"},
    {value: "1", label: "A창고"},
    {value: "2", label: "B창고"},
    {value: "3", label: "C창고"}
]

// -------------------- DeptName Status --------------------
export const DEPT_NAME_OPTIONS = [
    {value: "", label: "전체"},
    {value: "PRD", label: "생산/공정"},
    {value: "QAC", label: "품질관리"},
    {value: "RND", label: "연구개발"},
    {value: "SAL", label: "영업"},
    {value: "LOG", label: "물류/배송"},
    {value: "FIN", label: "재무/회계"},
    {value: "HRD", label: "인사/총무"},
    {value: "MKT", label: "마케팅/홍보"},
    {value: "CUS", label: "고객지원/CS"}
];


// -------------------- RoleName Status --------------------
export const ROLE_NAME_OPTIONS = [
    {value: "", label: "전체"},
    {value: "USER", label: "사원"},
    {value: "MANAGER", label: "담당자"},
    {value: "ADMIN", label: "관리자"}
]

// -------------------- Item Status --------------------
export const ITEM_ADD_STATUS_OPTIONS = [
    {value: "", label: "전체"},
    {value: "ACTIVE", label: "처리완료"},
    {value: "PENDING", label: "처리중"},
    {value: "REJECTED", label: "반려"},
]


// -------------------- Storage Type --------------------
export const STORAGE_TYPE = [
    {value: "", label: "전체"},
    {value: "IN", label: "입고"},
    {value: "OUT", label: "출고"}
]