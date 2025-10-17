// ManageUser.jsx
import {useState, useEffect} from "react";
import axios from "axios";
import FilterBar from "../../elements/FilterBar.jsx";
import Sheets from "../../elements/Sheets.jsx";
import InputBox from "../../elements/filters/InputBox.jsx";
import SelectBox from "../../elements/filters/SelectBox.jsx";
import DatePicker from "../../elements/filters/DatePicker.jsx";
import SearchButton from "../../elements/SearchButton.jsx";
import Bar from "../../elements/Bar.jsx";
import {BASE_URL, DEPT_NAME_OPTIONS, ROLE_NAME_OPTIONS} from "../../elements/constants/constants.js";
import CustomButton from "../../elements/CustomButton.jsx";
import Popup from "../../elements/Popup.jsx";
import Join from "./Join.jsx";
import ChangeRole from "./ChangeRole.jsx";

function ManageUser() {
    const [openRolePopup, setOpenRolePopup] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [newRole, setNewRole] = useState(null); // 팝업에서 선택할 권한
    const [originalData, setOriginalData] = useState([]);
    const [rowData, setRowData] = useState([]);
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({
        nameOrEmpCode: "",
        deptCode: "",
        roleName: "",
    });

    const columns = [
        {field: "index", headerName: "순번", flex: 0.6, align: "center"},
        {field: "empCode", headerName: "사번"},
        {field: "empName", headerName: "이름", flex: 0.7},
        {field: "deptName", headerName: "부서명"},
        {field: "roleDisplay", headerName: "직급", flex: 0.6},
        {field: "empEmail", headerName: "이메일", flex: 1.2},
        {field: "empPhone", headerName: "전화번호", align: "center", flex: 0.8},
        {field: "empBirthDate", headerName: "생년월일", align: "center", flex: 0.8},
        {field: "empHireDate", headerName: "입사일", align: "center", flex: 0.8},
        {
            field: "role",
            headerName: "권한변경",
            align: "center",
            renderCell: (params) => {
                const hasData = !!params.data?.empCode;
                if (!hasData) return null;
                return (
                    <CustomButton
                        width="100%"
                        onClick={() => {
                            setSelectedUser(params.data);
                            setOpenRolePopup(true);
                        }}
                    >
                        변경
                    </CustomButton>
                );
            },
        }
    ];

    useEffect(() => {
        setNewRole(null)
    }, [openRolePopup]);

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const res = await axios.get(BASE_URL + "/api/users", {withCredentials: true});
            const data = Array.isArray(res.data) ? res.data : [];
            const roleMap = {USER: "사원", MANAGER: "담당자", ADMIN: "관리자"};
            const mappedData = data.map((item, index) => ({
                ...item,
                index: index + 1,
                roleDisplay: roleMap[item.roleName] || item.roleName
            }));

            setOriginalData(mappedData);
            setRowData(mappedData);
        } catch (err) {
            console.error("사용자 조회 실패:", err.response?.data || err);
            alert("사용자 조회 실패");
        } finally {
            setLoading(false);
        }
    };


    useEffect(() => {
        fetchUsers();
    }, []);

    // 클라이언트 필터 적용
    const handleSearch = () => {
        setLoading(true);
        const filtered = originalData.filter(item => {
            const nameOrEmpCodeMatch =
                !filters.nameOrEmpCode ||
                item.empName.includes(filters.nameOrEmpCode) ||
                item.empCode.includes(filters.nameOrEmpCode);

            const deptMatch = !filters.deptCode || item.deptCode === filters.deptCode;
            const roleMatch = !filters.roleName || item.roleName === filters.roleName;

            const hireDate = new Date(item.empHireDate);
            const start = startDate ? new Date(startDate) : null;
            const end = endDate ? new Date(endDate) : null;
            const dateMatch = (!start || hireDate >= start) && (!end || hireDate <= end);

            return nameOrEmpCodeMatch && deptMatch && roleMatch && dateMatch;
        });

        const numberedData = filtered.map((item, index) => ({...item, index: index + 1}));
        setRowData(numberedData);
        setLoading(false);
    };

    const handleJoin = async ({empCode, userPwd}) => {
        try {
            await axios.post(
                BASE_URL + "/api/users",
                {empCode, userPwd},
                {withCredentials: true}
            );
            alert("회원 등록 완료");
            setOpen(false);       // 팝업 닫기
            fetchUsers();         // 사용자 목록 갱신
        } catch (err) {
            const errResponse = err.response?.data.errors;
            let errorMsg = "알 수 없는 오류가 발생했습니다."
            errResponse.forEach(err => {
                if (err === 'Already registered') errorMsg = "이미 등록된 사용자 입니다."
                else if (err === 'Employee not found') errorMsg = "일치하는 사원번호가 없습니다."
            })
            alert(errorMsg);
            console.error("회원 등록 실패:", err.response?.data || err);
        }
    };

    return (
        <main className="main">
            <h2 className="main-title">직원 관리 페이지</h2>
            <div style={{
                width: "100%",
                display: "flex",
                justifyContent: "center",
                flexDirection: "column",
                alignItems: "center"
            }}>
                <div style={{width: "90%", display: "flex", justifyContent: "flex-end", marginBottom: "3px"}}>
                    <CustomButton color={"gray"}><span style={{color: "black"}}
                                                       onClick={() => setOpen(true)}>회원등록</span></CustomButton>
                </div>
                <FilterBar>
                    <div className="filter-bar-scroll">
                        <InputBox
                            title="이름·사번"
                            value={filters.nameOrEmpCode}
                            setState={(val) => setFilters(prev => ({...prev, nameOrEmpCode: val}))}
                        />
                        <Bar/>
                        <SelectBox
                            title="부서"
                            options={DEPT_NAME_OPTIONS}
                            setState={(val) => setFilters(prev => ({...prev, deptCode: val}))}
                        />
                        <Bar/>
                        <SelectBox
                            title="직급"
                            options={ROLE_NAME_OPTIONS}
                            setState={(val) => setFilters(prev => ({...prev, roleName: val}))}
                        />
                        <Bar/>
                        <DatePicker
                            title="입사일"
                            setStartDate={setStartDate}
                            setEndDate={setEndDate}
                            value={{startDate, endDate}}
                        />
                    </div>
                    <SearchButton onClick={handleSearch}/>
                </FilterBar>
            </div>
            <Sheets rows={13} columns={columns} rowData={rowData} loading={loading}/>

            <Popup isOpen={open} onClose={() => setOpen(false)} width="35%" height="auto">
                <Join onSubmit={handleJoin} onClose={() => setOpen(false)}/>
            </Popup>
            <Popup isOpen={openRolePopup} onClose={() => setOpenRolePopup(false)} width="55%" height="auto">
                <ChangeRole
                    selectedUser={selectedUser}
                    newRole={newRole}
                    setNewRole={setNewRole}
                    onClose={() => setOpenRolePopup(false)}
                    fetchUsers={fetchUsers}
                />
            </Popup>
        </main>
    );
}

export default ManageUser;
