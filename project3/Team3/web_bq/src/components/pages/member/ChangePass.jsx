import { InputBox2 } from "../../elements/filters/index.js";
import { useState } from "react";
import CustomButton from "../../elements/CustomButton.jsx";

function ChangePass({ onSubmit, onClose }) {
    const [curPassword, setCurPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPwd, setConfirmPwd] = useState("");

    const handleSubmit = () => {
        if (!curPassword || !newPassword || !confirmPwd) {
            alert("모든 칸을 입력하세요.");
            return;
        }
        if (newPassword !== confirmPwd) {
            alert("새 비밀번호가 일치하지 않습니다.");
            return;
        }
        onSubmit({ curPassword, newPassword });
    };

    return (
        <>
            <h2 className="main-title" style={{ color: "#247CFF", marginBottom: "20px" }}>
                비밀번호 변경
            </h2>

            <InputBox2
                title="기존 비밀번호"
                type="password"
                width="70%"
                value={curPassword}
                onChange={(e) => setCurPassword(e.target.value)}
            />
            <InputBox2
                title="새 비밀번호"
                type="password"
                width="70%"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
            />
            <InputBox2
                title="새 비밀번호 확인"
                type="password"
                width="70%"
                value={confirmPwd}
                onChange={(e) => setConfirmPwd(e.target.value)}
            />

            <div className="itemDetailBtnBox" style={{ marginTop: "20px", display: "flex", gap: "10px" }}>
                <CustomButton height="40px" width="70px" onClick={handleSubmit}>
                    변경
                </CustomButton>
                <CustomButton height="40px" width="70px" color="red" onClick={onClose}>
                    취소
                </CustomButton>
            </div>
        </>
    );
}

export default ChangePass;
