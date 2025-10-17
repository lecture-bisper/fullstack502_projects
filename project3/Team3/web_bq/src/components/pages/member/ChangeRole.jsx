// /ChangeRole.jsx
import axios from "axios";
import {BASE_URL} from "../../elements/constants/constants.js";
import {InputBox, SelectBox} from "../../elements/filters/index.js";
import {CustomButton} from "../../elements/index.js";

function ChangeRole({selectedUser, newRole, setNewRole, onClose, fetchUsers}) {
    if (!selectedUser) return null;

    const ROLE_OPTIONS = [
        {value: "USER", label: "사원"},
        {value: "MANAGER", label: "담당자"},
        {value: "ADMIN", label: "관리자"}
    ]

    const handleSubmit = async () => {
        try {
            await axios.put(
                `${BASE_URL}/api/users/${selectedUser.empCode}`,
                {},
                {
                    params: {roleName: newRole},
                    withCredentials: true,
                }
            );
            alert("권한 변경 완료");
            onClose();
            fetchUsers();
        } catch (err) {
            console.error("권한 변경 실패:", err.response?.data || err);
            alert("권한 변경 실패");
        }
    };

    return (
        <>
            <h2 className="main-title" style={{color: "#247CFF", marginBottom: "20px"}}>
                권한 변경
            </h2>
            <div className={"user-form-row"} style={{width: "100%", display: "flex", justifyContent: "center", alignItems: "center", gap: "10px"}}>
                <InputBox title={"부서"} value={selectedUser.deptName} readOnly={true}/>
                <InputBox title={"이름"} value={selectedUser.empName} readOnly={true}/>
                <InputBox title={"사번"} value={selectedUser.empCode} readOnly={true}/>
                <SelectBox
                    title="권한 선택"
                    options={ROLE_OPTIONS}
                    setState={setNewRole}
                    value={newRole ? newRole : ROLE_OPTIONS.find(opt => opt.value === selectedUser.roleName).value}
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

export default ChangeRole;
