// 입출고현황 페이지

import FilterBar from "../../elements/FilterBar.jsx";
import {DatePicker, InputBox, SelectBox} from "../../elements/filters/index.js";
import Sheets from "../../elements/Sheets.jsx";
import {useEffect, useState} from "react";
import axios from "axios";
import dayjs from "dayjs";
import {mapByColumns} from "../statistics/StaticsConfig.js";
import SearchButton from "../../elements/SearchButton.jsx";
import {
    BASE_URL,
    CATEGORY_OPTIONS,
    STORAGE_TYPE,
    WAREHOUSE_STATUS_OPTIONS
} from "../../elements/constants/constants.js";
import {useLocation, useNavigate} from "react-router-dom";
import {Bar} from "../../elements/index.js";

function StockInOutList() {
    const location = useLocation();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);

    const today = dayjs().format("YYYY-MM-DD");
    const oneYearAgo = dayjs().subtract(1, "year").format("YYYY-MM-DD");

    // 기본 필터
    const defaults = {
        nameOrCode: "",
        manufacturer: "",
        empCodeOrEmpName: "",
        startDate: oneYearAgo,
        endDate: today,
        type: "",
        categoryId: "",
        warehouseId: "",
        all: true,
    };

    // 다른 페이지에서 넘어온 preset (없으면 null)
    const preset = location.state?.preset ?? null;

    // 폼/쿼리 초기값: preset이 있으면 병합해서 시작
    const [form, setForm] = useState(() => ({...defaults, ...(preset ?? {})}));
    const [query, setQuery] = useState(() => ({...defaults, ...(preset ?? {})}));
    const [rowData, setRowData] = useState(undefined);

    // preset을 적용하고 나면 히스토리 state 제거(뒤로가기 중복 방지)
    useEffect(() => {
        if (preset) {
            navigate(".", {replace: true, state: null});
        }
    }, []);

    // 요청 바디 빌드 (빈값 제거 + 카테고리 코드→ID)
    const buildRequestBody = (q) => {

        const body = {
            nameOrCode: q.nameOrCode?.trim(),
            manufacturer: q.manufacturer?.trim(),
            empCodeOrEmpName: q.empCodeOrEmpName?.trim(),
            type: q.type || undefined,
            categoryId:
                q.categoryId !== "" && q.categoryId != null
                    ? Number(q.categoryId)
                    : undefined,
            warehouseId: q.warehouseId !== "" && q.warehouseId != null ? Number(q.warehouseId) : undefined,
            startDate: q.startDate || undefined,
            endDate: q.endDate || undefined,
        };

        // undefined/빈 문자열 제거
        return Object.fromEntries(
            Object.entries(body).filter(([, v]) => v !== undefined && v !== "")
        );
    };

    // 조회
    const fetchData = async (q = query) => {
        try {
            setLoading(true)
            const body = buildRequestBody(q);
            const res = await axios.post(BASE_URL + "/api/stock-logs/search", body);
            const mapped = mapByColumns(res.data, columns);
            setRowData(mapped);
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false)
        }
    };

    // query가 바뀔 때만 조회 (일반 진입: defaults로 1회, preset 진입: preset으로 1회)
    useEffect(() => {
        fetchData(query);
    }, [query]);

    const updateForm = (patch) => setForm((prev) => ({...prev, ...patch}));

    const columns = [
        {field: "index", headerName: "순번", align: "center", flex: 0.1},
        {field: "type", headerName: "상태", align: "center", flex: 0.1},
        {field: "itemCode", headerName: "비품코드"},
        {field: "itemName", headerName: "비품명"},
        {field: "itemManufacturer", headerName: "제조사"},
        {field: "quantity", headerName: "수량(EA)"},
        {
            field: "itemPrice", headerName: "가격(원)", valueFormatter: (params) => {
                if (params.value == null) return "";
                const num = Number(params.value);
                if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                return num.toLocaleString("ko-KR") + "원";
            }, align: "right"
        },
        {
            field: "totalPrice", headerName: "총 금액(원)", valueFormatter: (params) => {
                if (params.value == null) return "";
                const num = Number(params.value);
                if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                return num.toLocaleString("ko-KR") + "원";
            }, align: "right"
        },
        {field: "categoryKrName", headerName: "종류", flex: 1.2},
        {field: "warehouseKrName", headerName: "창고", flex: 0.8},
        {field: "empName", headerName: "입·출고자", flex: 0.8},
        {field: "logDate", headerName: "입·출고일"},
        {field: "memo", headerName: "비고"},
    ];

    return (
        <main className="main">
            <p className="main-title">입·출고 현황</p>
            <FilterBar>
                <div className="filter-bar-scroll">
                    <InputBox
                        id="nameOrCode"
                        title="비품명·비품코드"
                        type="text"
                        width="160px"
                        value={form.nameOrCode}
                        setState={(val) => updateForm({nameOrCode: val})}
                    />
                    <Bar/>
                    <InputBox
                        id="manufacturer"
                        title="제조사"
                        type="text"
                        width="160px"
                        value={form.manufacturer}
                        setState={(val) => updateForm({manufacturer: val})}
                    />
                    <Bar/>
                    <InputBox
                        id="empCodeOrEmpName"
                        title="입·출고자"
                        type="text"
                        width="160px"
                        value={form.empCodeOrEmpName}
                        setState={(val) => updateForm({empCodeOrEmpName: val})}
                    />
                    <Bar/>
                    <SelectBox
                        id="type"
                        title="상태"
                        options={STORAGE_TYPE}
                        setState={(val) => updateForm({type: val})}
                    />
                    <Bar/>
                    <SelectBox
                        id="categoryId"
                        title="종류"
                        options={CATEGORY_OPTIONS}
                        setState={(val) => updateForm({categoryId: val})}
                    />
                    <SelectBox
                        id="warehouseId"
                        title="창고"
                        options={WAREHOUSE_STATUS_OPTIONS}
                        setState={(val) => updateForm({warehouseId: val})}
                    />
                    <Bar/>
                    <DatePicker
                        title={"기간"}
                        setStartDate={(val) => updateForm({startDate: val})}
                        setEndDate={(val) => updateForm({endDate: val})}
                        value={{startDate: form.startDate, endDate: form.endDate}}
                    />
                </div>
                <SearchButton onClick={() => setQuery(form)}/>
            </FilterBar>

            <Sheets rows={13} columns={columns} rowData={rowData} loading={loading}/>
        </main>
    );
}

export default StockInOutList;
