import axios from "axios";
import useUserStore from "../../../utils/useUserStore.jsx";
import {BASE_URL, CATEGORY_OPTIONS, WAREHOUSE_STATUS_OPTIONS} from "../../elements/constants/constants.js";
import {useState, useEffect} from "react";
import {InputBox, SelectBox} from "../../elements/filters/index.js";
import {Bar, CustomButton, FilterBar, ItemDetail, Popup, SearchButton, Sheets} from "../../elements/index.js";


function CurrentStockWarehouse() {
    // rowData 초기값 빈 배열
    const [rowData, setRowData] = useState(undefined);
    const [open, setOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [uStandardQty, setUStandardQty] = useState("");
    const [uSafetyQty, setUSafetyQty] = useState("");
    const [uStatus, setUStatus] = useState("");
    const [updatedItem, setUpdatedItem] = useState(null);
    const [selectedItemCode, setSelectedItemCode] = useState(null);
    const [data, setData] = useState(null);
    const [totalQuantity, setTotalQuantity] = useState(0);
    const [totalKinds, setTotalKinds] = useState(0);
    const [loading, setLoading] = useState(true)
    const user = useUserStore(state => state.user);

    // 필터 상태 (창고 기본값 1)
    const [filters, setFilters] = useState({
        name: "",
        manufacturer: "",
        category: "",
        warehouseId: "1"
    });

    // 컬럼 정의 (cellStyle로 정렬)
    const columns = [
        {field: "index", headerName: "순번", flex: 0.1, align: "center"},
        {field: "itemCode", headerName: "비품코드"},
        {field: "itemName", headerName: "비품명"},
        {field: "manufacturer", headerName: "제조사"},
        {field: "quantity", headerName: "보유수량(EA)"},
        {field: "categoryName", headerName: "종류"},
    ];

    // 데이터 조회 함수
    const fetchData = () => {
        setLoading(true)
        const updatedFilters = {
            ...filters,
            category: CATEGORY_OPTIONS.find(opt => opt.value == filters.category)?.name || filters.category
        }
        axios.get(BASE_URL + "/api/stock/search", {params: updatedFilters})
            .then((res) => {
                const data = Array.isArray(res.data) ? res.data : [];
                // 화면에 순번용 id 추가
                const numberedData = data.map((item, index) => ({
                    ...item,
                    index: index + 1,
                }));
                setRowData(numberedData);
            })
            .catch((err) => console.error(err))
            .finally(() => {
                setLoading(false)
            })
    };

    // 페이지 로드 시 초기 조회
    useEffect(() => {
        fetchData();
    }, []);

    useEffect(() => {
        if (!rowData) return;
        // 총 비품 개수
        setTotalQuantity(rowData.reduce((sum, item) => sum + (item.quantity || 0), 0));

        // 총 비품 종류 (itemName 기준)
        setTotalKinds(new Set(rowData.map(item => item.itemName)).size);
    }, [rowData]);


    const handleToggleEditing = () => {
        if (data) setIsEditing(!isEditing);
    }

    const handleCellClick = (clickedCell) => {
        if (!clickedCell.data.itemCode) {
            alert(""
                + "* 승인되지 않은 비품 입니다 *\n"
                + "\n비품명: " + clickedCell.data.name + " / 제조사: " + clickedCell.data.manufacturer
                + "\n등록자: " + clickedCell.data.addUserName + " / 등록일: " + clickedCell.data.addDate.split("T")[0]
            )
            return;
        }
        setSelectedItemCode(clickedCell.data.itemCode); // 선택한 비품 코드 저장
        setIsEditing(false);
        setOpen(true); // 팝업 열기
    };

    const handleCancel = () => {
        if (!data) return;
        setUStandardQty(data.standardQty);
        setUSafetyQty(data.safetyQty);
        setUStatus(data.status);
        setIsEditing(false);
    };

    const handleSave = () => {
        const params = {standard: uStandardQty, safety: uSafetyQty};
        for (const qty of Object.values(params)) {
            const num = Number(qty);

            if (!Number.isInteger(num) || num < 1) {
                alert("수량을 올바르게 입력해주세요.");
                return;
            }
        }

        const updateQuantity =
            axios.put(BASE_URL + "/api/min-stocks/" + selectedItemCode, {}, {
                params: {standard: uStandardQty, safety: uSafetyQty},
                withCredentials: true
            });
        const updateStatus =
            axios.put(BASE_URL + "/api/items/" + selectedItemCode + "/status", {}, {
                params: {status: uStatus},
                withCredentials: true
            });

        Promise.all([updateQuantity, updateStatus])
            .then(([qtyRes, statusRes]) => {
                setData({...data, status: statusRes.data.status, standardQty: uStandardQty, safetyQty: uSafetyQty});
                setUpdatedItem(statusRes.data);
                alert("수정이 완료되었습니다.")
            })
            .catch(err => {
                console.log(err.response.data);
            })
            .finally(() => {
                setIsEditing(false);
            })
    }

    return (
        <main className="main">
            <p className="main-title">창고별 비품 재고 현황</p>

            <FilterBar>
                <div className="filter-bar-scroll">
                    <InputBox
                        title="비품명"
                        value={filters.name}
                        setState={(val) => setFilters((prev) => ({...prev, name: val}))}
                    />
                    <Bar/>
                    <InputBox
                        title="제조사"
                        value={filters.manufacturer}
                        setState={(val) => setFilters((prev) => ({...prev, manufacturer: val}))}
                    />
                    <Bar/>
                    <SelectBox
                        title="종류"
                        options={CATEGORY_OPTIONS}
                        setState={(val) => setFilters((prev) => ({...prev, category: val}))}
                    />
                    <Bar/>
                    <SelectBox
                        title="창고"
                        options={WAREHOUSE_STATUS_OPTIONS.filter(opt => opt.value !== "")}
                        setState={(val) => setFilters((prev) => ({...prev, warehouseId: val}))}
                        value={filters.warehouseId}
                    />
                </div>
                <SearchButton onClick={fetchData}></SearchButton>
            </FilterBar>
            <Sheets rows={13} columns={columns} rowData={rowData} onCellClick={handleCellClick}
                    clickableFields={["itemName", "itemCode"]} loading={loading}/>
            <div style={{
                padding: "20px 0px",
                height: "auto",
                marginTop: "10px",
                justifyContent: "center",
                flexDirection: "column"
            }}
                 className={"filterBar"}>
                <h4 style={{color: "#0058DB", fontSize: "40px", fontWeight: "700"}}>창고 통계</h4>
                <div className={"warehouse"}>
                    <InputBox title="총 비품 개수" value={totalQuantity.toLocaleString()} readOnly/>
                    <InputBox title="총 비품 종류" value={totalKinds.toLocaleString()} readOnly/>
                </div>
            </div>

            <Popup isOpen={open} onClose={() => setOpen(false)} width="60%" height="auto">
                <ItemDetail code={selectedItemCode} isEditing={isEditing} setIsEditing={setIsEditing}
                            data={data} setData={setData} uStandardQty={uStandardQty}
                            setUStandardQty={setUStandardQty} uSafetyQty={uSafetyQty}
                            setUSafetyQty={setUSafetyQty} uStatus={uStatus}
                            setUStatus={setUStatus} updatedItem={updatedItem}/>
                <div className={"itemDetailBtnBox"}
                     style={{transition: "opacity 0.2s ease", opacity: isEditing ? 0.8 : 1}}>
                    {!isEditing ?
                        <>
                            {user.roleId >= 2 &&
                                <CustomButton color={"edit"} onClick={handleToggleEditing}>수정</CustomButton>}
                            <CustomButton color={"red"} onClick={() => setOpen(false)}>닫기</CustomButton>
                        </> :
                        <>
                            <CustomButton color={"save"} onClick={handleSave}>저장</CustomButton>
                            <CustomButton color={"cancel"} onClick={handleCancel}>취소</CustomButton>
                        </>
                    }
                </div>
            </Popup>
        </main>
    );
}

export default CurrentStockWarehouse;
