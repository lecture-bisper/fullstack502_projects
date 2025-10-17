// 직원별 비품별 사용현황
// 한 페이지 내에서 직원별 혹은 비품별 선택하여 사용현황조회(ui참조)

import dayjs from "dayjs";
import axios from "axios";
import {EmpItemModeConfig, mapByColumns} from "./StaticsConfig.js";
import {useEffect, useState} from "react";
import {Bar, CustomButton, FilterBar, SearchButton, Sheets} from "../../elements/index.js";
import {DatePicker, InputBox} from "../../elements/filters/index.js";
import {useNavigate} from "react-router-dom";

function EmployeeItemStatics() {
    const navigate = useNavigate();
    const today = dayjs().format("YYYY-MM-DD");

    const onYearAgo = dayjs().subtract(1, "year").format("YYYY-MM-DD");
    const [loading, setLoading] = useState(true);
    const [mode, setMode] = useState("EMP");
    const [rowData, setRowData] = useState(undefined);
    const [startDate, setStartDate] = useState(onYearAgo);
    const [endDate, setEndDate] = useState(today);

    // ✅ 한 군데(filters)에서만 관리
    const [filters, setFilters] = useState({
        codeOrName: "",
        startDate: startDate,
        endDate: endDate,
        all: true,
    });

    const columns = EmpItemModeConfig[mode].columns;

    const fetchData = async (f = filters) => {
        setLoading(true)
        try {
            const {data} = await axios.get(EmpItemModeConfig[mode].endpoint, {params: f});
            const mapped = mapByColumns(data, EmpItemModeConfig[mode].columns);
            setRowData(mapped);
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false)
        }
    };

    // 모드 변경 시 초기화 + 조회
    useEffect(() => {
        const init = {codeOrName: "", startDate: onYearAgo, endDate: today, all: true};
        setFilters(prev => ({...prev, ...init}));
        fetchData(init); // 상태 반영 기다리지 말고, 같은 객체로 바로 조회
    }, [mode]);

    useEffect(() => {
        const updateStartDate = startDate === "" ? "1900-01-01" : startDate;
        const updateEndDate = endDate === "" ? "9999-12-31" : endDate;
        setFilters({...filters, startDate: updateStartDate, endDate: updateEndDate});
    }, [startDate, endDate])

    const updateFilter = (patch) => setFilters(prev => ({...prev, ...patch}));


    function handleCellClick(clickedCell) {
        if (mode === "EMP" && clickedCell.colDef.field === "empName") {
            navigate("/stocks/log", {state: {preset: {empCodeOrEmpName: clickedCell.data.empName}}});
        } else if (mode === "ITEM" && clickedCell.colDef.field === "name") {
            navigate("/stocks/log", {state: {preset: {nameOrCode: clickedCell.data.name}}});
        }
    }


    return (
        <main className="main">
            <p className="main-title">직원별 · 비품별 사용 금액</p>
            <FilterBar>
                <div style={{display: "flex", justifyContent: "space-between", width: "100%"}}>
                    <div className="filter-bar-scroll">
                        <InputBox
                            id="codeOrName"
                            width="70%"
                            title={mode === "EMP" ? "사번·이름" : "비품명"}
                            type="text"
                            value={filters.codeOrName}
                            setState={(v) => updateFilter({codeOrName: v})}
                        />
                        <Bar/>
                        <DatePicker
                            title={"기간"}
                            value={{startDate, endDate}}
                            setStartDate={setStartDate}
                            setEndDate={setEndDate}
                        />
                    </div>
                    <div style={{display: "flex", gap: "5px"}}>
                        <CustomButton
                            color="red"
                            width="200px"
                            height="30px"
                            style={{marginLeft: "auto"}}
                            onClick={() => setMode(mode === "EMP" ? "ITEM" : "EMP")}
                        >
                            {mode === "EMP" ? "비품별 사용금액 확인" : "직원별 사용금액 확인"}
                        </CustomButton>
                        <SearchButton onClick={() => fetchData()}/>
                    </div>
                </div>
            </FilterBar>
            <Sheets
                rows={13}
                columns={columns}
                rowData={rowData}
                onCellClick={handleCellClick}
                clickableFields={mode === "EMP" ? ["empName"] : ["name"]}
                loading={loading}
            />
        </main>
    );
}

export default EmployeeItemStatics;