package bitc.full502.lostandfound.util

object Constants {

//    const val BASE_URL = "http://10.100.202.63:8080/api/"
//    const val WEBSOCKET_URL = "ws://10.100.202.63:8080/chat"
//    const val POST_CODE_URL = "http://10.100.202.63:8080/postcode"
//    const val IMAGE_BASE_URL = "http://10.100.202.63:8080/upload/board/"

    const val BASE_URL = "http://10.100.202.32:8080/api/"
    const val WEBSOCKET_URL = "ws://10.100.202.32:8080/chat"
    const val POST_CODE_URL = "http://10.100.202.32:8080/postcode"
    const val IMAGE_BASE_URL = "http://10.100.202.32:8080/upload/board/"

    const val TYPE_LOST: String = "LOST"
    const val TYPE_FOUND: String = "FOUND"

    const val STATUS_PENDING: String = "PENDING"
    const val STATUS_COMPLETE: String = "COMPLETE"
    const val STATUS_CANCEL: String = "CANCEL"

    const val CATEGORY_ELECTRONICS: Int = 0
    const val CATEGORY_WALLET_AND_BAG: Int = 1
    const val CATEGORY_ID_AND_CARD: Int = 2
    const val CATEGORY_KEY: Int = 3
    const val CATEGORY_CLOTHES_AND_ACCESSORY: Int = 4
    const val CATEGORY_DOCUMENT: Int = 5
    const val CATEGORY_VALUABLE: Int = 6
    const val CATEGORY_OTHERS: Int = 7

    const val ROLE_USER: String = "USER"
    const val ROLE_ADMIN: String = "ADMIN"
    const val ROLE_OFFICIAL: String = "OFFICIAL"

    const val SUCCESS: String = "SUCCESS"
    const val FAILURE: String = "FAILURE"
    const val NULL_OR_INVALID_FORMAT: String = "NULL_OR_INVALID_FORMAT"

    const val EXIST: String = "EXIST"
    const val NOT_EXIST: String = "NOT_EXIST"

    const val EXTRA_MODE = "extra_mode"
    const val MODE_EDIT = "edit"
    const val MODE_CREATE = "create"

    const val EXTRA_BOARD_ID = "extra_board_id"
    const val EXTRA_BOARD_DATA = "extra_board_data"


}