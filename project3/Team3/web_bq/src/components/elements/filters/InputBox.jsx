function InputBox({
                      id, type = "text", margin = "10px", title, placeholder, fontSize = "18px", width = "180px",
                      setState, readOnly = false, value, min, max, step
                  }) {
    const styles = {width: width};

    if (readOnly) {
        styles.backgroundColor = "#E5E5E5";
    }

    return (
        <div className={"d-flex justify-content-center align-items-center input"}>
            <div className={"inputText"} style={{marginRight: margin, fontSize: fontSize}}>{title}</div>
            <input
                className={"inputBox"}
                style={styles}
                id={id}
                type={type}
                min={type === "number" ? min : undefined}
                max={type === "number" ? max : undefined}
                step={type === "number" ? step : undefined}
                placeholder={placeholder}
                onChange={(e) => setState(e.target.value)}
                readOnly={readOnly}
                value={value}
            />
        </div>
    )
}

export default InputBox