import React from "react";

function WhiteBox({ width = "50%", height = "auto", children }) {
    const style = {
        width: typeof width === "number" ? `${width}px` : width,
        height: typeof height === "number" ? `${height}px` : height,
    };

    return (
        <div className="whiteBox" style={style}>
            {children}
        </div>
    );
}

export default WhiteBox
