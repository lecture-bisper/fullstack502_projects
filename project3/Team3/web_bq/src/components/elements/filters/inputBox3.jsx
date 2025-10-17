function InputBox3({
                       id,
                       type = "text",
                       margin = "10px",
                       title,
                       placeholder,
                       fontSize = "18px",
                       width = "100%",
                       height = "50px",
                       setState,
                       onChange,
                       readOnly,
                       value,
                       direction = "column",
                   }) {

    const styles = {height: height}

    if (readOnly) {
        styles.backgroundColor = "#E5E5E5";
    }

    return (
        <div
            className="d-flex justify-content-center"
            style={{
                flexDirection: direction,
                width: width,
                alignItems: direction === "row" ? "center" : "flex-start", // 🔹 row일 때 중앙 정렬
            }}
        >
            <div
                className="inputText"
                style={{
                    marginRight: direction === "row" ? margin : "0",
                    marginBottom: direction === "column" ? margin : "0",
                    fontSize: fontSize,
                    whiteSpace: "nowrap",
                }}
            >
                {title}
            </div>
            <input
                className="inputBox2"
                id={id}
                type={type}
                placeholder={placeholder}
                onChange={onChange || ((e) => setState?.(e.target.value))}
                readOnly={readOnly}
                value={value}
                style={styles}
            />
        </div>
    );
}


export default InputBox3;
