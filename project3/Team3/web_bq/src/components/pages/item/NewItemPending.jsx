import React, {useEffect, useState} from "react";
import InputBox from "../../elements/filters/InputBox.jsx";
import Bar from "../../elements/Bar.jsx";
import {CATEGORY_OPTIONS, BASE_URL} from "../../elements/constants/constants.js";
import CustomButton from "../../elements/CustomButton.jsx";
import axios from "axios";

function NewItemPending({item, onClose, fetchItems}) {
    const [userRole, setUserRole] = useState(""); // 유저 권한 상태

    // 페이지 로드 시 유저 권한 가져오기
    useEffect(() => {
        const fetchUserRole = async () => {
            try {
                const res = await axios.get(BASE_URL + "/api/users/me", {withCredentials: true});
                setUserRole(res.data.roleName || "");
            } catch (err) {
                console.error("유저 권한 조회 실패:", err.response?.data || err);
            }
        };
        fetchUserRole();
    }, []);

    const handleApprove = async () => {
        try {
            await axios.post(BASE_URL + `/api/items/${item.id}/approve`, null, {withCredentials: true});
            alert("승인 처리 완료");
            fetchItems();
            onClose();
        } catch (err) {
            console.error("승인 실패:", err.response?.data || err);
            alert("승인 실패");
        }
    };

    const handleReject = async () => {
        try {
            await axios.post(BASE_URL + `/api/items/${item.id}/reject`, null, {withCredentials: true});
            alert("반려 처리 완료");
            fetchItems();
            onClose();
        } catch (err) {
            console.error("반려 실패:", err.response?.data || err);
            alert("반려 실패");
        }
    };

    const handleDelete = async () => {
        try {
            await axios.delete(BASE_URL + `/api/items/${item.id}`, {withCredentials: true});
            alert("삭제 처리 완료");
            fetchItems();
            onClose();
        } catch (err) {
            console.error("삭제 실패:", err.response.data || err);
            alert("삭제 실패");
        }
    };

    if (!item) return null;

    const isManager = userRole === "MANAGER";

    return (
        <>
            <h2 className="main-title" style={{color: "#247CFF", marginBottom: "20px"}}>
                신규 비품 등록
            </h2>

            <div style={{width: "100%", display: "flex", justifyContent: "center", alignItems: "center", gap: "10px"}} className={"user-form-row"}>
                <InputBox title="비품명" value={item.name} readOnly/>
                <InputBox title="제조사" value={item.manufacturer} readOnly/>
                <InputBox title="가격(원)" value={`${Number(item.price).toLocaleString()}원`} readOnly/>
                <InputBox title="종류" value={CATEGORY_OPTIONS.find(opt => opt.value === item.categoryId)?.label || ""} readOnly/>
            </div>

            <div className={"pending-dtn-form-row"}>
                {!isManager && (
                    <div className={"manager-btn"}>
                        <CustomButton height="40px" width="70px" onClick={handleApprove}>승인</CustomButton>
                        <CustomButton height="40px" width="70px" color="gray" onClick={handleReject}>반려</CustomButton>
                        <CustomButton height="40px" width="70px" color="deepRed" onClick={handleDelete}>삭제</CustomButton>
                    </div>
                )}
                <div className={"cancel"}>
                    <CustomButton height="40px" width="70px" color={isManager ? "gray" : "red"} onClick={onClose}>
                        취소
                    </CustomButton>
                </div>
            </div>
        </>
    );
}

export default NewItemPending;
