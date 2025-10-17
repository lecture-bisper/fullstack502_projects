import axios from "axios";
import useUserStore from "../../../utils/useUserStore.jsx";
import {BASE_URL} from "../../elements/constants/constants.js";
import {useState} from "react";
import {CustomButton, Popup, WhiteBox} from "../../elements/index.js";
import {ChangePass} from "./index.js";
import {InputBox2} from "../../elements/filters/index.js";
import InputBox3 from "../../elements/filters/inputBox3.jsx";


function MyPage() {
    const [open, setOpen] = useState(false);
    const [editingField, setEditingField] = useState(null); // "empEmail" 또는 "empPhone"
    const [tempValue, setTempValue] = useState("");
    const user = useUserStore((state) => state.user);
    const setUser = useUserStore((state) => state.setUser);

    // 비밀번호 변경
    const updatePassword = async ({curPassword, newPassword}) => {
        try {
            await axios.put(
                BASE_URL + "/api/users/me/pwd",
                {curPassword, newPassword},
                {withCredentials: true}
            );
            alert("비밀번호가 변경되었습니다.");
            setOpen(false);
        } catch (err) {
            console.error(err.response?.data || err);
            alert("비밀번호 변경 실패");
        }
    };

    // 이메일 / 전화번호 변경
    const updateInfo = async (field) => {
        if (!tempValue) {
            alert("값을 입력해주세요.");
            return;
        }

        const data = {
            email: field === "empEmail" ? tempValue : user?.empEmail,
            phone: field === "empPhone" ? tempValue : user?.empPhone,
        };

        try {
            await axios.put(BASE_URL + "/api/users/me", data, {withCredentials: true});
            // 상태 업데이트
            setUser({...user, [field]: tempValue});
            setEditingField(null);
            setTempValue("");
            alert("정보가 변경되었습니다.");
        } catch (err) {
            console.error(err.response?.data || err);
            alert("정보 변경 실패");
        }
    };

    return (
        <main className="main">
            <WhiteBox width="30%">
                <p className="main-title" style={{color: "#0058DB"}}>마이 페이지</p>
                <div style={{display: "flex", flexDirection: "column", width: "100%", gap: "40px", padding: "0px 50px"}}>

                    <InputBox2 title="사번:" value={user?.empCode || ""} readOnly={true} direction="row" fontSize="23px" height="40px"/>

                    <div style={{display: "flex", gap: "10px"}}>
                        <InputBox3 title="비밀번호:" value="*******" readOnly direction="row" fontSize="23px" height="40px"/>
                        <CustomButton width="16%" height="40px" onClick={() => setOpen(true)}>변경</CustomButton>
                    </div>

                    <InputBox2 title="이름:" value={user?.empName || ""} readOnly direction="row" fontSize="23px" height="40px"/>
                    <InputBox2 title="부서:" value={user?.deptName || ""} readOnly direction="row" fontSize="23px" height="40px"/>

                    {/* 이메일 */}
                    <div style={{display: "flex", gap: "10px", alignItems: "center"}}>
                        <InputBox3
                            title="이메일:"
                            value={editingField === "empEmail" ? tempValue : user?.empEmail || ""}
                            readOnly={editingField !== "empEmail"}
                            direction="row"
                            fontSize="23px"
                            height="40px"
                            onChange={(e) => setTempValue(e.target.value)}
                        />
                        {editingField === "empEmail" ? (
                            <>
                                <CustomButton width="17%" height="40px" onClick={() => updateInfo("empEmail")}>저장</CustomButton>
                                <CustomButton width="17%" height="40px" color="red" onClick={() => {
                                    setEditingField(null);
                                    setTempValue("");
                                }}>취소</CustomButton>
                            </>
                        ) : (
                            <CustomButton width="16%" height="40px" onClick={() => {
                                setEditingField("empEmail");
                                setTempValue(user?.empEmail || "");
                            }}>변경</CustomButton>
                        )}
                    </div>

                    {/* 전화번호 */}
                    <div style={{display: "flex", gap: "10px", alignItems: "center"}}>
                        <InputBox3
                            title="전화번호:"
                            value={editingField === "empPhone" ? tempValue : user?.empPhone || ""}
                            readOnly={editingField !== "empPhone"}
                            direction="row"
                            fontSize="23px"
                            height="40px"
                            onChange={(e) => setTempValue(e.target.value)}
                        />
                        {editingField === "empPhone" ? (
                            <>
                                <CustomButton width="17%" height="40px" onClick={() => updateInfo("empPhone")}>저장</CustomButton>
                                <CustomButton width="17%" height="40px" color="red" onClick={() => {
                                    setEditingField(null);
                                    setTempValue("");
                                }}>취소</CustomButton>
                            </>
                        ) : (
                            <CustomButton width="16%" height="40px" onClick={() => {
                                setEditingField("empPhone");
                                setTempValue(user?.empPhone || "");
                            }}>변경</CustomButton>
                        )}
                    </div>

                    <InputBox2 title="생년월일:" value={user?.empBirthDate || ""} readOnly direction="row" fontSize="23px" height="40px"/>
                    <InputBox2 title="입사일:" value={user?.empHireDate || ""} readOnly direction="row" fontSize="23px" height="40px"/>
                </div>

                <Popup isOpen={open} onClose={() => setOpen(false)} width="35%" height="auto">
                    <ChangePass onSubmit={updatePassword} onClose={() => setOpen(false)}/>
                </Popup>
            </WhiteBox>
        </main>
    );
}

export default MyPage;
