import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
import {BASE_URL, CATEGORY_OPTIONS} from "../../elements/constants/constants.js";

export const EmpItemModeConfig = {
    EMP: {
        title: "직원별 사용 금액",
        endpoint: BASE_URL + "/api/stats/user",
        columns: [
            {field: "index", headerName: "순번", align: "center", flex: 0.1},
            {field: "empCode", path: "info.empCode", headerName: "사번"},
            {field: "empName", path: "info.empName", headerName: "이름"},
            {field: "deptName", path: "info.deptName", headerName: "부서"},
            {
                field: "totalPrice", path: "totalPrice", headerName: "총 사용금액(원)", valueFormatter: (params) => {
                    if (params.value == null) return "";
                    const num = Number(params.value);
                    if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                    return num.toLocaleString("ko-KR") + "원";
                }, align: "right"
            },
            {
                field: "latestDate", path: "latestDate", headerName: "최근 입출고 날짜",
                valueFormatter: p => (p.value ?? "").slice(0, 10)
            }
        ],
        placeholders: {keyword: "사번·사원명 입력"}
    },

    ITEM: {
        title: "비품별 사용 금액",
        endpoint: BASE_URL + "/api/stats/item",

        columns: [
            {field: "index", headerName: "순번", align: "center", flex: 0.1},
            {field: "code", path: "info.code", headerName: "비품코드"},
            {field: "name", path: "info.name", headerName: "비품명"},
            {field: "categoryName", path: "info.categoryName", headerName: "종류"},
            {field: "manufacturer", path: "info.manufacturer", headerName: "제조사"},
            {
                field: "totalPrice", path: "totalPrice", headerName: "총 사용금액(원)", valueFormatter: (params) => {
                    if (params.value == null) return "";
                    const num = Number(params.value);
                    if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                    return num.toLocaleString("ko-KR") + "원";
                }, align: "right"
            },
            {
                field: "latestDate", path: "latestDate", headerName: "최근 입출고 날짜",
                valueFormatter: p => (p.value ?? "").slice(0, 10)
            }
        ],
        placeholders: {keyword: "비품명 입력"}
    },

    UNIT_TIME: {
        title: "기간별 비품사용금액",
        columns: [
            {field: "index", headerName: "순번", align: "center", flex: 0.1},
            {field: "unitTime", headerName: "단위기간", filter: true},
            {field: "itemCode", headerName: "비품코드"},
            {field: "itemName", headerName: "비품명"},
            {field: "totalQuantity", headerName: "총 사용량(EA)"},
            {
                field: "totalPrice", headerName: "총 사용금액(원)",
                valueFormatter: (params) => {
                    if (params.value == null) return "";
                    const num = Number(params.value);
                    if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                    return num.toLocaleString("ko-KR") + "원";
                }, align: "right"
            },
            {
                field: "categoryId", headerName: "카테고리", valueFormatter: (params) => {
                    const found = CATEGORY_OPTIONS.find(opt => opt.value === params.value);
                    return found ? found.label : params.value; // 없으면 원래 값
                }
            },
            {
                field: "lastStorageDate", headerName: "최근 출고일",
                valueFormatter: p => (p.value ?? "").slice(0, 10)
            }
        ]

    },
    DEPT: {
        title: "부서별 비품사용금액",
        columns: [
            {field: "index", headerName: "순번", align: "center", flex: 0.1},
            {field: "dept", headerName: "부서명", filter: true},
            {field: "itemCode", headerName: "비품코드"},
            {field: "itemName", headerName: "비품명"},
            {field: "totalQuantity", headerName: "총 사용량(EA)"},
            {
                field: "totalPrice", headerName: "총 사용금액(원)",
                valueFormatter: (params) => {
                    if (params.value == null) return "";
                    const num = Number(params.value);
                    if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                    return num.toLocaleString("ko-KR") + "원";
                }, align: "right"
            },
            {
                field: "categoryId", headerName: "카테고리", valueFormatter: (params) => {
                    const found = CATEGORY_OPTIONS.find(opt => opt.value === params.value);
                    return found ? found.label : params.value; // 없으면 원래 값
                }
            },
            {
                field: "lastStorageDate", headerName: "최근 출고일",
                valueFormatter: p => (p.value ?? "").slice(0, 10)
            }
        ]

    }
};


// util

// 점 표기 경로로 안전하게 값 꺼내기
const getByPath = (obj, path) =>
    path?.split(".").reduce((o, k) => (o == null ? undefined : o[k]), obj);


export const mapByColumns = (rows, columns) =>
    (rows ?? []).map((row, index) =>
        columns.reduce((acc, col) => {
            const src = col.path || col.field;
            let val = getByPath(row, src);

            if ((col.field === "lastStorageDate" || col.field === "logDate") && typeof val === "string") {
                val = val.slice(0, 10);
            }
            if (col.field === "type") {
                val = val === "IN" ? "입고" : "출고";
            }

            acc[col.field] = val ?? (col.default ?? "");

            acc.index = index + 1;
            if (acc.quantity || acc.itemPrice) {
                acc.totalPrice = (acc.quantity ?? 0) * (acc.itemPrice ?? 0);
            }
            return acc;
        }, {})
    );


// 월 주 년 단위로 기간 키 만들기
dayjs.extend(isoWeek);

export function periodKey(dateStr, unit = "month") {
    const d = dayjs(dateStr);
    if (!d.isValid()) return ""; // 안전 가드

    switch (unit) {
        case "day":
            return d.startOf("day").format("YYYY년 MM월 DD일");
        case "year":
            return d.startOf("year").format("YYYY년");
        case "month":
        default:
            return d.startOf("month").format("YYYY년 MM월");
    }
}

// 최신 날짜 반영(a,b비교)
export const maxDate = (a, b) => (!a ? b : !b ? a : (dayjs(a).isAfter(b) ? a : b));

// 금액 표시
export const nfKR = new Intl.NumberFormat("ko-KR");
// 화폐 단위 표시
export const asWon = (v) => `${nfKR.format(v)}원`;