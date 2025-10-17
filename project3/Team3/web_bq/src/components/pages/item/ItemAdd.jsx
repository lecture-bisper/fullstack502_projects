// ItemAdd.jsx
import axios from "axios";
import {BASE_URL, CATEGORY_OPTIONS, ITEM_ADD_STATUS_OPTIONS,} from "../../elements/constants/constants.js";
import {useState, useEffect} from "react";
import {DatePicker, InputBox, SelectBox} from "../../elements/filters/index.js";
import {Bar, CustomButton, FilterBar, SearchButton, Sheets} from "../../elements/index.js";
import NewItemAdd from "./NewItemAdd.jsx";
import NewItemRejected from "./NewItemRejected.jsx";
import NewItemPending from "./NewItemPending.jsx";
import Popup from "../../elements/Popup.jsx";

function ItemAdd() {
    const [openPendingPopup, setOpenPendingPopup] = useState(false);
    const [openRejectedPopup, setOpenRejectedPopup] = useState(false);
    const [selectedItem, setSelectedItem] = useState(null);
    const [open, setOpen] = useState(false);
    const [originalData, setOriginalData] = useState([]);
    const [rowData, setRowData] = useState([]);
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [loading, setLoading] = useState(true)
    const [filters, setFilters] = useState({
        name: "",
        manufacturer: "",
        categoryId: "",
        status: "",
    });
    const statusMap = {ACTIVE: "처리완료", PENDING: "처리중", REJECTED: "반려"};
    const columns = [
        {field: "index", headerName: "순번", flex: 0.6, align: "center"},
        {field: "name", headerName: "비품명", flex: 2},
        {field: "manufacturer", headerName: "제조사"},
        {field: "categoryName", headerName: "종류"},
        {
            field: "price", headerName: "가격(원)", align: "right", valueFormatter: (params) => {
                if (params.value == null) return "";
                const num = Number(params.value);
                if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                return num.toLocaleString("ko-KR") + "원";
            }
        },
        {field: "addDate", headerName: "요청일"},
        {field: "addUserName", headerName: "요청자", flex: 0.6},
        {field: "approveUserName", headerName: "결재자", flex: 0.6},
        {
            field: "status",
            headerName: "상태",
            flex: 0.6,
            renderCell: (params) => {
                const display = statusMap[params.data.status] || params.data.status;
                const clickable = display === "처리중" || display === "반려";

                return (
                    <span
                        style={{
                            color: clickable ? "#0058db" : "black",
                            fontWeight: clickable ? "600" : "default",
                            textDecoration: clickable ? "underline" : "default",
                            cursor: clickable ? "pointer" : "default"
                        }}
                        onClick={() => {
                            if (display === "처리중") {
                                setSelectedItem(params.data);
                                setOpenPendingPopup(true);
                            } else if (display === "반려") {
                                setSelectedItem(params.data);
                                setOpenRejectedPopup(true);
                            }
                        }}
                    >
                            {display}
                        </span>
                );
            }
        }
    ];

    // API 데이터 가져오기
    const fetchItems = async () => {
        try {
            setLoading(true)
            const res = await axios.get(BASE_URL + "/api/items/web", {withCredentials: true});
            const data = Array.isArray(res.data) ? res.data : [];

            const numbered = data
                .filter(item => item.status !== "INACTIVE")
                .map((item, idx) => ({
                    ...item,
                    index: idx + 1,  // ← 테이블용 순번
                    addDate: item.addDate ? item.addDate.split("T")[0] : ""
                }));
            setOriginalData(numbered);
            setRowData(numbered);
        } catch (err) {
            console.error("비품 조회 실패:", err.response?.data || err);
            alert("비품 조회 실패");
        } finally {
            setLoading(false)
        }
    };

    useEffect(() => {
        fetchItems();
    }, []);

    const handleSearch = () => {
        setLoading(true)
        const filtered = originalData.filter(item => {
            const nameMatch =
                !filters.name || item.name?.includes(filters.name);
            const manufacturerMatch =
                !filters.manufacturer || item.manufacturer?.includes(filters.manufacturer);
            const categoryMatch =
                !filters.categoryId || item.categoryId === filters.categoryId;
            const statusMatch =
                !filters.status || item.status === filters.status;

            const addDate = new Date(item.addDate);
            const start = startDate ? new Date(startDate) : null;
            const end = endDate ? new Date(endDate) : null;
            const dateMatch = (!start || addDate >= start) && (!end || addDate <= end);
            return nameMatch && manufacturerMatch && categoryMatch && statusMatch && dateMatch;
        });

        setRowData(filtered.map((item, i) => ({...item, index: i + 1})));
        setLoading(false)
    };

    return (
        <main className="main">
            <h2 className="main-title">신규 비품 요청 현황</h2>
            <div style={{
                width: "100%",
                display: "flex",
                justifyContent: "center",
                flexDirection: "column",
                alignItems: "center"
            }}>
                <div style={{width: "90%", display: "flex", justifyContent: "flex-end", marginBottom: "5px"}}>
                    <CustomButton width="130px" color="deepBlue" onClick={() => setOpen(true)}>신규 비품 등록</CustomButton>
                </div>
                <FilterBar>
                    <div className="filter-bar-scroll">
                        <InputBox
                            title="비품명"
                            value={filters.name}
                            setState={(val) => setFilters(prev => ({...prev, name: val}))}
                        />
                        <Bar/>
                        <InputBox
                            title="제조사"
                            value={filters.manufacturer}
                            setState={(val) => setFilters(prev => ({...prev, manufacturer: val}))}
                        />
                        <Bar/>
                        <SelectBox
                            title="종류"
                            setState={(val) => setFilters(prev => ({...prev, categoryId: Number(val)}))}
                            options={CATEGORY_OPTIONS}
                        />
                        <Bar/>
                        <SelectBox
                            title="상태"
                            options={ITEM_ADD_STATUS_OPTIONS}
                            setState={(val) => setFilters(prev => ({...prev, status: val}))}
                        />
                        <Bar/>
                        <DatePicker
                            title="기간"
                            value={{startDate, endDate}}
                            setStartDate={setStartDate}
                            setEndDate={setEndDate}
                        />
                    </div>
                    <SearchButton onClick={handleSearch}/>
                </FilterBar>
            </div>
            <Sheets rows={13} columns={columns} rowData={rowData} loading={loading}/>
            <Popup isOpen={open} onClose={() => setOpen(false)} width="55%" height="auto">
                <NewItemAdd onClose={() => setOpen(false)} fetchItems={fetchItems}/>
            </Popup>
            <Popup isOpen={openPendingPopup} onClose={() => setOpenPendingPopup(false)} width="55%" height="auto">
                <NewItemPending item={selectedItem} onClose={() => setOpenPendingPopup(false)} fetchItems={fetchItems}/>
            </Popup>
            <Popup isOpen={openRejectedPopup} onClose={() => setOpenRejectedPopup(false)} width="55%" height="auto">
                <NewItemRejected item={selectedItem} onClose={() => setOpenRejectedPopup(false)}
                                 fetchItems={fetchItems}/>
            </Popup>
        </main>
    );
}

export default ItemAdd;
