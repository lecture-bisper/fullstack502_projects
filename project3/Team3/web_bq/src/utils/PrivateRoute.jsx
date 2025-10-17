import {Navigate, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import useUserStore from "./useUserStore.jsx";

export default function PrivateRoute({minRoleId, children}) {
    const user = useUserStore(state => state.user);
    const navigate = useNavigate();
    const [alertShown, setAlertShown] = useState(false);

    useEffect(() => {
        if (!user) return;

        if (minRoleId !== undefined && user.roleId < minRoleId && !alertShown) {
            setAlertShown(true);
            // 다음 이벤트 루프로 넘겨서 alert 실행
            setTimeout(() => {
                alert("권한이 필요한 페이지입니다.");
                navigate("/", {replace: true});
            }, 0);
        }
    }, [user, minRoleId, alertShown, navigate]);

    if (!user) return <Navigate to="/login" replace/>;
    if (minRoleId !== undefined && user.roleId < minRoleId) return null;
    return children;
}
