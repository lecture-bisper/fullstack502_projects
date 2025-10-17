// / Join.jsx


import InputBox2 from "../../elements/filters/InputBox2.jsx";
import CustomButton from "../../elements/CustomButton.jsx";
import {useState} from "react";

function Join({ onSubmit, onClose }) {
    const [empCode, setEmpCode] = useState("");
    const [userPwd, setUserPwd] = useState("");
    const [confirmPwd, setConfirmPwd] = useState("");

    const handleSubmit = () => {
        if (!empCode || !userPwd || !confirmPwd) {
            alert("모든 칸을 입력하세요.");
            return;
        }
        if (userPwd !== confirmPwd) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }
        onSubmit({ empCode, userPwd }); // 부모로 전달
    };

    return (
        <>
            <h2 className="main-title" style={{ color: "#247CFF", marginBottom: "20px" }}>
                사원등록
            </h2>
            <InputBox2
                title="사원번호"
                type="text"               // ← 수정
                width="70%"
                value={empCode}
                onChange={(e) => setEmpCode(e.target.value)}
            />
            <InputBox2
                title="비밀번호"
                type="password"
                width="70%"
                value={userPwd}
                onChange={(e) => setUserPwd(e.target.value)}
            />
            <InputBox2
                title="비밀번호 확인"
                type="password"
                width="70%"
                value={confirmPwd}
                onChange={(e) => setConfirmPwd(e.target.value)}
            />

            <div style={{ marginTop: 20, display: "flex", gap: 10 }}>
                <CustomButton height="40px" width="70px" onClick={handleSubmit}>등록</CustomButton>
                <CustomButton height="40px" width="70px" color="red" onClick={onClose}>취소</CustomButton>
            </div>
        </>
    );
}


export default Join