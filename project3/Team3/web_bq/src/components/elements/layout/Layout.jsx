import { useState } from "react";
import Header from "/src/components/elements/layout/Header.jsx";
import SideBar from "/src/components/elements/layout/SideBar.jsx";
import useUserStore from "../../../utils/useUserStore.jsx";

function Layout() {
    const user = useUserStore(state => state.user);
    const clearUser = useUserStore(state => state.clearUser);
    const [showSidebar, setShowSidebar] = useState(false);

    return (
        <>
            <Header
                user={user}
                clearUser={clearUser}
                onMenuClick={() => setShowSidebar(prev => !prev)}
            />

            {/* 사이드바 배경 클릭 시 닫기 */}
            {showSidebar && (
                <div
                    className="sidebar-overlay"
                    onClick={() => setShowSidebar(false)}
                />
            )}

            {/* 슬라이드 메뉴 */}
            <SideBar
                isLoggedIn={!!user}
                show={showSidebar}
                role={user?.roleName}
            />
        </>
    );
}

export default Layout;
