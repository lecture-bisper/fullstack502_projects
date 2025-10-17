// SideBar.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import sidebar_arrow from "../../../assets/sidebar_arrow.png";

function SideBarSection({ title, items, defaultOpen = false }) {
    const [open, setOpen] = useState(defaultOpen);

    return (
        <li className={`sidebar-subtitle ${open ? "is-open" : ""}`}>
            <button
                type="button"
                className="section-button"
                onClick={() => setOpen(prev => !prev)}
                aria-expanded={open}
            >
                {title}
                <img src={sidebar_arrow} className="sidebar-arrow" alt="arrow" />
            </button>
            <ul className={`sidebar-list ${open ? "open" : "closed"}`}>
                {items.map(t => (
                    <li key={t.label} onClick={t.onClick} className="sidebar-item">
                        <span style={{ cursor: "pointer" }}>
                            - {t.label}
                        </span>
                    </li>
                ))}
            </ul>

        </li>
    );
}

// 권한별 메뉴 정의
const menuByRole = {
    USER: {
        "비품관리": [
            { label: "비품 목록", path: "/items" },
            { label: "창고별 재고 현황", path: "/stocks/wh" },
            { label: "입출고 현황", path: "/stocks/log" },
        ],
    },
    MANAGER: {
        "비품관리": [
            { label: "비품 목록", path: "/items" },
            { label: "창고별 재고 현황", path: "/stocks/wh" },
            { label: "입출고 현황", path: "/stocks/log" },
            { label: "비품 발주 요청", path: "/items/order" },
            { label: "신규 비품 요청 현황", path: "/items/new" },
        ],
        "사용·통계": [
            { label: "직원·비품 사용현황", path: "/stats/emp" },
            { label: "기간·부서별 사용금액", path: "/stats/cost" },
        ],
    },
    ADMIN: {
        "비품관리": [
            { label: "비품 목록", path: "/items" },
            { label: "창고별 재고 현황", path: "/stocks/wh" },
            { label: "입출고 현황", path: "/stocks/log" },
            { label: "비품 발주 요청", path: "/items/order" },
            { label: "비품 발주 요청 현황", path: "/items/orders" },
            { label: "신규 비품 요청 현황", path: "/items/new" },
        ],
        "사용·통계": [
            { label: "직원·비품 사용현황", path: "/stats/emp" },
            { label: "기간·부서별 사용금액", path: "/stats/cost" },
        ],
        "회원·권한 관리": [
            { label: "회원관리", path: "/admin/users" },
        ],
    },
};

function SideBar({ isLoggedIn = false, show = false, role = "USER" }) {
    const navigate = useNavigate();
    const currentMenu = menuByRole[role] || menuByRole.USER;

    // 클릭 시 navigate 연결
    const menuWithNavigation = {};
    for (const [section, items] of Object.entries(currentMenu)) {
        menuWithNavigation[section] = items.map(item => ({
            ...item,
            onClick: () => navigate(item.path)
        }));
    }

    return (
        <div className={`sidebar-panel ${show ? "show" : ""}`}>
            {isLoggedIn ? (
                <ul className="sidebar-list">
                    {/* 항상 보이는 마이페이지 */}
                    <li className="sidebar-mainTitle">
                        <span style={{ cursor: "pointer" }} onClick={() => navigate("/me")}>
                            마이페이지
                        </span>
                    </li>

                    {/* 권한별 메뉴 */}
                    {Object.entries(menuWithNavigation).map(([sectionTitle, items]) => (
                        <SideBarSection
                            key={sectionTitle}
                            title={sectionTitle}
                            items={items}
                        />
                    ))}
                </ul>
            ) : (
                <span className={"sidebar-mainTitle"} style={{ cursor: "pointer" }} onClick={() => navigate("/login")}>
                        로그인
                </span>
            )}
        </div>
    );
}

export default SideBar;
