package bitc.full502.app_bq.data.model

data class UserDto(
    val deptId: Long,
    val deptCode: String?,
    val deptName: String?,

    val empId: Long,
    val empCode: String,
    val empName: String,
    val empEmail: String?,
    val empPhone: String?,
    val empBirthDate: String?,   // LocalDate → String("yyyy-MM-dd")
    val empHireDate: String?,    // LocalDate → String

    val userId: Long,
    val userStatus: String?,
    val userCreateDate: String?, // LocalDateTime → String("yyyy-MM-dd'T'HH:mm:ss")

    val roleId: Long,
    val roleName: String?,
    val roleStockIn: Char,
    val roleUpdateMinStock: Char,
    val roleAddItem: Char,
    val roleApproveItem: Char,
    val roleUpdateUserInfo: Char
)
