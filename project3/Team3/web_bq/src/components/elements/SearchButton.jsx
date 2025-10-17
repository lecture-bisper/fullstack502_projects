// / SearchButton.jsx
import Search from "/src/assets/Search.png"

import {Button} from "react-bootstrap";

function SearchButton({onClick}) {

    return (
        <Button className={"search-btn"} style={{padding: "0px"}} onClick={onClick}>
            <img style={{width: "30px", height: "30px"}} src={Search} alt={"검색"}/>
        </Button>
    )

}

export default SearchButton