// NewItemAdd.jsx
import React, {useState} from "react";
import InputBox from "../../elements/filters/InputBox.jsx";
import Bar from "../../elements/Bar.jsx";
import SelectBox from "../../elements/filters/SelectBox.jsx";
import {CATEGORY_OPTIONS} from "../../elements/constants/constants.js";
import CustomButton from "../../elements/CustomButton.jsx";
import axios from "axios";
import {BASE_URL} from "../../elements/constants/constants.js";

function NewItemAdd({onClose, fetchItems}) {
    const [name, setName] = useState("");
    const [manufacturer, setManufacturer] = useState("");
    const [price, setPrice] = useState("");
    const [categoryId, setCategoryId] = useState("");

    const handleSubmit = async () => {
        if (!name.trim()) {
            alert("올바른 이름을 입력해주세요.");
            return;
        }
        if (!manufacturer.trim()) {
            alert("올바른 제조사를 입력해주세요.");
            return;
        }
        if (!price || isNaN(Number(price)) || Number(price) <= 0) {
            alert("올바른 가격을 입력해주세요.");
            return;
        }
        if (!categoryId) {
            alert("종류를 선택해주세요.");
            return;
        }

        try {
            const payload = {
                name,
                manufacturer,
                price: Number(price),
                categoryId: Number(categoryId),
            };

            await axios.post(BASE_URL + "/api/items", payload, {
                withCredentials: true,
            });

            alert("신규 비품 등록 완료");
            onClose();
            fetchItems();
        } catch (err) {
            console.error("신규 비품 등록 실패:", err.response?.data || err);
            alert("신규 비품 등록 실패");
        }
    };

    return (
        <>
            <h2
                className={"main-title"}
                style={{color: "#247CFF", marginBottom: "20px"}}
            >
                신규 비품 등록
            </h2>
            <div
                style={{
                    width: "100%",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    gap: "10px",
                }}
                className={"user-form-row"}
            >
                <InputBox title={"비품명"} value={name} setState={setName}/>
                <InputBox
                    title={"제조사"}
                    value={manufacturer}
                    setState={setManufacturer}
                />
                <InputBox title={"가격(원)"} value={price} setState={setPrice}/>
                <SelectBox
                    title="종류"
                    options={CATEGORY_OPTIONS.map((item, index) =>
                        index === 0 ? {value: 0, label: "종류 선택"} : item)}
                    setState={setCategoryId}
                />
            </div>
            <div style={{marginTop: 20, display: "flex", gap: 10}}>
                <CustomButton height="40px" width="70px" onClick={handleSubmit}>
                    등록
                </CustomButton>
                <CustomButton height="40px" width="70px" color="red" onClick={onClose}>
                    취소
                </CustomButton>
            </div>
        </>
    );
}

export default NewItemAdd;
