// 비품 목록 페이지
// 비품 상세 팝업 필요

import axios from "axios";
import useUserStore from "../../../utils/useUserStore.jsx";
import {BASE_URL, CATEGORY_OPTIONS, ITEM_STATUS_OPTIONS} from "../../elements/constants/constants.js";
import {useEffect, useState} from "react";
import {DatePicker, InputBox, SelectBox} from "../../elements/filters/index.js";
import {CustomButton, FilterBar, ImageButton, ItemDetail, Popup, SearchButton, Sheets} from "../../elements/index.js";
import Bar from "../../elements/Bar.jsx";


function ItemList() {
    const user = useUserStore(state => state.user);
    const [keyword, setKeyword] = useState("");
    const [manufacturer, setManufacturer] = useState("");
    const [category, setCategory] = useState("");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [rowData, setRowData] = useState(undefined);
    const [status, setStatus] = useState("");
    const [open, setOpen] = useState(false);
    const [data, setData] = useState(null);
    const [selectedItemCode, setSelectedItemCode] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [uStandardQty, setUStandardQty] = useState("");
    const [uSafetyQty, setUSafetyQty] = useState("");
    const [uStatus, setUStatus] = useState("");
    const [updatedItem, setUpdatedItem] = useState(null);
    const [loading, setLoading] = useState(true);

    const columns = [
        {field: "index", headerName: "순번", flex: 0.7, align: "center"},
        {field: "code", headerName: "비품코드"},
        {field: "name", headerName: "비품명"},
        {field: "manufacturer", headerName: "제조사"},
        {field: "categoryName", headerName: "종류"},
        {
            field: "price", headerName: "가격(원)", valueFormatter: (params) => {
                if (params.value == null) return "";
                const num = Number(params.value);
                if (isNaN(num)) return params.value; // 숫자로 못 바꾸면 원래 값 그대로
                return num.toLocaleString("ko-KR") + "원";
            }, align: "right"
        },
        {field: "allQuantity", headerName: "전체 수량(EA)"},
        {
            field: "addDate", headerName: "등록일", valueFormatter: (params) => {
                if (!params.value) return "";
                return params.value.split("T")[0]
            }, flex: 0.8
        },
        {
            field: "status",
            headerName: "상태",
            valueFormatter: (params) => {
                const option = ITEM_STATUS_OPTIONS.find(opt => opt.value === params.value);
                return option ? option.label : params.value;
            },
            flex: 0.7
        },
        {
            field: "qr",
            headerName: "QR",
            flex: 0.5,
            align: "center",
            filter: false,
            cellRenderer: (params) => params.value
        }
    ]

    useEffect(() => {
        setLoading(true)
        axios.get(BASE_URL + "/api/items/web", {withCredentials: true})
            .then(res => {
                const dataWithIndex = res.data.map((item, index) => ({
                    ...item,
                    index: index + 1,
                    qr: item.code &&
                        <ImageButton ImageUrl={"/icon_download.png"}
                                     link={BASE_URL + "/api/qr/download?code=" + item.code} alt={item.name}/>
                }));
                setRowData(dataWithIndex);
            })
            .catch(err => console.error("데이터를 불러오지 못했습니다.:", err.data))
            .finally(() => {
                setLoading(false)
            })
    }, []);

    const handleToggleEditing = () => {
        if (data) setIsEditing(!isEditing);
    }

    const handleCellClick = (clickedCell) => {
        setData(null)
        if (!clickedCell.data.code) {
            alert(""
                + "* 승인되지 않은 비품 입니다 *\n"
                + "\n비품명: " + clickedCell.data.name + " / 제조사: " + clickedCell.data.manufacturer
                + "\n등록자: " + clickedCell.data.addUserName + " / 등록일: " + clickedCell.data.addDate.split("T")[0]
            )
            return;
        }
        setSelectedItemCode(clickedCell.data.code); // 선택한 비품 코드 저장
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

            if (!Number.isInteger(num) || num < 0) {
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
                handleSearch()
            })
            .catch(err => {
                console.log(err.response.data);
            })
            .finally(() => {
                setIsEditing(false);
            })
    }

    const handleSearch = async () => {
        setLoading(true)
        const params = {};

        if (keyword) params.name = keyword;
        if (manufacturer) params.manufacturer = manufacturer;
        if (category) params.categoryId = category;
        if (status) params.status = status;
        if (startDate) params.startDate = `${startDate}T00:00:00`;
        if (endDate) params.endDate = `${endDate}T23:59:59`;

        try {
            const response = await axios.get(BASE_URL + "/api/items/web", {params, withCredentials: true});
            const dataWithIndex = response.data.map((item, index) => ({
                ...item,
                index: index + 1,
                qr: item.code &&
                    <ImageButton ImageUrl={"/icon_download.png"} link={BASE_URL + "/api/qr/download?code=" + item.code}
                                 alt={item.name}/>
            }));
            setRowData(dataWithIndex);
        } catch (err) {
            console.error("검색 실패", err.data);
        } finally {
            setLoading(false)
        }
    };


    return (
        <main className={"main"}>
            <h2 className="main-title">비품 목록</h2>
            <FilterBar>
                <div className="filter-bar-scroll">
                    <InputBox title={'비품명'} setState={setKeyword}/>
                    <Bar/>
                    <InputBox title={'제조사'} setState={setManufacturer}/>
                    <Bar/>
                    <SelectBox title={'종류'} options={CATEGORY_OPTIONS} setState={setCategory}/>
                    <Bar/>
                    {user.roleId >= 2 && <SelectBox title={'상태'} options={ITEM_STATUS_OPTIONS} setState={setStatus}/>}
                    <Bar/>
                    <DatePicker title={"등록일"} setStartDate={setStartDate} setEndDate={setEndDate} margin={"20px"}
                                width={"auto"} value={{startDate, endDate}}/>
                </div>
                <SearchButton onClick={handleSearch}/>
            </FilterBar>
            <Sheets rows={13} columns={columns} rowData={rowData} onCellClick={handleCellClick} loading={loading}
                    clickableFields={["code", "name"]}/>
            <Popup isOpen={open} onClose={() => {
                setSelectedItemCode(null)
                setOpen(false)
            }} width="60%" height="auto">
                <ItemDetail code={selectedItemCode} isEditing={isEditing} setIsEditing={setIsEditing}
                            data={data} setData={setData} uStandardQty={uStandardQty}
                            setUStandardQty={setUStandardQty} uSafetyQty={uSafetyQty}
                            setUSafetyQty={setUSafetyQty} uStatus={uStatus}
                            setUStatus={setUStatus} updatedItem={updatedItem}/>
                <div className={"itemDetailBtnBox"}
                     style={{transition: "opacity 0.2s ease", opacity: isEditing ? 0.8 : 1}}>
                    {!isEditing ?
                        <>
                            {(user.roleId >= 2 && data) &&
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
    )
}

export default ItemList;
