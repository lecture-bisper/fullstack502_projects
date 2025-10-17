// 발주 요청
// 발주 요청 작성 팝업

import axios from "axios";
import useUserStore from "../../../utils/useUserStore.jsx";
import {BASE_URL, CATEGORY_OPTIONS, MIN_STOCK_STATUS_OPTIONS,} from "../../elements/constants/constants.js";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {InputBox, SelectBox} from "../../elements/filters/index.js";
import {Bar, CustomButton, FilterBar, Popup, SearchButton, Sheets} from "../../elements/index.js";


function ItemOrderRequest() {
    const {user} = useUserStore();
    const [selectedItem, setSelectedItem] = useState(null);
    const [loading, setLoading] = useState(true)
    const navigate = useNavigate();

    const [minStockStatus, setMinStockStatus] = useState("");
    const [keyword, setKeyword] = useState("");
    const [manufacturer, setManufacturer] = useState("");
    const [category, setCategory] = useState("");
    const [rowData, setRowData] = useState(undefined);
    const [open, setOpen] = useState(false);
    const [requestQty, setRequestQty] = useState("");
    const [comment, setComment] = useState("");

    const STATUS_LABELS = MIN_STOCK_STATUS_OPTIONS.reduce((acc, cur) => {
        acc[cur.value] = cur.label;
        return acc;
    }, {});

    const columns = [
        {field: "index", headerName: "순번", flex: 0.7, align: "center"},
        {field: "code", headerName: "비품코드"},
        {field: "name", headerName: "비품명", flex: 1.8},
        {field: "manufacturer", headerName: "제조사"},
        {field: "categoryKrName", headerName: "종류"},
        {
            field: "price",
            headerName: "가격(원)",
            valueFormatter: (params) => {
                if (params.value == null) return "";
                const num = Number(params.value);
                if (isNaN(num)) return params.value;
                return num.toLocaleString("ko-KR") + "원";
            }, align: "right"
        },
        {field: "stockQuantity", headerName: "현재 재고(EA)"},
        {field: "standardQty", headerName: "기준 재고(EA)"},
        {field: "safetyQty", headerName: "안전 재고(EA)"},
        {
            field: "minStockStatus",
            headerName: "상태",
            valueFormatter: (params) => STATUS_LABELS[params.value] || params.value,
            cellStyle: (params) => {
                if (params.data.minStockStatus === "LOW") return {color: "red", fontWeight: "bold"};
                if (params.data.minStockStatus === "OK") return {color: "green"}
                if (params.data.minStockStatus === "PENDING") return {color: "blue"}
            },
            flex: 0.8,
        },
        {
            field: "orderRequest", headerName: "발주요청", align: "center",
            renderCell: (params) => {
                if (!params.data.id) return null;
                return (
                    <CustomButton
                        width="100%"
                        onClick={() => handleRowClick(params.data)}>
                        발주요청
                    </CustomButton>);
            }
        },
    ];

    useEffect(() => {
        fetchLowStockItems();
    }, []);

    const fetchLowStockItems = async () => {
        try {
            setLoading(true)
            const response = await axios.get(BASE_URL + "/api/request/order", {
                withCredentials: true
            });

            const sanitizedData = response.data.map(item =>
                Object.fromEntries(
                    Object.entries(item).map(([key, value]) => [
                        key,
                        value ?? (typeof value === "number" ? 0 : "")
                    ])
                )
            );
            setRowData(sanitizedData.map((item, index) => {
                return {...item, index: index + 1};
            }));
        } catch (err) {
            console.error("초기 데이터 로딩 실패:", err);
        } finally {
            setLoading(false)
        }
    };

    const handleSearch = async () => {
        setLoading(true)
        const params = {};

        if (keyword) params.keyword = keyword;
        if (manufacturer) params.manufacturer = manufacturer;
        if (category) params.categoryId = category;
        if (minStockStatus) params.minStockStatus = minStockStatus;

        try {
            const response = await axios.get(BASE_URL + "/api/request/order", {params, withCredentials: true});
            setRowData(response.data.map((item, index) => {
                return {...item, index: index + 1}
            }));
        } catch (err) {
            console.error("검색 실패:", err);
        } finally {
            setLoading(false)
        }
    };

    const handleRowClick = (row) => {
        setSelectedItem(row);
        setRequestQty("");
        setComment("");
        setOpen(true);
    };


    const handleOrderSubmit = async () => {
        if (!requestQty) {
            alert("요청 수량을 입력하세요.");
            return;
        }

        const payload = {
            itemId: selectedItem?.id,
            name: selectedItem?.name,
            code: selectedItem?.code,
            manufacturer: selectedItem?.manufacturer,
            category: selectedItem?.categoryName,
            categoryKrName: selectedItem?.categoryKrName,
            requestQty: Number(requestQty),
            price: selectedItem?.price,
            requestUser: user?.empCode,
            comment: comment,
        };

        try {
            await axios.post(BASE_URL + "/api/orders", payload, {
                withCredentials: true
            });

            setRowData((prev) =>
                prev.map((item) =>
                    item.id === selectedItem?.id
                        ? {...item, minStockStatus: "REQUESTED"}
                        : item
                )
            );


            alert("발주 요청이 등록되었습니다.");
            setOpen(false);

            if (user?.roleName === "ADMIN") {
                navigate("/items/orders");
            } else {
                setRowData((prev) =>
                    prev.map((item) =>
                        item.id === selectedItem?.id ? {...item, minStockStatus: "PENDING"} : item)
                );
            }
        } catch (err) {
            console.error("발주 요청 실패:", err);
            alert("발주 요청에 실패했습니다.");
        }
    };


    return (
        <main className={"main"}>
            <h2 className="main-title">비품 발주 요청</h2>
            <FilterBar>
                <div className="filter-bar-scroll">
                    <InputBox title={"비품명"} setState={setKeyword}/>
                    <Bar/>
                    <InputBox title={"제조사"} setState={setManufacturer}/>
                    <Bar/>
                    <SelectBox title={"종류"} options={CATEGORY_OPTIONS} setState={setCategory}/>
                    <Bar/>
                    <SelectBox title={"상태"} options={MIN_STOCK_STATUS_OPTIONS} setState={setMinStockStatus}/>
                </div>
                <SearchButton onClick={handleSearch}>검색</SearchButton>
            </FilterBar>

            <Sheets rows={13} columns={columns} rowData={rowData} loading={loading}/>

            <div style={{textAlign: "center", marginTop: "50px"}}>
                <Popup isOpen={open} onClose={() => setOpen(false)} width="80%" height="auto">
                    <div className="orderDetailBox">
                        <h2 className={"main-title"} style={{color: "#247CFF", marginBottom: "30px"}}>비품 발주 요청 상세</h2>

                        <div className="order-form-row">
                            <InputBox title="이름" value={selectedItem?.name || ""} readOnly/>
                            <InputBox title="비품코드" value={selectedItem?.code || ""} readOnly/>
                            <InputBox title="제조사" value={selectedItem?.manufacturer || ""} readOnly/>
                            <InputBox title="종류" value={selectedItem?.categoryKrName || ""} readOnly/>
                            <InputBox
                                title="가격"
                                value={
                                    selectedItem?.price
                                        ? selectedItem.price.toLocaleString("ko-KR") + "원"
                                        : ""
                                }
                                readOnly
                            />
                        </div>

                        <div className="order-form-row">
                            <InputBox title="비품기준수량" value={selectedItem?.standardQty || 0} readOnly/>
                            <InputBox title="비품안전수량" value={selectedItem?.safetyQty || 0} readOnly/>
                            <InputBox title="현재보유수량" value={selectedItem?.stockQuantity || 0} readOnly/>
                            <InputBox title="요청수량"
                                      value={requestQty}
                                      type="number"
                                      min={1}
                                      setState={setRequestQty}
                            />
                        </div>

                        <div className="order-form-row">
                            <InputBox
                                title="총 예상 금액"
                                value={
                                    selectedItem
                                        ? (Number(selectedItem.price || 0) * Number(requestQty || 0)).toLocaleString("ko-KR") +
                                        "원"
                                        : "0원"
                                }
                                readOnly
                            />
                            <InputBox title="요청자" value={user?.empName || ""} readOnly/>
                        </div>

                        <div style={{marginTop: "15px"}}>
                            <label>내용</label>
                            <textarea
                                style={{width: "100%", height: "120px", padding: "10px", marginBottom: "10px"}}
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                            />
                        </div>
                    </div>

                    <div style={{marginTop: "20px", display: "flex", gap: "10px"}}>
                        <CustomButton onClick={handleOrderSubmit}>요청</CustomButton>
                        <CustomButton color={"red"} onClick={() => setOpen(false)}>닫기</CustomButton>
                    </div>
                </Popup>
            </div>
        </main>
    );
}

export default ItemOrderRequest;
