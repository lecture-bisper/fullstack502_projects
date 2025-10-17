import axios from "axios";
import useUserStore from "../../../utils/useUserStore.jsx";
import {BASE_URL} from "../../elements/constants/constants.js";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {InputBox2} from "../../elements/filters/index.js";
import {CustomButton, WhiteBox} from "../../elements/index.js";

function Login() {
    const navigate = useNavigate();
    const setUser = useUserStore((state) => state.setUser);
    const clearUser = useUserStore((state) => state.clearUser);

    // state 관리 (배열 구조 분해!)
    const [empCode, setEmpCode] = useState("");
    const [userPwd, setUserPwd] = useState("");
    const [autoLogin, setAutoLogin] = useState(false);

    const handleLogin = async () => {
        const authDto = {empCode: empCode.trim(), userPwd: userPwd.trim()};

        try {
            // 1. 로그인
            await axios.post(
                BASE_URL + "/api/auth/web-login",
                authDto,
                {params: {autoLogin: autoLogin}, withCredentials: true}
            );
            // 2. 로그인 성공 후 바로 내 정보 요청
            const userRes = await axios.get(
                BASE_URL + "/api/users/me",
                {withCredentials: true}
            );
            setUser(userRes.data);
            localStorage.setItem("isLoggedIn", "true");
            navigate("/"); // 페이지 이동
        } catch (err) {
            clearUser()
            console.error(err.response?.data || err);
            localStorage.setItem("isLoggedIn", "false");
            alert(`로그인 실패: ${err.response?.data.errors || "알 수 없는 오류"}`);
        }
    };


    return (
        <main className={"main"}>
            <p className={"main-title"}>비품 관리 시스템</p>
            <WhiteBox width={"35%"}>
                <p className={"main-title"} style={{color: "#0058DB"}}>로그인</p>
                <InputBox2
                    title={"사번"}
                    value={empCode}
                    width={"70%"}
                    onChange={(e) => setEmpCode(e.target.value)}
                />
                <InputBox2
                    title={"비밀번호"}
                    type="password"
                    width={"70%"}
                    value={userPwd}
                    onChange={(e) => setUserPwd(e.target.value)}
                />
                <div style={{display: "flex", width: "70%", justifyContent: "space-between", marginBottom: "30px"}}>
                    <div style={{display: "flex", alignItems: "center", gap: "5px"}}>
                        <input id={"cb-auto-login"} type="checkbox" style={{width: "15px", height: "15px"}} checked={autoLogin}
                               onChange={(e) => setAutoLogin(e.target.checked)}/>
                        <label htmlFor={"cb-auto-login"}>자동로그인</label>
                    </div>
                </div>
                <CustomButton height={"50px"} width={"50%"} onClick={handleLogin}>
                    <span className={"login-btn"}>로그인</span>
                </CustomButton>
                <div style={{marginBottom: "15px"}}></div>
            </WhiteBox>
        </main>
    );
}

export default Login;
