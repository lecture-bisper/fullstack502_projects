// 기간별/부서별 사용금액
// 한 페이지 내에서 버튼으로 기간별/부서별 사용금액 조회

import FilterBar from "../../elements/FilterBar.jsx";
import {DatePicker} from "../../elements/filters/index.js";
import CustomButton from "../../elements/CustomButton.jsx";
import SearchButton from "../../elements/SearchButton.jsx";
import {useEffect, useMemo, useState} from "react";
import dayjs from "dayjs";
import {EmpItemModeConfig, periodKey} from "./StaticsConfig.js";
import axios from "axios";
import GroupSheets from "../../elements/GroupSheets.jsx";
import {BASE_URL} from "../../elements/constants/constants.js";
import ChartBar from "../../elements/ChartBar.jsx";
import ChartPie from "../../elements/ChartPie.jsx";
import {CategoryScale} from "chart.js";
import {Sheets} from "../../elements/index.js";

function CostStatics() {
    const [mode, setMode] = useState("UNIT_TIME");
    const config = EmpItemModeConfig[mode];
    const [periodUnit, setPeriodUnit] = useState("month");
    const [groupField, setGroupField] = useState("")
    const [view, setView] = useState("table");
    const [chartKind, setChartKind] = useState(null);
    const [loading, setLoading] = useState(true);

    const [dateRange, setDateRange] = useState({
        start: dayjs().subtract(1, "year").format("YYYY-MM-DD"),
        end: dayjs().format("YYYY-MM-DD")
    });

    const [rowData, setRowData] = useState(undefined);

    // -------- 원본 데이터 패치 --------
    const [dataLog, setDataLog] = useState(null);
    const [dataUser, setDataUser] = useState(null);

    const fetchAll = async () => {
        setLoading(true)

        // 내부 상태 키(start/end) → API 파라미터 매핑
        const params = {startDate: dateRange.start, endDate: dateRange.end, type: "OUT"};

        const API_Log_URL = BASE_URL + "/api/stock-logs/search";
        const API_User_URL = BASE_URL + "/api/users";

        try {
            const [resLog, resUser] = await Promise.allSettled([
                axios.post(API_Log_URL, params), // 서버가 POST 바디로 받는 계약이면 유지
                axios.get(API_User_URL, {withCredentials: true}),
            ]);

            if (resLog.status === "fulfilled") {
                setDataLog(resLog.value.data);
            } else {
                console.error("❌ errorLog", resLog.reason);
            }

            if (resUser.status === "fulfilled") {
                setDataUser(resUser.value.data);
            } else {
                console.error("❌ errorUser", resUser.reason);
            }
        } finally {
            setLoading(false)
        }
    };

    useEffect(() => {
        fetchAll();
        setView("table");
        setChartKind(null);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [mode]);

    // ------ 부서별,아이템별 합계 --------

    // 1) 사원 → 부서 인덱스
    function buildUserIndex(users = []) {
        const idx = new Map();
        for (const u of users) {
            if (!u?.empCode) continue;
            idx.set(String(u.empCode), u);
        }
        return idx;
    }

    // 2) 로그에 부서정보 보강
    function attachDeptToLogs(logs = [], userIndex /* Map */) {
        return logs.map((log) => {
            const emp = userIndex.get(String(log.empCode));
            const deptName = log.deptName ?? emp?.deptName ?? "부서 없음";
            const deptCode = log.deptCode ?? emp?.deptCode ?? null;
            return {...log, deptName, deptCode};
        });
    }

    // 3) 부서별 + 부서 내 아이템별 합계
    const toNum = (v) => (v == null ? 0 : Number(v) || 0);
    const newer = (a, b) => {
        if (!a) return b ?? null;
        if (!b) return a ?? null;
        return new Date(a) > new Date(b) ? a : b;
    };

    function aggregateDeptAndItems(logs = []) {
        const deptMap = new Map();
        // deptName -> { deptName, totalQuantity, totalPrice, latestDate, items: Map }

        for (const row of logs) {
            const deptName = row.deptName ?? "Unknown";
            const itemCode = String(row.itemCode ?? "");
            const itemName = row.itemName ?? "";
            const categoryId = row.categoryId ?? null;
            const qty = toNum(row.quantity);
            const price = toNum(row.itemPrice);
            const when = row.logDate ?? null;

            // 부서 합계
            if (!deptMap.has(deptName)) {
                deptMap.set(deptName, {
                    deptName,
                    totalQuantity: 0,
                    totalPrice: 0,
                    latestDate: null,
                    items: new Map(), // itemCode -> { itemCode, itemName, categoryId, totalQuantity, totalPrice, latestDate }
                });
            }
            const d = deptMap.get(deptName);
            d.totalQuantity += qty;
            d.totalPrice += qty * price;
            d.latestDate = newer(d.latestDate, when);

            // 부서 내 아이템 합계
            if (!d.items.has(itemCode)) {
                d.items.set(itemCode, {
                    itemCode,
                    itemName,
                    categoryId,
                    totalQuantity: 0,
                    totalPrice: 0,
                    latestDate: null,
                });
            }
            const it = d.items.get(itemCode);
            it.totalQuantity += qty;
            it.totalPrice += qty * price;
            it.latestDate = newer(it.latestDate, when);
        }

        const byDept = Array.from(deptMap.values()).map((dept) => ({
            deptName: dept.deptName,
            totalQuantity: dept.totalQuantity,
            totalPrice: dept.totalPrice,
            latestDate: dept.latestDate,
            items: Array.from(dept.items.values()),
        }));

        const flatDeptItem = [];
        for (const dept of byDept) {
            for (const it of dept.items) {
                flatDeptItem.push({
                    deptName: dept.deptName,
                    itemCode: it.itemCode,
                    itemName: it.itemName,
                    categoryId: it.categoryId,
                    totalQuantity: it.totalQuantity,
                    totalPrice: it.totalPrice,
                    latestDate: it.latestDate,
                });
            }
        }
        return {byDept, flatDeptItem};
    }

    // DEPT 모드: 컬럼 스키마에 맞춰 rowData 생성
    function buildDeptRowData(logs, users) {
        const userIdx = buildUserIndex(users ?? []);
        const logsWithDept = attachDeptToLogs(logs ?? [], userIdx);
        const {flatDeptItem} = aggregateDeptAndItems(logsWithDept);

        return flatDeptItem.map((r, i) => ({
            index: i + 1,
            dept: r.deptName,            // EmpItemModeConfig.DEPT.columns의 field에 맞춤
            itemCode: r.itemCode,
            itemName: r.itemName,
            totalQuantity: r.totalQuantity,
            totalPrice: r.totalPrice,
            categoryId: r.categoryId,
            lastStorageDate: r.latestDate, // field: "lastStorageDate"
        }));
    }

    // ------ 기간별, 아이템별 합계 ------

    // 1) UNIT_TIME용 집계기
    function buildUnitTimeRowData(logs, unit = "month") {
        // periodKey는 StaticsConfig에 있음 , 기간단위 만드는 함수
        const grouped = new Map(); // key = period + '_' + itemCode

        for (const row of logs ?? []) {
            const period = periodKey(row.logDate, unit); // <- 너의 util
            const itemCode = String(row.itemCode ?? "");
            const key = `${period}__${itemCode}`;

            if (!grouped.has(key)) {
                grouped.set(key, {
                    unitTime: period,
                    itemCode: row.itemCode ?? "",
                    itemName: row.itemName ?? "",
                    categoryId: row.categoryId ?? null,
                    totalQuantity: 0,
                    totalPrice: 0,
                    lastStorageDate: null,
                });
            }
            const g = grouped.get(key);
            const qty = Number(row.quantity) || 0;
            const price = Number(row.itemPrice) || 0;

            g.totalQuantity += qty;
            g.totalPrice += qty * price;
            g.lastStorageDate = g.lastStorageDate == null
                ? row.logDate ?? null
                : (row.logDate && new Date(row.logDate) > new Date(g.lastStorageDate) ? row.logDate : g.lastStorageDate);
        }

        // id 부여 + 컬럼 스키마에 맞춤 (EmpItemModeConfig.UNIT_TIME.columns)
        return Array.from(grouped.values()).map((r, i) => ({
            index: i + 1,
            unitTime: r.unitTime,
            itemCode: r.itemCode,
            itemName: r.itemName,
            totalQuantity: r.totalQuantity,
            totalPrice: r.totalPrice,
            categoryId: r.categoryId,
            lastStorageDate: r.lastStorageDate,
        }));
    }


    // 모드/데이터 변경 시 보여줄 rowData 갱신
    useEffect(() => {
        // 먼저 클리어해서 이전 모드의 잔상 방지
        setRowData([]);

        if (mode === "DEPT" && dataLog && dataUser) {
            const rows = buildDeptRowData(dataLog, dataUser);
            setGroupField("dept");
            setRowData(rows);
            return;
        }

        if (mode === "UNIT_TIME" && dataLog) {
            const rows = buildUnitTimeRowData(dataLog, periodUnit);
            setGroupField("unitTime");
            setRowData(rows);
        }
    }, [mode, dataLog, dataUser, periodUnit]);


    // ------ 그래프 그리기 ------

    // 기간별 사용금액 막대그래프
    const unitTimeBar = useMemo(() => {
        if (mode !== "UNIT_TIME" || !dataLog) return {labels: [], values: []};

        // rowData가 최신 상태면 rowData에서 바로 합쳐도 되고,
        // 안전하게 다시 집계하려면 dataLog로 재계산
        const rows = buildUnitTimeRowData(dataLog, periodUnit);

        // unitTime별 totalPrice 합계
        const map = new Map();
        for (const r of rows) {
            const k = r.unitTime ?? "기타";
            const v = Number(r.totalPrice) || 0;
            map.set(k, (map.get(k) || 0) + v);
        }

        // 표시 순서 정렬 (예: unitTime 오름차순)
        const labels = Array.from(map.keys()).sort();
        const values = labels.map(l => map.get(l));

        return {labels, values};
    }, [mode, dataLog, periodUnit]);

    // 부서별 사용금액 원형그래프
    const deptPie = useMemo(() => {
        if (mode !== "DEPT" || !dataLog || !dataUser) return {labels: [], values: []};

        const userIdx = buildUserIndex(dataUser ?? []);
        const logsWithDept = attachDeptToLogs(dataLog ?? [], userIdx);
        const {byDept} = aggregateDeptAndItems(logsWithDept);

        const filtered = byDept.filter(d => (Number(d.totalPrice) || 0) > 0);
        const sorted = filtered.sort((a, b) => b.totalPrice - a.totalPrice);
        const top = sorted.slice(0, 10);
        const rest = sorted.slice(10);
        const restSum = rest.reduce((acc, d) => acc + (Number(d.totalPrice) || 0), 0);

        const labels = top.map(d => d.deptName).concat(rest.length ? ["기타"] : []);
        const values = top.map(d => d.totalPrice).concat(rest.length ? [restSum] : []);
        return {labels, values};
    }, [mode, dataLog, dataUser]);


    return (
        <main className="main">
            <p className="main-title">{mode === "UNIT_TIME" ? "기간별 비품 사용 금액 확인" : "부서별 비품 사용 금액 확인"}</p>
            <div style={{
                width: "100%",
                display: "flex",
                justifyContent: "center",
                flexDirection: "column",
                alignItems: "center"
            }}>
                <div style={{width: "90%", display: "flex", justifyContent: "flex-end", marginBottom: "5px"}}>
                    <CustomButton
                        color="deepBlue"
                        width="200  px"
                        height="35px"
                        onClick={() => setMode(mode === "UNIT_TIME" ? "DEPT" : "UNIT_TIME")}
                    >
                        {mode === "UNIT_TIME" ? "부서별 비품 사용 금액 확인" : "기간별 비품 사용 금액 확인"}
                    </CustomButton>
                </div>

                <FilterBar>
                    <div className="filter-bar-scroll">
                        <DatePicker
                            title={"기간"}
                            value={{startDate: dateRange.start, endDate: dateRange.end}}
                            setStartDate={(v) => setDateRange(prev => ({...prev, start: v}))}
                            setEndDate={(v) => setDateRange(prev => ({...prev, end: v}))}
                        />
                    </div>
                    <SearchButton onClick={fetchAll}/>
                    <div className={"mode-btn"}>
                        {mode === "UNIT_TIME" && (
                            <>
                                <CustomButton color="deepBlue" width="90px" height="35px" onClick={() => setPeriodUnit("day")}>
                                    일별확인
                                </CustomButton>
                                <CustomButton color="deepBlue" width="90px" height="35px" onClick={() => setPeriodUnit("month")}>
                                    월별확인
                                </CustomButton>
                                <CustomButton color="deepBlue" width="105px" height="35px" onClick={() => setPeriodUnit("year")}>
                                    년도별확인
                                </CustomButton>
                            </>
                        )}
                        <CustomButton
                            color="deepBlue"
                            width="90px"
                            height="35px"
                            onClick={() => {
                                if (view === "table") {
                                    // 차트로 전환
                                    if (mode === "UNIT_TIME") setChartKind("bar");
                                    if (mode === "DEPT") setChartKind("pie");
                                    setView("chart");
                                } else {
                                    // 표로 전환
                                    setView("table");
                                }
                            }}
                            disabled={
                                (mode === "UNIT_TIME" && (!unitTimeBar.labels.length || !dataLog)) ||
                                (mode === "DEPT" && (!deptPie.labels.length || !dataLog || !dataUser))
                            }
                        >
                            {view === "chart" ? "표보기" : "차트보기"}
                        </CustomButton>
                    </div>
                </FilterBar>
            </div>
            {view === "chart" ? (
                chartKind === "bar" && mode === "UNIT_TIME" ? (
                    <div style={{
                        backgroundColor: "white",
                        padding: "10px",
                        borderRadius: "8px",
                        height: "600px",
                        width: "90%"
                    }}>
                        <ChartBar
                            labels={unitTimeBar.labels}
                            values={unitTimeBar.values}
                            height={550}
                            CategoryScale
                            // 필요하면 tooltipFormatter, onBarClick 등 추가
                        />
                    </div>
                ) : chartKind === "pie" && mode === "DEPT" ? (
                    <div style={{
                        backgroundColor: "white",
                        padding: "10px",
                        borderRadius: "8px",
                        height: "600px",
                        width: "90%"
                    }}>
                        <ChartPie
                            labels={deptPie.labels}
                            values={deptPie.values}
                            height={550}
                            legend="right"
                            // onSliceClick={({ label }) => { ...드릴다운… }}
                        />
                    </div>
                ) : (
                    // 안전망: 조건 맞지 않으면 표로
                    <Sheets rows={13} columns={config.columns} rowData={rowData} loading={loading}/>
                )
            ) : (
                <GroupSheets rowData={rowData} columns={config.columns} groupByField={groupField} loading={loading}/>
            )}
        </main>
    );
}

export default CostStatics;
