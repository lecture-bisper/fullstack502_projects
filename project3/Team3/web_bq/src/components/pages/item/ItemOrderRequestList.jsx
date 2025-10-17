// 발주 요청 현황
// 발주 요청 상세 팝업 필요

import FilterBar from "../../elements/FilterBar.jsx";
import {Sheets} from "../../elements/index.js";
import InputBox from "../../elements/filters/InputBox.jsx";
import {useEffect, useState} from "react";
import {BASE_URL, CATEGORY_OPTIONS, REQUEST_STATUS_OPTIONS} from "../../elements/constants/constants.js";
import {DatePicker, SelectBox} from "../../elements/filters/index.js";
import Popup from "../../elements/Popup.jsx";
import CustomButton from "../../elements/CustomButton.jsx";
import axios from "axios";
import Bar from "../../elements/Bar.jsx";
import SearchButton from "../../elements/SearchButton.jsx";

function ItemOrderRequestList() {
    const [open, setOpen] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [keyword, setKeyword] = useState("");
    const [manufacturer, setManufacturer] = useState("");
    const [category, setCategory] = useState("");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [rowData, setRowData] = useState(undefined);
    const [status, setStatus] = useState("");
    const [loading, setLoading] = useState(true)

    const STATUS_LABELS = REQUEST_STATUS_OPTIONS.reduce((acc, cur) => {
        acc[cur.value] = cur.label;
        return acc;
    }, {});

    const columns = [
        {field: "index", headerName: "순번", flex: 0.1, align: "center"},
        {field: "code", headerName: "비품코드"},
        {field: "name", headerName: "비품명"},
        {field: "manufacturer", headerName: "제조사"},
        {field: "categoryKrName", headerName: "종류"},
        {field: "requestQty", headerName: "요청수량(EA)"},
        {
            field: "price", headerName: "가격(원)",
            valueFormatter: (params) => {
                if (params.value == null) return "";
                const num = Number(params.value);
                if (isNaN(num)) return params.value;
                return num.toLocaleString("ko-KR") + "원";
            }, align: "right"
        },
        {
            field: "totalPrice", headerName: "총 금액(원)", valueFormatter: (params) => {
                if (params.value == null) return "";
                const num = Number(params.value);
                if (isNaN(num)) return params.value;
                return num.toLocaleString("ko-KR") + "원";
            }, align: "right"
        },
        {field: "requestUserName", headerName: "요청자"},
        {
            field: "requestDate", headerName: "요청일", valueFormatter: (params) => {
                if (!params.value) return "";
                return (params.value || "").split("T")[0];
            }
        },
        {
            field: "status", headerName: "상태", valueFormatter: (params) => STATUS_LABELS[params.value] || params.value,
            renderCell: (params) => {
                const label = STATUS_LABELS[params.value] || params.value;
                const isRequested = params.value === "REQUESTED";
                return (
                    <span style={{color: isRequested ? "red" : "inherit"}}>
        {label}
      </span>
                );
            }, flex: 0.7
        }
    ];

    const handleRowClick = (row) => {
        // 승인된 건은 수정 불가하게 아예 팝업 차단함
        if (row.status === "APPROVED" || row.status === "REJECTED") {
            return;
        }
        setSelectedOrder(row);
        setOpen(true);
    };

    const handleSearch = async () => {
        setLoading(true)
        const params = {};

        if (keyword) params.keyword = keyword;
        if (manufacturer) params.manufacturer = manufacturer;
        if (category) params.categoryId = category;
        if (status) params.status = status;
        if (startDate) params.startDate = `${startDate}T00:00:00`;
        if (endDate) params.endDate = `${endDate}T23:59:59`;

        try {
            const response = await axios.get(BASE_URL + "/api/orders", {params, withCredentials: true});
            setRowData(response.data);
        } catch (err) {
            console.error("검색 실패:", err);
            setRowData(null);
        } finally {
            setLoading(false)
        }
    };

    const handleApprove = async () => {
        try {
            const response = await axios.post(BASE_URL + `/api/orders/${selectedOrder.id}/approve`,
                {},
                {withCredentials: true}
            );
            setSelectedOrder(prev => ({
                ...prev,
                ...response.data,
            }));
            setOpen(false);

            await fetchOrders();
        } catch (err) {
            console.error("결재 실패:", err);
        }
    }

    const handleReject = async () => {
        try {
            const response = await axios.post(BASE_URL + `/api/orders/${selectedOrder.id}/reject`,
                {},
                {withCredentials: true}
            );
            setSelectedOrder(prev => ({
                ...prev,
                ...response.data,
            }));

            setOpen(false);

            await fetchOrders();
        } catch (err) {
            console.error("반려 실패:", err)
        }
    }

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        setLoading(true)
        try {
            const response = await axios.get(BASE_URL + "/api/orders", {
                withCredentials: true,
            });
            setRowData(response.data);
        } catch (err) {
            console.error("발주 요청 조회 실패:", err);
            setRowData(null);
        } finally {
            setLoading(false)
        }
    };


    return (
        <main className={"main"}>
            <h2 className="main-title">발주 요청 현황 조회</h2>
            <FilterBar>
                <div className="filter-bar-scroll">
                    <InputBox title={"비품명"} setState={setKeyword}/>
                    <Bar/>
                    <InputBox title={"제조사"} setState={setManufacturer}/>
                    <Bar/>
                    <SelectBox title={"종류"} options={CATEGORY_OPTIONS} setState={setCategory}/>
                    <Bar/>
                    <SelectBox title={"상태"} options={REQUEST_STATUS_OPTIONS}
                               setState={(opt) => setStatus(opt?.value ?? opt)}/>
                    <Bar/>
                    <DatePicker title={"기간"} setStartDate={setStartDate} setEndDate={setEndDate} value={{startDate, endDate}}/>
                </div>
                <SearchButton onClick={handleSearch}>검색</SearchButton>
            </FilterBar>

            <Sheets rows={13} columns={columns} rowData={rowData ? rowData.map((data, index) => {
                return {...data, index: index + 1, totalPrice: data.requestQty * data.price}
            }) : rowData} onCellClick={(e) => handleRowClick(e.data)} loading={loading}/>
            <div style={{textAlign: "center", marginTop: "50px"}}>
                <Popup isOpen={open} onClose={() => setOpen(false)} width="80%" height="auto">
                    <div className="orderDetailBox">
                        <h2 className="title">비품 발주 결재</h2>
                        <div className="itemDetailSection">
                            <InputBox title="비품명" value={selectedOrder?.name || ""} readOnly/>
                            <InputBox title="비품 코드" value={selectedOrder?.code || ""} readOnly/>
                            <InputBox title="제조사" value={selectedOrder?.manufacturer || ""} readOnly/>
                            <InputBox title="종류" value={selectedOrder?.categoryKrName || ""} readOnly/>
                            <InputBox
                                title="가격"
                                value={selectedOrder ? selectedOrder.price.toLocaleString("ko-KR") + "원" : ""}
                                readOnly
                            />
                        </div>
                        <div className="itemDetailSection">
                            <InputBox title="비품 기준 수량" value={selectedOrder?.standardQty || "0"} readOnly/>
                            <InputBox title="비품 안전 수량" value={selectedOrder?.safetyQty || "0"} readOnly/>
                            <InputBox title="현재 보유 수량" value={selectedOrder?.stockQuantity || "0"} readOnly/>
                            <InputBox title="요청 수량" value={selectedOrder?.requestQty || "0"} readOnly/>
                        </div>
                        <div className="itemDetailSection">
                            <InputBox
                                title="총 금액"
                                value={
                                    selectedOrder
                                        ? (Number(selectedOrder.price || 0) * Number(selectedOrder.requestQty || 0)).toLocaleString("ko-KR") + "원"
                                        : "0원"
                                }
                                readOnly
                            />
                            <InputBox title="요청자" value={selectedOrder?.requestUserName || ""} readOnly/>
                            <InputBox title="승인자" value={selectedOrder?.approveUserName || ""} readOnly/>
                        </div>
                        <div style={{marginTop: "50px"}}>
                            <label>내용</label>
                            <textarea
                                style={{width: "98%", height: "120px", padding: "10px", marginBottom: "10px"}}
                                value={selectedOrder?.comment || ""}
                                readOnly
                            />
                        </div>
                        <div style={{marginTop: "20px", display: "flex", justifyContent: "center", gap: "10px"}}>
                            <CustomButton onClick={handleApprove}>승인</CustomButton>
                            <CustomButton color="red" onClick={handleReject}>반려</CustomButton>
                            <CustomButton color={"gray"} onClick={() => setOpen(false)}>닫기</CustomButton>
                        </div>
                    </div>
                </Popup>
            </div>
        </main>
    )
}

export default ItemOrderRequestList;