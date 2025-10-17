import axios from "axios";
import useUserStore from "../../utils/useUserStore.jsx";
import {BASE_URL} from "./constants/constants.js";
import {useEffect, useState} from "react";
import {InputBox, SelectBox} from "./filters/index.js";

function ItemDetail({
                        data,
                        setData,
                        updatedItem,
                        code,
                        isEditing,
                        uStandardQty,
                        setUSafetyQty,
                        setUStandardQty,
                        uSafetyQty,
                        uStatus,
                        setUStatus
                    }) {
    const user = useUserStore(state => state.user);
    const [loading, setLoading] = useState(true);
    const selectOptions = [
        {value: "ACTIVE", label: "활성화"},
        {value: "INACTIVE", label: "비활성화"}
    ];

    useEffect(() => {
        if (!code) return;

        setLoading(true);

        const itemRequest = axios.get(`${BASE_URL}/api/items/${code}`);
        const stockRequest = axios.get(`${BASE_URL}/api/stocks/${code}`);
        const minStockRequest = axios.get(`${BASE_URL}/api/min-stocks/item/${code}`, {withCredentials: true});

        Promise.all([itemRequest, stockRequest, minStockRequest])
            .then(([itemRes, stockRes, minStockRes]) => {
                const item = {
                    ...itemRes.data
                }
                const stockList = stockRes.data || [];

                // 창고별 수량 계산
                const itemData = {
                    name: item.name,
                    code: item.code,
                    manufacturer: item.manufacturer,
                    category: item.categoryName,
                    price: item.price,
                    totalQuantity: stockList.reduce((sum, s) => sum + (s.quantity || 0), 0),
                    AQty: stockList.find(s => s.warehouseName === "wh_a")?.quantity || 0,
                    BQty: stockList.find(s => s.warehouseName === "wh_b")?.quantity || 0,
                    CQty: stockList.find(s => !["wh_a", "wh_b"].includes(s.warehouseName))?.quantity || 0,
                    addDate: item.addDate || "",
                    addUserName: item.addUserName || "",
                    approveUserName: item.approveUserName || "",
                    safetyQty: minStockRes.data.safetyQty || 0,
                    standardQty: minStockRes.data.standardQty || 0,
                    status: item.status || ""
                };
                setData(itemData);
                setUStandardQty(itemData.standardQty);
                setUSafetyQty(itemData.safetyQty);
                setUStatus(itemData.status);
            })
            .catch(err => console.error(err))
            .finally(() => setLoading(false));
    }, [code, updatedItem]);

    if (loading) return <div>로딩 중...</div>;
    if (!data) return <div>조회된 데이터가 없습니다.</div>;

    return (
        <>
            <h2 className="main-title" style={{color: "#247CFF", marginBottom: "30px"}}>비품 상세내용</h2>
            <div className="itemDetailBox">
                <div className="detail-form-row">
                    <InputBox title="비품코드" value={data.code} width="100px" readOnly={true}/>
                    <InputBox title="비품명" value={data.name} width="120px" readOnly={true}/>
                    <InputBox title="제조사" value={data.manufacturer} width="100px" readOnly={true}/>
                </div>
                <div className="detail-form-row">
                    <InputBox title="종류" value={data.category} width="134px" readOnly={true}/>
                    <InputBox title="가격" value={data?.price ? `${data.price.toLocaleString()}원` : ""} width="150px"
                              readOnly={true}/>
                    <InputBox title="전체보유수량" value={data.totalQuantity} width="70px" readOnly={true}/>
                </div>
                <div className="detail-form-row">
                    <InputBox title="A창고 보유수량" value={data.AQty} width="55px" readOnly={true}/>
                    <InputBox title="B창고 보유수량" value={data.BQty} width="55px" readOnly={true}/>
                    <InputBox title="C창고 보유수량" value={data.CQty} width="55px" readOnly={true}/>
                </div>
                {user.roleId >= 2 &&
                    <>
                        <div className={"detail-form-row"}>
                            <InputBox title={"기준수량"} type={"number"}
                                      value={!isEditing ? data.standardQty : uStandardQty} setState={setUStandardQty}
                                      width="104px" min={"0"} step={"1"}
                                      readOnly={!isEditing}/>
                            <InputBox title={"안전수량"} type={"number"} value={!isEditing ? data.safetyQty : uSafetyQty}
                                      setState={setUSafetyQty}
                                      width="90px" min={"0"} step={"1"}
                                      readOnly={!isEditing}/>
                            <SelectBox
                                title="상태"
                                value={!isEditing ? data.status : uStatus || data.status}
                                setState={setUStatus}
                                options={selectOptions}
                                width="100px"
                                disabled={!isEditing}
                            />
                        </div>
                        <div className="detail-form-row">
                            <InputBox title="등록일"
                                      value={data.addDate ? new Date(data.addDate).toLocaleDateString() : ""}
                                      width="120px"
                                      readOnly={true}/>
                            <InputBox title="등록자" value={data.addUserName} width="130px" readOnly={true}/>
                            <InputBox title="결재자" value={data.approveUserName} width="110px" readOnly={true}/>
                        </div>
                    </>
                }
            </div>
        </>
    );
}

export default ItemDetail;
