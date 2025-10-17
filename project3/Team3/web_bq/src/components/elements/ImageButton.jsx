import React from "react";

function ImageButton({link = "#", ImageUrl = "", alt = "Image-Button", width = "24px", height = "24px"}) {

    return (
        <a href={link} target="_blank" rel="noopener noreferrer">
            <img src={ImageUrl} style={{width: width, height: height}} alt={alt}/>
        </a>
    );
}

export default ImageButton