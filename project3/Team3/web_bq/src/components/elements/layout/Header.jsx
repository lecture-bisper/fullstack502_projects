import {useNavigate} from "react-router-dom";
import menu from "/src/assets/menu.png";
import logo from "/src/assets/Logo.webp";
import CustomButton from "../CustomButton.jsx";
import axios from "axios";
import {BASE_URL} from "../constants/constants.js";

function Header({user, clearUser, onMenuClick}) {
    const navigate = useNavigate();

    const handleLoginClick = () => {
        navigate("/login");
    };

    const handleLogoutClick = () => {
        axios.post(BASE_URL + "/api/auth/web-logout", {}, {withCredentials: true})
            .catch(err => console.error(`${err.response.data.error}, ${err.response.data.message}`))
            .finally(() => {
                    clearUser()
                    localStorage.setItem("isLoggedIn", "false");
                    navigate("/")
                }
            )
    }

    return (
        <header className="header">
            <button className="header_menu_btn" onClick={onMenuClick}>
                <img className="header_menu_img" src={menu} alt="메뉴"/>
            </button>
            <img className="header_logo_img" src={logo} alt="로고" onClick={() => navigate("/")}/>

            <div className="header_right">
                {user ? <>
                    <span className={"header_text"}>{`${user.empName}님 환영합니다.`}</span>
                    <CustomButton color="gray" style={{cursor: "pointer", marginLeft: "10px"}} onClick={handleLogoutClick}>
                        <span style={{color: "#242424", fontSize: '16px', fontWeight: 600,}}>로그아웃</span>
                    </CustomButton>
                </> : <CustomButton onClick={handleLoginClick} style={{cursor: "pointer", marginLeft: "10px"}}><span>로그인</span></CustomButton>}
            </div>
        </header>
    );
}

export default Header;
