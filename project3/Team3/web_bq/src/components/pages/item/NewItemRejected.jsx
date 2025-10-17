// / NewItemRejected.jsx

import InputBox from "../../elements/filters/InputBox.jsx";
import Bar from "../../elements/Bar.jsx";
import SelectBox from "../../elements/filters/SelectBox.jsx";
import { BASE_URL, CATEGORY_OPTIONS } from "../../elements/constants/constants.js";
import CustomButton from "../../elements/CustomButton.jsx";
import React, { useState } from "react";
import axios from "axios";

function NewItemRejected({ item, onClose, fetchItems }) {
    const [name, setName] = useState(item.name);
    const [manufacturer, setManufacturer] = useState(item.manufacturer);
    const [price, setPrice] = useState(item.price);
    const [categoryId, setCategoryId] = useState(item.categoryId);

    const handleEdit = async () => {
        try {
            await axios.patch(BASE_URL + `/api/items/${item.id}`,
                {
                    status: "PENDING",
                    name,
                    manufacturer,
                    price,
                    categoryId
                },
                { withCredentials: true }
            );
            alert("수정 완료.");
            fetchItems();
            onClose();
        } catch (err) {
            console.error("상태 변경 실패:", err.response?.data || err);
            alert("형식에 맞춰 입력하시오.");
        }
    };

    if (!item) return null;

    return (
        <>
            <h2 className="main-title" style={{ color: "#247CFF", marginBottom: 20 }}>
                신규 비품 등록
            </h2>

            <div style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center", gap: 10 }} className={"user-form-row"}>
                <InputBox title="비품명" value={name} setState={setName} />
                <InputBox title="제조사" value={manufacturer} setState={setManufacturer} />
                <InputBox title="가격(원)" value={Number(price) ? Number(price).toLocaleString() : "0"} setState={(val) => setPrice(val.replace(/,/g, ""))} />
                <SelectBox title="종류" options={CATEGORY_OPTIONS.slice(1)} value={categoryId} setState={setCategoryId} />
            </div>

            <div style={{ marginTop: 20, display: "flex", gap: 10 }}>
                <CustomButton height="40px" width="70px" onClick={handleEdit}>
                    수정
                </CustomButton>
                <CustomButton height="40px" width="70px" color="red" onClick={onClose}>
                    취소
                </CustomButton>
            </div>
        </>
    );
}

export default NewItemRejected;
